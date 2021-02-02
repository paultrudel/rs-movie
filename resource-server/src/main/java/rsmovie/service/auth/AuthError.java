package rsmovie.service.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthError implements AuthResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;
}
