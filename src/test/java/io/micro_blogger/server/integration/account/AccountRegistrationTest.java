//package io.micro_blogger.server.integration.account;
//
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.repository.AccountRepo;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//
//import java.time.Instant;
//import java.util.HashMap;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class AccountRegistrationTest extends IntegrationTestBase {
//
//    @Autowired
//    private AccountRepo accountRepo;
//
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "", "so", "sososososososososososososososos", " some_username ", "some username", ".someuser", "someuser.", "...",
//            "123"
//    })
//    public void testRegisterAccount_whenUsernameInvalid_badRequest(String invalidUsername) throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", invalidUsername);
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testRegisterAccount_whenPasswordEmpty_badRequest() throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "some_username");
//        payload.put("password", "");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isBadRequest());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"some_username", "some.username", "some-username", "some.username7", "44user.baba"})
//    public void testRegisterAccount_withValidPayload_ok(String username) throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", username);
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        Instant beforeTime = Instant.now();
//
//        this.mockMvc.perform(request)
//                .andExpectAll(
//                        status().isOk(),
//                        jsonPath("$.id").isNotEmpty()
//                );
//
//        Instant afterTime = Instant.now();
//
//        Account newAccount = this.accountRepo.findByUsername(username).orElse(null);
//        Assertions.assertNotNull(newAccount);
//
//        Assertions.assertEquals(username, newAccount.getUsername());
//
//        Assertions.assertNotEquals("some_password", newAccount.getPassword());
//        Assertions.assertFalse(newAccount.getPassword().isBlank());
//
//        Assertions.assertTrue(newAccount.getCreatedAt().toInstant().isAfter(beforeTime));
//        Assertions.assertTrue(newAccount.getCreatedAt().toInstant().isBefore(afterTime));
//    }
//
//    @Test
//    public void testRegisterAccount_whenDuplicateUsername_conflict() throws Exception {
//        Account alreadyRegisteredAccount = new Account();
//        alreadyRegisteredAccount.setUsername("some_username");
//        alreadyRegisteredAccount.setPassword("some_password");
//
//        this.accountRepo.save(alreadyRegisteredAccount);
//
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "some_username");
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.code").value("account:already_exists"));
//    }
//
//    @Test
//    public void contextLoads() {
//        Assertions.assertNotNull(context);
//    }
//}
