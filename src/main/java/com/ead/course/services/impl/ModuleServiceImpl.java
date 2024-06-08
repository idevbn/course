package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.ModuleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public ModuleServiceImpl(
            final ModuleRepository moduleRepository,
            final LessonRepository lessonRepository
    ) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional
    public void delete(final ModuleModel moduleModel) {
        final List<LessonModel> lessonModelList
                = this.lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId());

        if (!lessonModelList.isEmpty()) {
            this.lessonRepository.deleteAll(lessonModelList);
        }

        this.moduleRepository.delete(moduleModel);
    }

    @Override
    public ModuleModel save(final ModuleModel moduleModel) {
        final ModuleModel savedModule = this.moduleRepository.save(moduleModel);

        return savedModule;
    }

    @Override
    public Optional<ModuleModel> findModuleIntoCourse(final UUID courseId, final UUID moduleId) {
        final Optional<ModuleModel> moduleIntoCourse
                = this.moduleRepository.findModuleIntoCourse(courseId, moduleId);

        return moduleIntoCourse;
    }

    @Override
    public List<ModuleModel> findAllByCourse(final UUID courseId) {
        final List<ModuleModel> modulesIntoCourse = this.moduleRepository
                .findAllModulesIntoCourse(courseId);

        return modulesIntoCourse;
    }

    @Override
    public Optional<ModuleModel> findById(final UUID moduleId) {
        final Optional<ModuleModel> optionalModuleModel = this.moduleRepository.findById(moduleId);

        return optionalModuleModel;
    }

    @Override
    public Page<ModuleModel> findAllByCourse(
            final Specification<ModuleModel> spec,
            final Pageable pageable
    ) {
        final Page<ModuleModel> moduleModelPage = this.moduleRepository.findAll(spec, pageable);

        return moduleModelPage;
    }

}
