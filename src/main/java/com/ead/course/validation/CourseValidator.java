package com.ead.course.validation;

import com.ead.course.configs.security.AuthenticationCurrentUserService;
import com.ead.course.dtos.CourseDTO;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    private final Validator validator;
    private final UserService userService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    @Autowired
    public CourseValidator(final Validator validator,
                           final UserService userService,
                           final AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.validator = validator;
        this.userService = userService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CourseDTO courseDTO = (CourseDTO) target;
        this.validator.validate(courseDTO, errors);

        if (!errors.hasErrors()) {
            this.validateUserInstructor(courseDTO.getUserInstructor(), errors);
        }
     }

     private void validateUserInstructor(final UUID userInstructor, final Errors errors) {
         final UUID currentUserId = this.authenticationCurrentUserService
                 .getCurrentUser()
                 .getUserId();

         if (currentUserId.equals(userInstructor)) {

             final Optional<UserModel> userModelOpt = this.userService.findById(userInstructor);

             if (userModelOpt.isEmpty()) {
                 errors.rejectValue(
                         "userInstructor",
                         "UserInstructorError",
                         "Instructor not found."
                 );
             } else if (userModelOpt.get().getUserType().equals(UserType.STUDENT.toString()) ||
                        userModelOpt.get().getUserType().equals(UserType.USER.toString())) {
                 errors.rejectValue(
                         "userInstructor",
                         "UserInstructorError",
                         "User must be INSTRUCTOR or ADMIN."
                 );
             }
         } else {
             throw new AccessDeniedException("Forbidden");
         }
     }

}
