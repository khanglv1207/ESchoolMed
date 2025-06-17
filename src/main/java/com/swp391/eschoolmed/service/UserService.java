package com.swp391.eschoolmed.service;

import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import com.swp391.eschoolmed.dto.request.RegisterRequest;
import com.swp391.eschoolmed.dto.response.IntrospectResponse;
import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.dto.response.RegisterResponse;
import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.UserRepository;

@Service
public class UserService {
    @Value("${jwt.signer-key}")
    private String KEY;
    @Value("${jwt.expiration-duration}")
    private long EXPIRATION_DURATION;

    @Autowired
    private UserRepository userRepository; // dua repo vao service

    public LoginResponse login(String email, String password) {
    if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
        throw new ResponseStatusException(
            ErrorCode.EMPTY_CREDENTIALS.getStatusCode(),
            "Vui lòng nhập đầy đủ email và mật khẩu"
        );
    }

    Optional<User> optionalUser = userRepository.findByEmail(email);
    
    // Gộp cả 2 điều kiện email không tồn tại hoặc mật khẩu sai
    if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(password)) {
        throw new ResponseStatusException(
            ErrorCode.USERNAME_OR_PASSWORD_ERROR.getStatusCode(),
            ErrorCode.USERNAME_OR_PASSWORD_ERROR.getMessage()
        );
    }

    User user = optionalUser.get();

    LoginResponse response = new LoginResponse();
    response.setId(user.getId());
    response.setEmail(user.getEmail());
    response.setFullName(user.getFullName());

    return response;
}


    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã được sử dụng.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setRole("PARENT");
        userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setEmail(request.getEmail());
        response.setFullName(request.getFullName());
        response.setRole(user.getRole());
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

}
