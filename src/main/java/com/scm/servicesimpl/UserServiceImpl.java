package com.scm.servicesimpl;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Role;
import com.scm.entities.Status;
import com.scm.entities.User;
import com.scm.exception.UserNotFoundException;
import com.scm.repositories.UserRepository;
import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	@Override
	public long coutOfUser() {
		return userRepository.count();
	}

	@Override
	public User findByUsernameOrEmail(String usernameOrEmail) {
		 if (usernameOrEmail.contains("@")) {
	            return userRepository.findByUsernameOrEmail(null, usernameOrEmail).orElseThrow(()->new UserNotFoundException("User NOt Found for this email"));
	        } else {
	            return userRepository.findByUsernameOrEmail(usernameOrEmail, null).orElseThrow(()-> new UserNotFoundException("User Not Found For this username"));
	        }
	}

	@Override
	public String updateStatus(String email, Status newStatus) {
		User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User Does Not Exists for this email"));
		Status currentStatus = user.getStatus();
		if(newStatus==currentStatus) {
			return "User is alread"+newStatus;
		}
		user.setStatus(newStatus);
		userRepository.save(user);
		return "User Status Updated";
	}

	@Override
	public List<User> findUserByStatus(Status status) {
		List<User> listOfUser = userRepository.findByStatus(status);
		return listOfUser;
	}

	@Override
	public long countOfUserByStatus(Status status) {
		Long count = userRepository.countUserByStatus(status);
		return count;
	}

	@Override
	public long countOfuserByRole(Role role) {
		Long count = userRepository.countUserByRole(role);
		return count;
	}

	@Override
	public List<User> findUsersByRole(Role role) {
		List<User> listOfusers = userRepository.findByRole(role);
		return listOfusers;
	}

	@Override
	public User uploadUserImage(String email,String imageUrl) throws IOException {
		System.out.println("upload userimage");
		System.out.println("Email ->"+email);
		System.out.println("imageurl ->"+imageUrl);
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found For This Email :"+email));
		user.setImage(imageUrl);
		User updatedUser = userRepository.save(user);
		System.out.println(updatedUser);
		return updatedUser;
	}

}
