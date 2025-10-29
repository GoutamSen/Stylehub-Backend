package com.scm.servicesimpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.scm.entities.Role;
import com.scm.entities.Status;
import com.scm.entities.User;
import com.scm.exception.UserAlreadyExistsException;
import com.scm.exception.UserBlockedException;
import com.scm.exception.UserNotFoundException;
import com.scm.repositories.UserRepository;
import com.scm.requests.LoginRequest;
import com.scm.requests.SignupRequest;
import com.scm.services.AuthService;
import com.scm.util.JwtUtil;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtil jwtUtil;
	
//	@Autowired
//	private JavaMailSender mailSender;
	

	private final Map<String, String> otpStore = new HashMap<>();

	

	@Override
	public String signup(SignupRequest request) throws Exception {
		// checks for Username
		Optional<User> duplicateUser = userRepository.findByUsername(request.getUsername());
		if (duplicateUser.isPresent()) {
			throw new UserAlreadyExistsException("Username already exists! Please choose another one.");
		}

		// checks for Email
		Optional<User> duplicateEmail = userRepository.findByEmail(request.getEmail());
		if (duplicateEmail.isPresent()) {
			throw new UserAlreadyExistsException("User Email already exists! Please choose another one.");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setAddress(request.getAddress());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(Status.INACTIVE);
		if (request.getRole() == null) {
			user.setRole(Role.USER); // default role
		} else {
			user.setRole(request.getRole());
		}
		userRepository.save(user);
		return "User Registered Successfully ";
	}

	@Override
	public String login(LoginRequest loginRequest) {
		User existingUser = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
				() -> new UserNotFoundException("No user registered with email: " + loginRequest.getEmail()));

		System.out.println("User status: " + existingUser.getStatus());

		// ✅ Check block status first
		if (existingUser.getStatus() == Status.BLOCK) {
			throw new UserBlockedException("User is blocked");
		}

		// ✅ Then check password
		if (!passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}

		// ✅ If everything is fine → generate token
		String token = jwtUtil.generateToken(existingUser.getUsername(), existingUser.getRole().toString());

		// Mark user as ACTIVE after successful login
		existingUser.setStatus(Status.ACTIVE);
		userRepository.save(existingUser);

		return token;
	}

	@Override
	public String generateOtp(String email) {
		String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
		otpStore.put(email, otp);
		// Send Email
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(email);
//		message.setSubject("Your OTP Code");
//		message.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
//		mailSender.send(message);
		
        Email from = new Email("sengoutam689@gmail.com");
        Email to = new Email(email);
        String messageBody = "Hello your OTP is : "+otp+"\n\n This OTP is valid for 5 Minutes.";
        Content content = new Content("text/plan", messageBody);
        String subject = "Your OTP for StyleHub Login";
        Mail mail = new Mail(from,subject,to,content);
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
     
        try {
        	Request request = new Request();
        	request.setMethod(Method.POST);
        	request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println("Email sent with stuats : "+response.getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return otp;
	}

	// Verify OTP
	@Override
	public boolean verifyOtp(String email, String otp) {
		return otp.equals(otpStore.get(email));
	}

	@Override
	public boolean resetPassword(String email, String newPassword) {
		Optional<User> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent()) {

			User user = existingUser.get();

			// ✅ encode the new password before saving
			user.setPassword(passwordEncoder.encode(newPassword));

			// ✅ save updated user back to DB
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void logout(String email) {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
		user.setStatus(Status.INACTIVE);
		userRepository.save(user);
	}

}
