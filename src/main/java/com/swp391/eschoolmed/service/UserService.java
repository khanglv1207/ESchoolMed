package com.swp391.eschoolmed.service;

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.swp391.eschoolmed.dto.request.UpdateUserRequest;
import com.swp391.eschoolmed.dto.response.GetAllUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swp391.eschoolmed.dto.response.IntrospectResponse;
import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;
import com.swp391.eschoolmed.model.PasswordResetToken;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.PasswordResetTokenRepository;
import com.swp391.eschoolmed.repository.UserRepository;
import com.swp391.eschoolmed.service.mail.MailService;

@Service
public class UserService {
    @Value("${jwt.signer-key}")
    private String KEY;
    @Value("${jwt.expiration-duration}")
    private long EXPIRATION_DURATION;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository; // dua repo vao service
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private MailService mailService;

    public LoginResponse login(String email, String password) throws JOSEException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(
                    ErrorCode.EMPTY_CREDENTIALS.getStatusCode(),
                    "Vui lòng nhập đầy đủ email và mật khẩu");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        ErrorCode.USERNAME_OR_PASSWORD_ERROR.getStatusCode(),
                        ErrorCode.USERNAME_OR_PASSWORD_ERROR.getMessage()));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(
                    ErrorCode.USERNAME_OR_PASSWORD_ERROR.getStatusCode(),
                    ErrorCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setEmail(email);
        response.setToken(generateToken(user));
        response.setFirstLogin(user.isMustChangePassword());
        response.setRole(user.getRole());

        System.out.println("Generated Token: " + response.getToken());

        return response;
    }



    // token
    public IntrospectResponse introspect(String token) {
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    private void verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        var verified = signedJWT.verify(verifier);
        if (!(verified)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String generateToken(User user) throws JOSEException { // tao token
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId() + "")
                .issuer("Khanglv")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(EXPIRATION_DURATION, ChronoUnit.SECONDS)))
                .claim("scope", user.getRole().toUpperCase())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(KEY));
        return jwsObject.serialize();
    }

    public UUID extractUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            return UUID.fromString(subject);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    // nhập email reset password
    public void requestPasswordRequest(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String otp = String.format("%06d", new Random().nextInt(1000000));
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setOtpCode(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        tokenRepository.save(token);

        mailService.sendOtpEmail(email,otp);
    }



    // xác thuc otp
    public void verifyOtp(String email, String otpCode) {
        PasswordResetToken token = tokenRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new AppException(ErrorCode.ERROR_OTP));

        if(token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRY_OTP);
        }
        token.setVerified(true);
        tokenRepository.save(token);
    }

    //reset password
    public void resetPassword(String email, String newPassword) {
        PasswordResetToken token = tokenRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));

        if (!token.isVerified()) {
            throw new AppException(ErrorCode.OTP_NOT_VERIFY);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        tokenRepository.delete(token);
        userRepository.save(user);
    }

    public List<GetAllUserResponse> getAllUser() {
    List<User> users = userRepository.findAll();
    List<GetAllUserResponse> responses = new ArrayList<>();
    for (User user : users) {
        GetAllUserResponse userResponse = new GetAllUserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setPasswordHash(user.getPasswordHash());
        userResponse.setMustChangePassword(user.isMustChangePassword());
        responses.add(userResponse);
    }
    return responses;
    }

    public GetAllUserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        if (request.getPasswordHash() != null) {
            user.setPasswordHash(request.getPasswordHash());
            user.setMustChangePassword(true);
        }
        user.setRole(request.getRole());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    private GetAllUserResponse toDto(User u) {
        GetAllUserResponse dto = new GetAllUserResponse();
        dto.setId(u.getId());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPasswordHash(u.getPasswordHash());
        dto.setRole(u.getRole());
        dto.setMustChangePassword(u.isMustChangePassword());
        return dto;
    }


}

