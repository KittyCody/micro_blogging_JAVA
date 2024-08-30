package io.micro_blogger.server.service.account;

import io.micro_blogger.server.model.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface AccountService extends UserDetailsService {
    Optional<Account> findById(UUID accountId);

}
