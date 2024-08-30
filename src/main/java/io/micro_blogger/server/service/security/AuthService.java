package io.micro_blogger.server.service.security;

import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.AuthenticateAccountRequest;
import io.micro_blogger.server.dto.RegisterAccountRequest;
import io.micro_blogger.server.viewmodel.AuthenticationViewModel;
import io.micro_blogger.server.viewmodel.RegistrationViewModel;

public interface AuthService {
    Result<RegistrationViewModel> registerAccount(RegisterAccountRequest request);

    Result<AuthenticationViewModel> authenticateAccount(AuthenticateAccountRequest request);

}
