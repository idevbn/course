package com.ead.course.services;

import com.ead.course.models.LessonModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonService {
    LessonModel save(final LessonModel lessonModel);

    Optional<LessonModel> findLessonIntoModule(final UUID moduleId, final UUID lessonId);

    void delete(final LessonModel lessonModel);

    List<LessonModel> findAllByModule(final UUID moduleId);

    Page<LessonModel> findAllByModule(final Specification<LessonModel> spec, final Pageable pageable);

}
