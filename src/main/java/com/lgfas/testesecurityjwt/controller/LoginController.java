package com.lgfas.testesecurityjwt.controller;

import com.lgfas.testesecurityjwt.dto.Login;
import com.lgfas.testesecurityjwt.dto.Sessao;
import com.lgfas.testesecurityjwt.model.User;
import com.lgfas.testesecurityjwt.repository.UserRepository;
import com.lgfas.testesecurityjwt.security.JWTCreator;
import com.lgfas.testesecurityjwt.security.JWTObject;
import com.lgfas.testesecurityjwt.security.SecurityConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class LoginController {
    private final PasswordEncoder encoder;
    private final SecurityConfig securityConfig;
    private final UserRepository userRepository;

    public LoginController(PasswordEncoder encoder, SecurityConfig securityConfig, UserRepository userRepository) {
        this.encoder = encoder;
        this.securityConfig = securityConfig;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Sessao logar(@RequestBody Login login){
        User user = userRepository.findByUsername(login.getUsername());
        if(user!=null) {
            boolean passwordOk =  encoder.matches(login.getPassword(), user.getPassword());
            if (!passwordOk) {
                throw new RuntimeException("Senha inválida para o login: " + login.getUsername());
            }
            //Estamos enviando um objeto Sessão para retornar mais informações do usuário
            Sessao sessao = new Sessao();
            sessao.setLogin(user.getUsername());

            JWTObject jwtObject = new JWTObject();
            jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtObject.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
            jwtObject.setRoles(user.getRoles());
            sessao.setToken(JWTCreator.create(SecurityConfig.PREFIX, SecurityConfig.KEY, jwtObject));
            return sessao;
        } else {
            throw new RuntimeException("Erro ao tentar fazer login");
        }
    }
}
