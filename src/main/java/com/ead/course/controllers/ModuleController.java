package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
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
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService;

    @Autowired
    public ModuleController(final ModuleService moduleService, final CourseService courseService) {
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping(value = "/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(
            @RequestBody final ModuleDTO moduleDTO,
            @PathVariable(value = "courseId") final UUID courseId
    ) {
        log.debug("POST saveModule moduleDto received {} ", moduleDTO.toString());

        final Optional<CourseModel> optionalCourseModel = this.courseService.findById(courseId);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }

        var moduleModel = new ModuleModel();

        BeanUtils.copyProperties(moduleDTO, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(optionalCourseModel.get());

        final ModuleModel savedModuleModel = this.moduleService.save(moduleModel);

        log.debug("POST saveModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module saved successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedModuleModel);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping(value = "/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(
            @PathVariable(value = "courseId") final UUID courseId,
            @PathVariable(value = "moduleId") final UUID moduleId
    ) {
        log.debug("DELETE deleteModule moduleId received {} ", moduleId);

        final Optional<ModuleModel> optionalModuleModel
                = this.moduleService.findModuleIntoCourse(courseId, moduleId);

        if (optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }

        this.moduleService.delete(optionalModuleModel.get());

        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {} ", moduleId);

        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully.");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping(value = "/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(
            @PathVariable(value = "courseId") final UUID courseId,
            @PathVariable(value = "moduleId") final UUID moduleId,
            @RequestBody @Valid final ModuleDTO moduleDTO
    ) {
        log.debug("PUT updateModule moduleDto received {} ", moduleDTO.toString());

        final Optional<ModuleModel> optionalModuleModel
                = this.moduleService.findModuleIntoCourse(courseId, moduleId);

        if (optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        var moduleModel = optionalModuleModel.get();

        BeanUtils.copyProperties(moduleDTO, moduleModel, "id", "creationDate");

        final ModuleModel savedModuleModel = this.moduleService.save(moduleModel);

        log.debug("PUT updateModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module updated successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.OK).body(savedModuleModel);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(value = "/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(
            @PathVariable(value = "courseId") final UUID courseId,
            final SpecificationTemplate.ModuleSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC)
            final Pageable pageable
    ) {
        final Page<ModuleModel> modules = this.moduleService
                .findAllByCourse(SpecificationTemplate.moduleIdCourse(courseId).and(spec), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(modules);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(value = "/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(
            @PathVariable(value = "courseId") final UUID courseId,
            @PathVariable(value = "moduleId") final UUID moduleId
    ) {
        final Optional<ModuleModel> optionalModuleModel
                = this.moduleService.findModuleIntoCourse(courseId, moduleId);

        if (optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalModuleModel.get());
    }

}
