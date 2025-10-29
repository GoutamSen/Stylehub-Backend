package com.scm.services;

import com.scm.requests.LoginRequest;
import com.scm.requests.SignupRequest;

public interface AuthService {

	String signup(SignupRequest signupRequest) throws Exception;
	String login(LoginRequest user);
	String generateOtp(String email);
	boolean verifyOtp(String email,String otp);
	boolean resetPassword(String email,String newPassword);
	void logout(String email);
}
