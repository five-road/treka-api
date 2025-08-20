package com.example.ieumapi.plan.repository;

import com.example.ieumapi.plan.domain.Note;
import com.example.ieumapi.plan.domain.Plan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByPlanAndNoteIdLessThanOrderByNoteIdDesc(
            Plan plan, Long cursorId, Pageable pageable
    );

    List<Note> findByPlanAndDateAndNoteIdLessThanOrderByNoteIdDesc(
            Plan plan, LocalDate date, Long cursorId, Pageable pageable
    );

    List<Note> findByPlanAndPinnedAndNoteIdLessThanOrderByNoteIdDesc(
            Plan plan, boolean pinned, Long cursorId, Pageable pageable
    );
}
