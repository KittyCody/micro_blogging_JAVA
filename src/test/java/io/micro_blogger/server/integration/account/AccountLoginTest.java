//package io.micro_blogger.server.integration.account;
//
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.repository.AccountRepo;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//
//import java.util.HashMap;
//
//import static org.hamcrest.Matchers.containsString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class AccountLoginTest extends IntegrationTestBase {
//
//    @Autowired
//    private AccountRepo accountRepo;
//
//    @ParameterizedTest
//    @ValueSource(strings = { "" })
//    public void testLogin_whenUsernameEmpty_badRequest(String invalidUsername) throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", invalidUsername);
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testLogin_whenUsernameNonExistent_notFound() throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "nonexistent_username");
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value(containsString("entity:not_present")));
//    }
//
//    @Test
//    public void testLogin_whenPasswordEmpty_badRequest() throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "some_username");
//        payload.put("password", "");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testLogin_withValidCredentials_ok() throws Exception {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        Account existingAccount = new Account();
//        existingAccount.setUsername("some_username");
//        existingAccount.setPassword(passwordEncoder.encode("correct_password"));
//        this.accountRepo.save(existingAccount);
//
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "some_username");
//        payload.put("password", "correct_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    public void testLogin_whenIncorrectPassword_unauthorized() throws Exception {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        Account existingAccount = new Account();
//        existingAccount.setUsername("some_username");
//        existingAccount.setPassword(passwordEncoder.encode("correct_password"));
//        this.accountRepo.save(existingAccount);
//
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "some_username");
//        payload.put("password", "wrong_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.code").value("account:credentials_mismatch"));
//    }
//
//    @Test
//    public void testLogin_whenUsernameDoesNotExist_notFound() throws Exception {
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put("username", "non_existent_user");
//        payload.put("password", "some_password");
//
//        MockHttpServletRequestBuilder request = post(this.endpointUrl("/accounts/tokens"))
//                .content(this.toJson(payload))
//                .contentType(MediaType.APPLICATION_JSON);
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value("entity:not_present"));
//    }
//
//}
