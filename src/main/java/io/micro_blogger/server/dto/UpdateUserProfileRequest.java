package io.micro_blogger.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {

        private String username;
        private String email;
        private String biography;


}


