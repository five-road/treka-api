package com.example.ieumapi.plan.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.group.repository.GroupRepository;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.dto.plan.CreatePlanRequest;
import com.example.ieumapi.plan.dto.plan.PlanDto;
import com.example.ieumapi.plan.dto.plan.UpdatePlanRequest;
import com.example.ieumapi.plan.exception.PlanError;
import com.example.ieumapi.plan.exception.PlanException;
import com.example.ieumapi.plan.repository.PlanRepository;
import com.example.ieumapi.user.domain.User;
import com.example.ieumapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ----------------- Create -----------------
    public PlanDto create(CreatePlanRequest req) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new PlanException(PlanError.INVALID_TITLE);
        }
        if (!req.getStartDate().isBefore(req.getEndDate()) && !req.getStartDate().isEqual(req.getEndDate())) {
            // start <= end 허용(당일치기)
            throw new PlanException(PlanError.INVALID_DATE_RANGE);
        }

        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new PlanException(PlanError.FORBIDDEN));

        Group group = null;
        if (req.getGroupId() != null) {
            group = groupRepository.findById(req.getGroupId())
                    .orElseThrow(() -> new PlanException(PlanError.GROUP_NOT_FOUND));
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(group.getGroupId(), currentUserId);
            if (!isMember) throw new PlanException(PlanError.FORBIDDEN);
        }

        Plan saved = planRepository.save(
                Plan.builder()
                        .owner(owner)
                        .group(group)
                        .title(req.getTitle().trim())
                        .description(req.getDescription())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .locationName(req.getLocationName())
                        .build()
        );
        return toDto(saved);
    }

    // ----------------- List: 내 개인 플랜 -----------------
    @Transactional(readOnly = true)
    public CursorPageResponse<PlanDto> listMyPlans(int size, String cursor, String q) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new PlanException(PlanError.FORBIDDEN));

        long cursorMillis = decodeCursor(cursor);
        long clampMillis = Math.min(cursorMillis, System.currentTimeMillis());
        LocalDateTime cursorTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(clampMillis),
                ZoneOffset.systemDefault()
        );

        Pageable pageable = PageRequest.of(0, size + 1);

        List<Plan> list = (q == null || q.isBlank())
                ? planRepository.findByOwnerAndGroupIsNullAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(me, cursorTime, pageable)
                : planRepository.findByOwnerAndGroupIsNullAndTitleContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(me, q.trim(), cursorTime, pageable);

        boolean hasNext = list.size() > size;
        if (hasNext) list = list.subList(0, size);

        List<PlanDto> data = list.stream().map(this::toDto).collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            long lastMillis = list.get(list.size() - 1)
                    .getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastMillis).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    // ----------------- List: 특정 그룹의 플랜 -----------------
    @Transactional(readOnly = true)
    public CursorPageResponse<PlanDto> listGroupPlans(Long groupId, int size, String cursor, String q) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new PlanException(PlanError.GROUP_NOT_FOUND));

        boolean isMember = groupMemberRepository.existsByGroupGroupIdAndUserId(groupId, currentUserId);
        if (!isMember) throw new PlanException(PlanError.FORBIDDEN);

        long cursorMillis = decodeCursor(cursor);
        long clampMillis = Math.min(cursorMillis, System.currentTimeMillis());
        LocalDateTime cursorTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(clampMillis),
                ZoneOffset.systemDefault()
        );

        Pageable pageable = PageRequest.of(0, size + 1);
        List<Plan> list = (q == null || q.isBlank())
                ? planRepository.findByGroupAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(group, cursorTime, pageable)
                : planRepository.findByGroupAndTitleContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(group, q.trim(), cursorTime, pageable);

        boolean hasNext = list.size() > size;
        if (hasNext) list = list.subList(0, size);

        List<PlanDto> data = list.stream().map(this::toDto).collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            long lastMillis = list.get(list.size() - 1)
                    .getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = Base64.getEncoder()
                    .encodeToString(String.valueOf(lastMillis).getBytes(StandardCharsets.UTF_8));
        }

        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    // ----------------- Get One -----------------
    @Transactional(readOnly = true)
    public PlanDto get(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanException(PlanError.PLAN_NOT_FOUND));
        assertReadable(plan, SecurityUtils.getCurrentUserId());
        return toDto(plan);
    }

    // ----------------- Update -----------------
    public PlanDto update(Long planId, UpdatePlanRequest req) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanException(PlanError.PLAN_NOT_FOUND));
        assertPlanUpdatable(plan, SecurityUtils.getCurrentUserId());

        LocalDate newStart = req.getStartDate() != null ? req.getStartDate() : plan.getStartDate();
        LocalDate newEnd   = req.getEndDate()   != null ? req.getEndDate()   : plan.getEndDate();
        if (!newStart.isBefore(newEnd) && !newStart.isEqual(newEnd)) {
            throw new PlanException(PlanError.INVALID_DATE_RANGE);
        }
        if (req.getTitle() != null && req.getTitle().trim().isEmpty()) {
            throw new PlanException(PlanError.INVALID_TITLE);
        }

        plan.update(
                req.getTitle(),
                req.getDescription(),
                req.getStartDate(),
                req.getEndDate(),
                req.getLocationName()
        );
        Plan updated = planRepository.save(plan);
        return toDto(updated);
    }

    // ----------------- Delete -----------------
    public void delete(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanException(PlanError.PLAN_NOT_FOUND));
        assertPlanUpdatable(plan, SecurityUtils.getCurrentUserId());
        planRepository.delete(plan);
    }

    // --------- 권한/유틸 ---------
    private void assertReadable(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) throw new PlanException(PlanError.FORBIDDEN);
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new PlanException(PlanError.FORBIDDEN);
        }
    }

    /** 플랜 자체 수정/삭제는 소유자 or 그룹 오너만 */
    private void assertPlanUpdatable(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) throw new PlanException(PlanError.FORBIDDEN);
        } else {
            boolean owner = plan.getOwner().getUserId().equals(userId);
            boolean groupOwner = plan.getGroup().getOwner().getUserId().equals(userId);
            if (!(owner || groupOwner)) throw new PlanException(PlanError.FORBIDDEN);
        }
    }

    private PlanDto toDto(Plan p) {
        return new PlanDto(
                p.getPlanId(),
                p.getOwner().getUserId(),
                p.getGroup() != null ? p.getGroup().getGroupId() : null,
                p.getTitle(),
                p.getDescription(),
                p.getStartDate(),
                p.getEndDate(),
                p.getLocationName(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return System.currentTimeMillis();
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return Long.parseLong(decoded);
        } catch (IllegalArgumentException e) {
            return System.currentTimeMillis();
        }
    }
}
