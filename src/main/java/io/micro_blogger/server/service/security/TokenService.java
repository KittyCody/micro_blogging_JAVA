package io.micro_blogger.server.service.security;
import io.micro_blogger.server.model.Account;

public interface TokenService {
    String generate(Account user);
}