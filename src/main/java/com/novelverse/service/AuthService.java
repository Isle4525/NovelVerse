package com.novelverse.service;

import com.novelverse.dto.AuthResponse;
import com.novelverse.dto.ChangePasswordRequest;
import com.novelverse.dto.LoginRequest;
import com.novelverse.dto.ProfileResponse;
import com.novelverse.dto.RegisterRequest;
import com.novelverse.dto.UpdateProfileRequest;
import com.novelverse.model.User;
import com.novelverse.model.UserRole;
import com.novelverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AvatarStorageService avatarStorageService;

    @Value("${app.admin-email:}")
    private String adminEmail;

    public String register(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email уже занят");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Имя пользователя уже занято");
        }

        String code = String.format("%06d", new java.util.Random().nextInt(999999));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(resolveRole(req.getUsername(), req.getEmail()))
                .verified(false)
                .verificationToken(code)
                .tokenExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(req.getEmail(), code);

        return "Код отправлен на " + req.getEmail();
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail() != null ? req.getEmail().trim() : "";
        String name = req.getName() != null ? req.getName().trim() : "";

        User user = !email.isBlank()
                ? userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"))
                : userRepository.findByUsername(name)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email не подтверждён");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(null)
                .user(toUserDto(user))
                .build();
    }

    public ProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return toProfileResponse(user);
    }

    public AuthResponse updateProfile(String currentEmail, UpdateProfileRequest req) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String username = req.getUsername() != null ? req.getUsername().trim() : "";
        String email = req.getEmail() != null ? req.getEmail().trim() : "";

        if (username.isBlank() || email.isBlank()) {
            throw new RuntimeException("Заполните имя и email");
        }

        userRepository.findByUsername(username)
                .filter(found -> !found.getId().equals(user.getId()))
                .ifPresent(found -> {
                    throw new RuntimeException("Имя пользователя уже занято");
                });

        userRepository.findByEmail(email)
                .filter(found -> !found.getId().equals(user.getId()))
                .ifPresent(found -> {
                    throw new RuntimeException("Email уже занят");
                });

        user.setUsername(username);
        user.setEmail(email);
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(null)
                .user(toUserDto(user))
                .build();
    }

    public ProfileResponse uploadAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String avatarUrl = avatarStorageService.storeAvatar(file, user.getId());
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return toProfileResponse(user);
    }

    public String changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String newPassword = req.getNewPassword() != null ? req.getNewPassword() : "";
        String confirmPassword = req.getConfirmPassword() != null ? req.getConfirmPassword() : "";

        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            throw new RuntimeException("Заполните новый пароль и подтверждение");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Новые пароли не совпадают");
        }
        if (newPassword.length() < 6) {
            throw new RuntimeException("Новый пароль должен быть не короче 6 символов");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Пароль обновлён";
    }

    public void confirmEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен"));

        if (user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Токен истёк");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiresAt(null);
        userRepository.save(user);
    }

    public String verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.isVerified()) {
            throw new RuntimeException("Email уже подтверждён");
        }

        if (!user.getVerificationToken().equals(code)) {
            throw new RuntimeException("Неверный код");
        }

        if (user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Код истёк, зарегистрируйся заново");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiresAt(null);
        userRepository.save(user);

        return "Email подтверждён!";
    }

    public void resendCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.isVerified()) {
            throw new RuntimeException("Email уже подтверждён");
        }

        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setVerificationToken(code);
        user.setTokenExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        emailService.sendVerificationEmail(email, code);
    }

    private UserRole resolveRole(String username, String email) {
        if (!adminEmail.isBlank() && adminEmail.equalsIgnoreCase(email)) {
            return UserRole.ADMIN;
        }
        if ("admin".equalsIgnoreCase(username) && userRepository.countByRole(UserRole.ADMIN) == 0) {
            return UserRole.ADMIN;
        }
        return UserRole.USER;
    }

    private AuthResponse.UserDto toUserDto(User user) {
        return AuthResponse.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
