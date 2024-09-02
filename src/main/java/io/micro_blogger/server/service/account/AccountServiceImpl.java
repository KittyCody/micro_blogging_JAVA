package io.micro_blogger.server.service.account;

import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.CustomUserDetails;
import io.micro_blogger.server.repository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountServiceImpl(AccountRepo accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository
                .findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public Account save(Account account) {
        if (account.getPassword() == null) {
            throw new IllegalArgumentException("Password must not be null");
        }

        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (account.getCreatedAt() == null) {
            account.setCreatedAt(new Date());
        }

        return accountRepository.save(account);
    }
}
