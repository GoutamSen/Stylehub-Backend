package com.scm.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.scm.requests.LoginRequest;
import com.scm.requests.OtpRequest;
import com.scm.requests.ResetPasswordRequest;
import com.scm.requests.SignupRequest;
import com.scm.response.ApiResponse;
import com.scm.response.LoginResponse;
import com.scm.servicesimpl.AuthServiceImpl;
import com.scm.util.JwtUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://stylehub-apps.netlify.app", allowedHeaders = "*", allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthServiceImpl authService;
	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) throws Exception {
		System.out.println(request);
		authService.signup(request);

		ApiResponse<Void> response = ApiResponse.<Void>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.CREATED.value()).message("User registered successfully!").data(null).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		System.out.println("start login method : ");
		String token = authService.login(loginRequest);
		String role = jwtUtil.extractRole(token);

		LoginResponse response = LoginResponse.builder().token(token).message("Login successful").role(role).build();

		return ResponseEntity.ok(response);
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/google")
	public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
		String token = payload.get("token");

		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
					new JacksonFactory())
					.setAudience(Collections
							.singletonList("331224454738-7emvlqhld3akq1m5816n7to91taasodc.apps.googleusercontent.com"))
					.build();

			GoogleIdToken idToken = verifier.verify(token);
			if (idToken != null) {
				GoogleIdToken.Payload tokenPayload = idToken.getPayload();
				String email = tokenPayload.getEmail();
				String name = (String) tokenPayload.get("name");

				// ✅ Here: save or fetch user from DB
				// Example: findOrCreateUser(email, name);
				// ✅ Generate your own JWT for Angular
				String jwt = jwtUtil.generateToken(email, "USER");

				
				return  ResponseEntity.ok(Map.of("token", jwt, "email", email, "name", name, "role", "USER"));
			} else {
				return   ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
			}
		} catch (Exception e) {
			return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error verifying Google token");
		}
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/generate-otp")
	public ResponseEntity<ApiResponse<String>> generateOtp(@RequestBody OtpRequest request) {
		String otp = authService.generateOtp(request.getEmail());

		ApiResponse<String> response = ApiResponse.<String>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("OTP sent successfully to !"+request.getEmail()).data(otp) // or null if you don’t want
																							// to expose OTP in response
				.build();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		boolean isValid = authService.verifyOtp(email, otp);
		if (isValid) {
			return ResponseEntity.ok(ApiResponse.<Void>builder().status(HttpStatus.OK.value()).message("Valid Otp !")
					.timestamp(LocalDateTime.now()).build());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.<Void>builder().status(HttpStatus.BAD_REQUEST.value())
							.message("Invalid Otp ! Please regenerate the Otp ").timestamp(LocalDateTime.now())
							.build());
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
		System.out.println("start");
		boolean success = authService.resetPassword(request.getEmail(), request.getNewPassword());

		if (success) {
			return ResponseEntity.ok(ApiResponse.<Void>builder().status(HttpStatus.OK.value())
					.message("Password reset successfully!").timestamp(LocalDateTime.now()).build());
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.<Void>builder().status(HttpStatus.BAD_REQUEST.value())
							.message("Invalid request. Could not reset password.").timestamp(LocalDateTime.now())
							.build());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token,
			@RequestParam String email) {
		// Extract token from "Bearer <token>"
		String jwtToken = token.replace("Bearer ", "");
		authService.logout(email);

		ApiResponse<String> response = ApiResponse.<String>builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.OK.value()).message("User logged out successfully").data("Status set to INACTIVE")
				.build();

		return ResponseEntity.ok(response);
	}
}
