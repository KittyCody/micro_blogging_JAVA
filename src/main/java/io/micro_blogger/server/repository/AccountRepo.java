package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepo extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);

}