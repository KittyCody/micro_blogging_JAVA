package io.micro_blogger.server.service.security;

import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.AuthenticateAccountRequest;
import io.micro_blogger.server.dto.RegisterAccountRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.repository.AccountRepo;
import io.micro_blogger.server.service.userProfile.UserProfileService;
import io.micro_blogger.server.viewmodel.AuthenticationViewModel;
import io.micro_blogger.server.viewmodel.RegistrationViewModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserProfileService userProfileService;

    @Autowired
    public AuthServiceImpl(AccountRepo userRepo,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           UserProfileService profileService,
                           @Qualifier("tokenServiceImpl") TokenService tokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userProfileService = profileService;
    }

    @Transactional
    @Override
    public Result<RegistrationViewModel> registerAccount(RegisterAccountRequest request) {
        if (this.userRepo.findByUsername(request.username()).isPresent()) {
            return Result.failure(CommonErrors.ACCOUNT_ALREADY_EXISTS);
        }

        Account account = this.storeAccount(request);
        this.userProfileService.createForAccount(account);

        RegistrationViewModel result = new RegistrationViewModel(
                account.getId(),
                account.getUsername(),
                account.getCreatedAt()
        );

        return Result.success(result);
    }

    @Override
    public Result<AuthenticationViewModel> authenticateAccount(AuthenticateAccountRequest request) {
        Account user = this.userRepo.findByUsername(request.username()).orElse(null);

        if (user == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        if (!areCredentialsMatching(request)) {
            return Result.failure(CommonErrors.ACCOUNT_CREDENTIALS_MISMATCH);
        }

        String accessToken = this.tokenService.generate(user);
        AuthenticationViewModel result = new AuthenticationViewModel(accessToken);

        return Result.success(result);
    }

    private boolean areCredentialsMatching(AuthenticateAccountRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.username(), request.password());
            this.authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            return false;
        }

        return true;
    }

    private Account storeAccount(RegisterAccountRequest request) {
        Account account = new Account();
        account.setUsername(request.username());
        account.setPassword(this.passwordEncoder.encode(request.password()));
        account.setCreatedAt(new Date());

        return this.userRepo.save(account);
    }
}
