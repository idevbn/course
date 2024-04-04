package com.ead.course.services.impl;

import com.ead.course.models.UserModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Page<UserModel> findAll(final Specification<UserModel> spec, final Pageable pageable) {
        return this.userRepository.findAll(spec, pageable);
    }

    @Override
    public UserModel save(final UserModel userModel) {
        return this.userRepository.save(userModel);
    }

    @Override
    @Transactional
    public void delete(final UUID userId) {
        this.courseRepository.deleteCourseUserByUser(userId);

        this.userRepository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(final UUID userInstructor) {
        return this.userRepository.findById(userInstructor);
    }

}
