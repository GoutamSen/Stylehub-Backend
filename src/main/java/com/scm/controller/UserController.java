package com.scm.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.scm.entities.BookAppointment;
import com.scm.entities.User;
import com.scm.requests.UploadImageRequest;
import com.scm.response.ApiResponse;
import com.scm.services.BlackListService;
import com.scm.services.BookAppointmentService;
import com.scm.services.UserService;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
	
	
	@Autowired private BookAppointmentService appointmentService;
	@Autowired private BlackListService blackListService;
	@Autowired private UserService userSerice;
	
    @GetMapping("/profile")
    public String userProfile() {
        return "User Profile Accessed";
    }
    
    @SuppressWarnings("unchecked")
	@GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<BookAppointment>>> getAllAppointmentByEmail(@RequestParam String email){
    	List<BookAppointment> listOfAppointments = appointmentService.getAllAppointmentByUsernameOrEmail(email);
    	System.out.println("start appointment controller --->"+listOfAppointments);
    	@SuppressWarnings("rawtypes")
		ApiResponse response =  ApiResponse.builder()
    			                .timestamp(LocalDateTime.now())
    			                .data(listOfAppointments) 
    			                .message("Fetch All Appointment of User !") 
    			                .status(HttpStatus.OK.value()) 
    			                .build();
    	return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // remove "Bearer "
        blackListService.addToBlacklist(jwt);
        return ResponseEntity.ok("Logged out successfully!");
    }
    
    
    @GetMapping("")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@RequestParam String email){
    	User user = userSerice.findByUsernameOrEmail(email);
    	ApiResponse<User> response = ApiResponse.<User>builder()
    			                     .timestamp(LocalDateTime.now())
    			                     .status(HttpStatus.OK.value())
    			                     .message("Find User By Email")
    			                     .data(user)
    			                     .build();
    	return ResponseEntity.ok(response);
    }
    
    @PutMapping("/upload/image")
    public ResponseEntity<ApiResponse<User>> uploadUserImage(@RequestBody UploadImageRequest request) throws IOException{
    	System.out.println(request.getEmail());
    	System.out.println(request.getImageUrl());
    	User user = userSerice.uploadUserImage(request.getEmail(), request.getImageUrl());
    	ApiResponse<User> response = ApiResponse.<User>builder()
    			                      .timestamp(LocalDateTime.now())
    			                      .status(HttpStatus.OK.value())
    			                      .message("User Image Uploaded : ")
    			                      .data(user)
    			                      .build();
    	return ResponseEntity.ok(response);
    }
    
    
}