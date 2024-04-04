package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validation.CourseValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(value = "/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    private final CourseService courseService;
    private final CourseValidator courseValidator;

    @Autowired
    public CourseController(final CourseService courseService,
                            final CourseValidator courseValidator) {
        this.courseService = courseService;
        this.courseValidator = courseValidator;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> saveCourse(@RequestBody final CourseDTO courseDTO,
                                                  final Errors errors) {
        log.debug("POST saveCourse courseDto received {} ", courseDTO.toString());

        var courseModel = new CourseModel();

        this.courseValidator.validate(courseDTO, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
        }

        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        final CourseModel savedCourseModel = this.courseService.save(courseModel);

        log.debug("POST saveCourse courseId saved {} ", courseModel.getCourseId());
        log.info("Course saved successfully courseId {} ", courseModel.getCourseId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourseModel);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "id") final UUID id) {
        log.debug("DELETE deleteCourse courseId received {} ", id);

        final Optional<CourseModel> optionalCourseModel = this.courseService.findById(id);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }

        this.courseService.delete(optionalCourseModel.get());

        log.debug("DELETE deleteCourse courseId deleted {} ", id);
        log.info("Course deleted successfully courseId {} ", id);

        return  ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully.");
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> updateCourse(
            @PathVariable(value = "id") final UUID id,
            @RequestBody @Valid final CourseDTO courseDTO
    ) {
        log.debug("PUT updateCourse courseDto received {} ", courseDTO.toString());

        final Optional<CourseModel> optionalCourseModel = this.courseService.findById(id);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }
        var courseModel = optionalCourseModel.get();

        BeanUtils.copyProperties(courseDTO, courseModel, "id", "creationDate");

        final CourseModel savedCourseModel = this.courseService.save(courseModel);

        log.debug("PUT updateCourse courseId saved {} ", courseModel.getCourseId());
        log.info("Course updated successfully courseId {} ", courseModel.getCourseId());

        return ResponseEntity.status(HttpStatus.OK).body(savedCourseModel);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<Object> getAllCourses(
            final SpecificationTemplate.CourseSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC)
            final Pageable pageable,
            @RequestParam(required = false) final UUID userId
    ) {
        if (userId != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.courseService.findAll(
                    SpecificationTemplate.courseUserId(userId).and(spec), pageable)
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.findAll(spec, pageable));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value = "id") final UUID id) {
        final Optional<CourseModel> optionalCourseModel = this.courseService.findById(id);

        if (optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalCourseModel.get());
    }

}
