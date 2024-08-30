package io.micro_blogger.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.service.security.TokenService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.UUID;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    @LocalServerPort
    private int port;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    public String authHeader(Account account) {
        String token = this.tokenService.generate(account);
        return "Bearer " + token;
    }

    protected String endpointUrl(@NotNull String endpoint) {
        return "http://localhost:" + port + endpoint;
    }

    protected String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }

    protected Account createUniqueAccount(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("someSecurePassword");
        account.setCreatedAt(new Date());
        account.setId(UUID.randomUUID());
        return account;
    }
}
