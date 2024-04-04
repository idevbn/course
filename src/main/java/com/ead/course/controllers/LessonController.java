package com.ead.course.controllers;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    private final LessonService lessonService;
    private final ModuleService moduleService;

    @Autowired
    public LessonController(final LessonService lessonService, final ModuleService moduleService) {
        this.lessonService = lessonService;
        this.moduleService = moduleService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping(value = "/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(
            @RequestBody final LessonDTO lessonDTO,
            @PathVariable(value = "moduleId") final UUID moduleId
    ) {
        log.debug("POST saveLesson lessonDto received {} ", lessonDTO.toString());

        final Optional<ModuleModel> optionalModuleModel = this.moduleService.findById(moduleId);

        if (optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found.");
        }

        var lessonModel = new LessonModel();

        BeanUtils.copyProperties(lessonDTO, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(optionalModuleModel.get());

        final LessonModel savedLessonModel = this.lessonService.save(lessonModel);

        log.debug("POST saveLesson lessonId saved {} ", lessonModel.getLessonId());
        log.info("Lesson saved successfully lessonId {} ", lessonModel.getLessonId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLessonModel);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping(value = "/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(
            @PathVariable(value = "moduleId") final UUID moduleId,
            @PathVariable(value = "lessonId") final UUID lessonId
    ) {
        log.debug("DELETE deleteLesson lessonId received {} ", lessonId);

        final Optional<LessonModel> optionalLessonModel
                = this.lessonService.findLessonIntoModule(moduleId, lessonId);

        if (optionalLessonModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        this.lessonService.delete(optionalLessonModel.get());

        log.debug("DELETE deleteLesson lessonId deleted {} ", lessonId);
        log.info("Lesson deleted successfully lessonId {} ", lessonId);

        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully.");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping(value = "/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(
            @PathVariable(value = "moduleId") final UUID moduleId,
            @PathVariable(value = "lessonId") final UUID lessonId,
            @RequestBody @Valid final LessonDTO lessonDTO
    ) {
        log.debug("PUT updateLesson lessonDto received {} ", lessonDTO.toString());

        final Optional<LessonModel> optionalLessonModelModel
                = this.lessonService.findLessonIntoModule(moduleId, lessonId);

        if (optionalLessonModelModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        var lessonModel = optionalLessonModelModel.get();

        BeanUtils.copyProperties(lessonDTO, lessonModel, "id", "creationDate");

        final LessonModel savedLessonModel = this.lessonService.save(lessonModel);

        log.debug("PUT updateLesson lessonId saved {} ", lessonModel.getLessonId());
        log.info("Lesson updated successfully lessonId {} ", lessonModel.getLessonId());

        return ResponseEntity.status(HttpStatus.OK).body(savedLessonModel);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(value = "/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(
            @PathVariable(value = "moduleId") final UUID moduleId,
            final SpecificationTemplate.LessonSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC)
            final Pageable pageable
    ) {
        final Page<LessonModel> lessons = this.lessonService
                .findAllByModule(SpecificationTemplate.lessonIdModule(moduleId).and(spec), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(lessons);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(value = "/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(
            @PathVariable(value = "moduleId") final UUID moduleId,
            @PathVariable(value = "lessonId") final UUID lessonId
    ) {
        final Optional<LessonModel> optionalLessonModel
                = this.lessonService.findLessonIntoModule(moduleId, lessonId);

        if (optionalLessonModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalLessonModel.get());
    }

}
