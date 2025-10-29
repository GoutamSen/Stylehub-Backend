package com.scm.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Role;
import com.scm.entities.Status;
import com.scm.entities.User;

public interface UserService {

	   List<User> getAllUser();
	   long coutOfUser();
	   long countOfUserByStatus(Status status);
	   long countOfuserByRole(Role role);
	   User findByUsernameOrEmail(String usernameoremail);
	   String updateStatus(String email,Status newStatus);
	   List<User> findUserByStatus(Status status);
	   List<User> findUsersByRole(Role role);
	   User uploadUserImage(String email,String imageUrl) throws IOException;
	   
}
