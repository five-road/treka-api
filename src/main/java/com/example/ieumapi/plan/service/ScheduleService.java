package com.example.ieumapi.plan.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.domain.Schedule;
import com.example.ieumapi.plan.dto.schedule.CreateScheduleRequest;
import com.example.ieumapi.plan.dto.schedule.ScheduleDto;
import com.example.ieumapi.plan.dto.schedule.UpdateScheduleRequest;
import com.example.ieumapi.plan.exception.ScheduleError;
import com.example.ieumapi.plan.exception.ScheduleException;
import com.example.ieumapi.plan.repository.PlanRepository;
import com.example.ieumapi.plan.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PlanRepository planRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ---------- C ----------
    public ScheduleDto create(Long planId, CreateScheduleRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        if (!req.getStartAt().isBefore(req.getEndAt())) {
            throw new ScheduleException(ScheduleError.INVALID_TIME_RANGE);
        }

        Schedule saved = scheduleRepository.save(
                Schedule.builder()
                        .plan(plan)
                        .title(req.getTitle().trim())
                        .description(req.getDescription())
                        .startAt(req.getStartAt())
                        .endAt(req.getEndAt())
                        .placeName(req.getPlaceName())
                        .address(req.getAddress())
                        .lat(req.getLat())
                        .lng(req.getLng())
                        .build()
        );
        return toDto(saved);
    }

    // ---------- R: list (cursor by scheduleId DESC) ----------
    @Transactional(readOnly = true)
    public CursorPageResponse<ScheduleDto> list(Long planId, LocalDate date, int size, String cursor) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        long cursorId = decodeCursor(cursor);
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "scheduleId"));

        List<Schedule> list;
        if (date != null) {
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.plusDays(1).atStartOfDay();
            list = scheduleRepository.findByPlanAndStartAtBetweenAndScheduleIdLessThanOrderByScheduleIdDesc(
                    plan, from, to, cursorId, pageable
            );
        } else {
            list = scheduleRepository.findByPlanAndScheduleIdLessThanOrderByScheduleIdDesc(
                    plan, cursorId, pageable
            );
        }

        boolean hasNext = list.size() > size;
        if (hasNext) list = list.subList(0, size);

        List<ScheduleDto> data = list.stream().map(this::toDto).collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = list.get(list.size() - 1).getScheduleId();
            nextCursor = encodeCursor(lastId);
        }
        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    // ---------- R: get one ----------
    @Transactional(readOnly = true)
    public ScheduleDto get(Long planId, Long scheduleId) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND));
        if (!schedule.getPlan().getPlanId().equals(planId)) {
            throw new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND);
        }
        return toDto(schedule);
    }

    // ---------- U ----------
    public ScheduleDto update(Long planId, Long scheduleId, UpdateScheduleRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND));
        if (!schedule.getPlan().getPlanId().equals(planId)) {
            throw new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND);
        }

        // 시간 유효성: 변경 후에도 start < end
        LocalDateTime newStart = (req.getStartAt() != null) ? req.getStartAt() : schedule.getStartAt();
        LocalDateTime newEnd   = (req.getEndAt() != null)   ? req.getEndAt()   : schedule.getEndAt();
        if (!newStart.isBefore(newEnd)) {
            throw new ScheduleException(ScheduleError.INVALID_TIME_RANGE);
        }

        schedule.update(
                req.getTitle(),
                req.getDescription(),
                req.getStartAt(),
                req.getEndAt(),
                req.getPlaceName(),
                req.getAddress(),
                req.getLat(),
                req.getLng()
        );
        Schedule updated = scheduleRepository.save(schedule);
        return toDto(updated);
    }

    // ---------- D ----------
    public void delete(Long planId, Long scheduleId) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND));
        if (!schedule.getPlan().getPlanId().equals(planId)) {
            throw new ScheduleException(ScheduleError.SCHEDULE_NOT_FOUND);
        }
        scheduleRepository.delete(schedule);
    }

    // ---------- utils ----------
    private ScheduleDto toDto(Schedule s) {
        return new ScheduleDto(
                s.getScheduleId(),
                s.getPlan().getPlanId(),
                s.getTitle(),
                s.getDescription(),
                s.getStartAt(),
                s.getEndAt(),
                s.getPlaceName(),
                s.getAddress(),
                s.getLat(),
                s.getLng(),
                s.getCreatedAt(),
                s.getUpdatedAt()
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

    private Plan getPlanOrThrow(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ScheduleException(ScheduleError.PLAN_NOT_FOUND));
    }

    private void assertReadable(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new ScheduleException(ScheduleError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new ScheduleException(ScheduleError.FORBIDDEN);
        }
    }

    private void assertWritableItem(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new ScheduleException(ScheduleError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new ScheduleException(ScheduleError.FORBIDDEN);
        }
    }
}
