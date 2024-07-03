package com.lgfas.testesecurityjwt.service;

import com.lgfas.testesecurityjwt.model.User;
import com.lgfas.testesecurityjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void createUser(User user) {
        String password = user.getPassword();

        //criptografando antes de salvar no banco
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }
}
