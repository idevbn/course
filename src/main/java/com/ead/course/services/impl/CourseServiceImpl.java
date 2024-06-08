package com.ead.course.services.impl;

import com.ead.course.dtos.NotificationCommandDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import com.ead.course.publishers.NotificationCommandPublisher;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.CourseService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository courseUserRepository;
    private final NotificationCommandPublisher notificationCommandPublisher;

    @Autowired
    public CourseServiceImpl(
            final CourseRepository courseRepository,
            final ModuleRepository moduleRepository,
            final LessonRepository lessonRepository,
            final UserRepository courseUserRepository,
            final NotificationCommandPublisher notificationCommandPublisher
    ) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.courseUserRepository = courseUserRepository;
        this.notificationCommandPublisher = notificationCommandPublisher;
    }

    /**
     * Implementando um método de deleção em cascata.
     *
     * É passado o
     * @param courseModel
     * e verificados os módulos associados a esse curso.
     *
     * Caso existam módulos, será feita uma nova verificação,
     * buscando as lições associadas a tais módulos.
     *
     * Caso existam as lições, elas serão excluídas.
     * Depois serão excluídos os módulos e, por fim,
     * será excluído o curso associado.
     */
    @Override
    @Transactional
    public void delete(final CourseModel courseModel) {
        final List<ModuleModel> moduleModelList
                = this.moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if (!moduleModelList.isEmpty()) {
            for (ModuleModel module : moduleModelList) {
                final List<LessonModel> lessonModelList
                        = this.lessonRepository.findAllLessonsIntoModule(module.getModuleId());

                if (!lessonModelList.isEmpty()) {
                    this.lessonRepository.deleteAll(lessonModelList);
                }
            }

            this.moduleRepository.deleteAll(moduleModelList);
        }

        this.courseRepository.deleteCourseUserByCourse(courseModel.getCourseId());

        this.courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel save(final CourseModel courseModel) {
        final CourseModel savedCourseModel = this.courseRepository.save(courseModel);

        return savedCourseModel;
    }

    @Override
    public Optional<CourseModel> findById(final UUID id) {
        final Optional<CourseModel> optionalCourseModel = this.courseRepository.findById(id);

        return optionalCourseModel;
    }

    @Override
    public List<CourseModel> findAll() {
        final List<CourseModel> courses = this.courseRepository.findAll();

        return courses;
    }

    @Override
    public Page<CourseModel> findAll(
            final Specification<CourseModel> spec,
            final Pageable pageable
    ) {
        final Page<CourseModel> courseModelPage = this.courseRepository.findAll(spec, pageable);

        return courseModelPage;
    }

    @Override
    public boolean existsByCourseAndUser(final UUID courseId, final UUID userId) {
        return this.courseRepository.existsByCourseAndUser(courseId, userId);
    }

    @Override
    @Transactional
    public void saveSubscriptionUserInCourse(final UUID courseId, final UUID userId) {
        this.courseRepository.saveCourseUser(courseId, userId);
    }

    @Override
    @Transactional
    public void saveSubscriptionUserInCourseAndSendNotification(final CourseModel course,
                                                                final UserModel user) {
        this.courseRepository.saveCourseUser(course.getCourseId(), user.getUserId());

        try {
            var notificationCommandDTO = new NotificationCommandDTO();
            notificationCommandDTO.setTitle("Bem-Vindo(a) ao Curso: " + course.getName());
            notificationCommandDTO.setMessage(user.getFullName() + " a sua inscrição foi realizada com sucesso!");
            notificationCommandDTO.setUserId(user.getUserId());

            this.notificationCommandPublisher.publishNotificationCommand(notificationCommandDTO);
        } catch (final Exception e) {
            log.warn("Error sending notification!");
        }

    }

}
