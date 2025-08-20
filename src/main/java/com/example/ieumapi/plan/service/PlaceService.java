package com.example.ieumapi.plan.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.plan.domain.Place;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.dto.place.CreatePlaceRequest;
import com.example.ieumapi.plan.dto.place.PlaceDto;
import com.example.ieumapi.plan.dto.place.UpdatePlaceRequest;
import com.example.ieumapi.plan.exception.PlaceError;
import com.example.ieumapi.plan.exception.PlaceException;
import com.example.ieumapi.plan.repository.PlaceRepository;
import com.example.ieumapi.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlanRepository planRepository;
    private final GroupMemberRepository groupMemberRepository;

    /* 권한 규칙
       - 개인 플랜: 소유자만 읽기/쓰기
       - 그룹 플랜: 그룹 멤버는 읽기 가능, '아이템'(Place) 작성/수정/삭제 가능
     */
    @Transactional(readOnly = true)
    public CursorPageResponse<PlaceDto> list(Long planId, String query, int size, String cursor) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        long cursorId = decodeCursor(cursor);
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "placeId"));

        List<Place> entities = (query == null || query.isBlank())
                ? placeRepository.findByPlanAndPlaceIdLessThanOrderByPlaceIdDesc(plan, cursorId, pageable)
                : placeRepository.findByPlanAndNameContainingIgnoreCaseAndPlaceIdLessThanOrderByPlaceIdDesc(
                plan, query.trim(), cursorId, pageable);

        boolean hasNext = entities.size() > size;
        if (hasNext) entities = entities.subList(0, size);

        List<PlaceDto> data = entities.stream().map(this::toDto).collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = entities.get(entities.size() - 1).getPlaceId();
            nextCursor = encodeCursor(lastId);
        }
        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    public PlaceDto create(Long planId, CreatePlaceRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Place place = Place.builder()
                .plan(plan)
                .name(req.getName().trim())
                .address(req.getAddress())
                .lat(req.getLat())
                .lng(req.getLng())
                .memo(req.getMemo())
                .build();
        Place saved = placeRepository.save(place);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public PlaceDto get(Long planId, Long placeId) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(PlaceError.PLACE_NOT_FOUND));
        if (!place.getPlan().getPlanId().equals(planId)) {
            throw new PlaceException(PlaceError.PLACE_NOT_FOUND);
        }
        return toDto(place);
    }

    public PlaceDto update(Long planId, Long placeId, UpdatePlaceRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(PlaceError.PLACE_NOT_FOUND));
        if (!place.getPlan().getPlanId().equals(planId)) {
            throw new PlaceException(PlaceError.PLACE_NOT_FOUND);
        }

        place.update(
                req.getName(),
                req.getAddress(),
                req.getLat(),
                req.getLng(),
                req.getMemo()
        );
        Place updated = placeRepository.save(place);
        return toDto(updated);
    }

    public void delete(Long planId, Long placeId) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(PlaceError.PLACE_NOT_FOUND));
        if (!place.getPlan().getPlanId().equals(planId)) {
            throw new PlaceException(PlaceError.PLACE_NOT_FOUND);
        }
        placeRepository.delete(place);
    }

    // ---------------------- util ----------------------

    private Plan getPlanOrThrow(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlaceException(PlaceError.PLAN_NOT_FOUND));
    }

    private void assertReadable(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new PlaceException(PlaceError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new PlaceException(PlaceError.FORBIDDEN);
        }
    }

    private void assertWritableItem(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new PlaceException(PlaceError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new PlaceException(PlaceError.FORBIDDEN);
        }
    }

    private PlaceDto toDto(Place p) {
        return new PlaceDto(
                p.getPlaceId(),
                p.getPlan().getPlanId(),
                p.getName(),
                p.getAddress(),
                p.getLat(),
                p.getLng(),
                p.getMemo(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return Long.MAX_VALUE;
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            return Long.MAX_VALUE;
        }
    }

    private String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
    }
}
