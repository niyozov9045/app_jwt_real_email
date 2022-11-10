package uz.pdp.appjwtrealemailauditing.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtrealemailauditing.dto.ApiResponse;
import uz.pdp.appjwtrealemailauditing.dto.LoginDto;
import uz.pdp.appjwtrealemailauditing.dto.RegisterDto;
import uz.pdp.appjwtrealemailauditing.entity.User;
import uz.pdp.appjwtrealemailauditing.entity.enums.RoleName;
import uz.pdp.appjwtrealemailauditing.repository.RoleRepository;
import uz.pdp.appjwtrealemailauditing.repository.UserRepository;
import uz.pdp.appjwtrealemailauditing.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    public ApiResponse registerUser(RegisterDto registerDto) {
        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("Bunday email allaqachon mavjud", false);
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setEmail(registerDto.getEmail());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_USER))); //Collection singleton bitta obyektni collection qiberadi
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);
        // emailga yuborish metodoni chaqiryapmiz
        sendEmail(user.getEmail(), user.getEmailCode());
        return new ApiResponse("Muvaffaqiyatli ro'yxatdan o'tdingiz. Accauntingiz activlashtirilishi uchun emailingizni tasdiqlang", true);
    }


    public Boolean sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Test@pdp.gmail.com");
            message.setTo(sendingEmail);
            message.setSubject("Accauntni tasdiqlash");
            message.setText("<a href='http://localhost:8080/api/auth/verifyEmail?emailCode=" + emailCode + "&email=" + sendingEmail + "'>Tasdiqlang</a>");
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public ApiResponse verifyEmail(String emailCode, String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            userRepository.save(user);
            return new ApiResponse("Accaunt tasdiqlandi", true);
        }
        return new ApiResponse("Accaunt allaqachon tasdiqlangan", false);
    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getPassword(),
                    loginDto.getPassword()));
            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
            return new ApiResponse("Token", true,token);
        }catch (BadCredentialsException badCredentialsException){
            return new ApiResponse("Parol yoki login xato",false);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + "topilmadi"));
//        Optional<User> optionalUser = userRepository.findByEmail(username);
//        if (optionalUser.isPresent()) {
//            return optionalUser.get();
//            throw new UsernameNotFoundException(username + "Topilmadi");
//        }
    }
}
