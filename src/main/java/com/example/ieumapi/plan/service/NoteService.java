package com.example.ieumapi.plan.service;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.util.SecurityUtils;
import com.example.ieumapi.group.repository.GroupMemberRepository;
import com.example.ieumapi.plan.domain.Note;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.dto.note.CreateNoteRequest;
import com.example.ieumapi.plan.dto.note.NoteDto;
import com.example.ieumapi.plan.dto.note.UpdateNoteRequest;
import com.example.ieumapi.plan.exception.NoteError;
import com.example.ieumapi.plan.exception.NoteException;
import com.example.ieumapi.plan.repository.NoteRepository;
import com.example.ieumapi.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final PlanRepository planRepository;
    private final GroupMemberRepository groupMemberRepository;

    /* 권한 규칙
       - 개인 플랜: 소유자만 읽기/쓰기
       - 그룹 플랜: 그룹 멤버는 읽기 가능, 아이템(Note) 생성/수정/삭제 가능
     */

    public NoteDto create(Long planId, CreateNoteRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Note saved = noteRepository.save(
                Note.builder()
                        .plan(plan)
                        .content(req.getContent().trim())
                        .date(req.getDate())
                        .pinned(req.isPinned())
                        .build()
        );
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<NoteDto> list(Long planId, LocalDate date, boolean pinnedOnly, int size, String cursor) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        long cursorId = decodeCursor(cursor);
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "noteId"));

        List<Note> notes;
        if (pinnedOnly) {
            notes = noteRepository.findByPlanAndPinnedAndNoteIdLessThanOrderByNoteIdDesc(
                    plan, true, cursorId, pageable);
        } else if (date != null) {
            notes = noteRepository.findByPlanAndDateAndNoteIdLessThanOrderByNoteIdDesc(
                    plan, date, cursorId, pageable);
        } else {
            notes = noteRepository.findByPlanAndNoteIdLessThanOrderByNoteIdDesc(
                    plan, cursorId, pageable);
        }

        boolean hasNext = notes.size() > size;
        if (hasNext) notes = notes.subList(0, size);

        List<NoteDto> data = notes.stream().map(this::toDto).collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext) {
            Long lastId = notes.get(notes.size() - 1).getNoteId();
            nextCursor = encodeCursor(lastId);
        }
        return new CursorPageResponse<>(data, nextCursor, hasNext);
    }

    @Transactional(readOnly = true)
    public NoteDto get(Long planId, Long noteId) {
        Plan plan = getPlanOrThrow(planId);
        assertReadable(plan, SecurityUtils.getCurrentUserId());

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteException(NoteError.NOTE_NOT_FOUND));
        if (!note.getPlan().getPlanId().equals(planId)) {
            throw new NoteException(NoteError.NOTE_NOT_FOUND);
        }
        return toDto(note);
    }

    public NoteDto update(Long planId, Long noteId, UpdateNoteRequest req) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteException(NoteError.NOTE_NOT_FOUND));
        if (!note.getPlan().getPlanId().equals(planId)) {
            throw new NoteException(NoteError.NOTE_NOT_FOUND);
        }

        note.update(req.getContent(), req.getDate(), req.getPinned());
        Note updated = noteRepository.save(note);
        return toDto(updated);
    }

    public void delete(Long planId, Long noteId) {
        Plan plan = getPlanOrThrow(planId);
        assertWritableItem(plan, SecurityUtils.getCurrentUserId());

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteException(NoteError.NOTE_NOT_FOUND));
        if (!note.getPlan().getPlanId().equals(planId)) {
            throw new NoteException(NoteError.NOTE_NOT_FOUND);
        }
        noteRepository.delete(note);
    }

    // ----------------- util -----------------

    private Plan getPlanOrThrow(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new NoteException(NoteError.PLAN_NOT_FOUND));
    }

    private void assertReadable(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new NoteException(NoteError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new NoteException(NoteError.FORBIDDEN);
        }
    }

    private void assertWritableItem(Plan plan, Long userId) {
        if (plan.getGroup() == null) {
            if (!plan.getOwner().getUserId().equals(userId)) {
                throw new NoteException(NoteError.FORBIDDEN);
            }
        } else {
            boolean isMember = groupMemberRepository
                    .existsByGroupGroupIdAndUserId(plan.getGroup().getGroupId(), userId);
            if (!isMember) throw new NoteException(NoteError.FORBIDDEN);
        }
    }

    private NoteDto toDto(Note n) {
        return new NoteDto(
                n.getNoteId(),
                n.getPlan().getPlanId(),
                n.getContent(),
                n.getDate(),
                n.isPinned(),
                n.getCreatedAt(),
                n.getUpdatedAt()
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