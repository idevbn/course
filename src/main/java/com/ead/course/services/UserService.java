package com.ead.course.services;

import com.ead.course.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Page<UserModel> findAll(final Specification<UserModel> spec, final Pageable pageable);

    UserModel save(final UserModel userModel);

    void delete(final UUID userId);

    Optional<UserModel> findById(final UUID userInstructor);

}
