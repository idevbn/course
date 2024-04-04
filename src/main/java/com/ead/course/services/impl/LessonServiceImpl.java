package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonServiceImpl(final LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonModel save(final LessonModel lessonModel) {
        final LessonModel savedLessonModel = this.lessonRepository.save(lessonModel);

        return savedLessonModel;
    }

    @Override
    public Optional<LessonModel> findLessonIntoModule(final UUID moduleId, final UUID lessonId) {
        final Optional<LessonModel> lessonIntoModule
                = this.lessonRepository.findLessonIntoModule(moduleId, lessonId);

        return lessonIntoModule;
    }

    @Override
    public void delete(final LessonModel lessonModel) {
        this.lessonRepository.delete(lessonModel);
    }

    @Override
    public List<LessonModel> findAllByModule(final UUID moduleId) {
        final List<LessonModel> allLessonsIntoModule
                = this.lessonRepository.findAllLessonsIntoModule(moduleId);

        return allLessonsIntoModule;
    }

    @Override
    public Page<LessonModel> findAllByModule(
            final Specification<LessonModel> spec, final Pageable pageable
    ) {
        final Page<LessonModel> lessonModelPage = this.lessonRepository.findAll(spec, pageable);

        return lessonModelPage;
    }

}
