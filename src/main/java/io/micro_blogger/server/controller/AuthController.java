package io.micro_blogger.server.controller;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.AuthenticateAccountRequest;
import io.micro_blogger.server.dto.RegisterAccountRequest;
import io.micro_blogger.server.service.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Validated
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(@Qualifier("authServiceImpl") AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> registerAccount(@Valid @RequestBody RegisterAccountRequest request) {
        return handleResult(authService.registerAccount(request));
    }

    @PostMapping("/tokens")
    public ResponseEntity<?> authenticateAccount(@Valid @RequestBody AuthenticateAccountRequest request) {
        return handleResult(authService.authenticateAccount(request));
    }

    private ResponseEntity<?> handleResult(Result<?> result) {
        if (result.isFailure()) {
            return handleError(result.getError());
        }
        return ResponseEntity.ok(result.getValue());
    }

    private ResponseEntity<ApiError> handleError(ApiError error) {
        HttpStatus status = getStatusForError(error);

        return new ResponseEntity<>(error, status);
    }

    private HttpStatus getStatusForError(ApiError error) {
        if (error == CommonErrors.ACCOUNT_ALREADY_EXISTS) return HttpStatus.CONFLICT;
        if (error == CommonErrors.USERNAME_INVALID) return HttpStatus.BAD_REQUEST;
        if (error == CommonErrors.PASSWORD_TOO_SHORT) return HttpStatus.BAD_REQUEST;
        if (error == CommonErrors.ACCOUNT_CREDENTIALS_MISMATCH) return HttpStatus.UNAUTHORIZED;
        if (error == CommonErrors.ENTITY_NOT_PRESENT) return HttpStatus.NOT_FOUND;

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
