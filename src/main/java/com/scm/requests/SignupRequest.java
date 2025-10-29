package com.scm.requests;
import com.scm.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SignupRequest {
	
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String address;
    private Role role;
}

