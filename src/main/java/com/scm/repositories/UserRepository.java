package com.scm.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.scm.entities.Role;
import com.scm.entities.Status;
import com.scm.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	  Optional<User> findByUsername(String username);
	  Optional<User> findByEmail(String email);
	  long count();
	  long countUserByRole(Role role);
	  long countUserByStatus(Status status);
	  Optional<User> findByUsernameOrEmail(String username,String email);
	  List<User> findByStatus(Status status);
	  List<User> findByRole(Role role);
	  
}
