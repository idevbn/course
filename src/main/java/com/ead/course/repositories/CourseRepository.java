package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CourseRepository extends
        JpaRepository<CourseModel, UUID>, JpaSpecificationExecutor<CourseModel> {

    @Query(value = "SELECT CASE WHEN COUNT(tcu) > 0 THEN TRUE ELSE FALSE END FROM tb_courses_users tcu WHERE tcu.course_id = :courseId AND tcu.user_id = :userId",
            nativeQuery = true)
    boolean existsByCourseAndUser(
            @Param("courseId") final UUID courseId,
            @Param("userId") final UUID userId
    );

    @Modifying
    @Query(value = "INSERT INTO tb_courses_users VALUES(:courseId, :userId)", nativeQuery = true)
    void saveCourseUser(@Param("courseId") final UUID courseId, @Param("userId") final UUID userId);

    @Modifying
    @Query(value = "DELETE FROM tb_courses_users WHERE course_id = :courseId", nativeQuery = true)
    void deleteCourseUserByCourse(@Param("courseId") final UUID courseId);

    @Modifying
    @Query(value = "DELETE FROM tb_courses_users WHERE user_id = :userId", nativeQuery = true)
    void deleteCourseUserByUser(@Param("userId") final UUID userId);

}
