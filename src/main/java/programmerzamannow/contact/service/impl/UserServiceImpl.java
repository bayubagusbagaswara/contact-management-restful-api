package programmerzamannow.contact.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import programmerzamannow.contact.dto.RegisterUserRequest;
import programmerzamannow.contact.dto.UserResponse;
import programmerzamannow.contact.entity.User;
import programmerzamannow.contact.repository.UserRepository;
import programmerzamannow.contact.security.BCrypt;
import programmerzamannow.contact.service.UserService;
import programmerzamannow.contact.service.ValidationService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    public UserServiceImpl(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    @Override
    public void register(RegisterUserRequest registerUserRequest) {
        validationService.validate(registerUserRequest);

        if (userRepository.existsById(registerUserRequest.getUsername())) {
            // user is already exist
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already register");
        }

        User user = new User();
        user.setUsername(registerUserRequest.getUsername());
        user.setPassword(BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt()));
        user.setName(registerUserRequest.getName());

        userRepository.save(user);
    }

    @Override
    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
