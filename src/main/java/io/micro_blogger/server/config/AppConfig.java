package io.micro_blogger.server.config;

import io.micro_blogger.server.repository.AccountRepo;
import io.micro_blogger.server.service.account.AccountServiceImpl;
import io.micro_blogger.server.service.security.AuthService;
import io.micro_blogger.server.service.security.AuthServiceImpl;
import io.micro_blogger.server.service.security.TokenService;
import io.micro_blogger.server.service.security.TokenServiceImpl;
import io.micro_blogger.server.service.userProfile.UserProfileService;
import io.micro_blogger.server.service.userProfile.UserProfileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class AppConfig {

    @Bean
    public AuthService provideAuthService(AccountRepo userRepo, PasswordEncoder passwordEncoder,
                                          AuthenticationManager authenticationManager,
                                          UserProfileService userProfileService,
                                          TokenService tokenService) {
        return new AuthServiceImpl(userRepo, passwordEncoder, authenticationManager, userProfileService, tokenService);
    }

    @Bean
    public UserDetailsService provideAccountService(AccountRepo accountRepo) {
        return new AccountServiceImpl(accountRepo);
    }

    @Bean
    public TokenService tokenService() {
        return new TokenServiceImpl();
    }
}
