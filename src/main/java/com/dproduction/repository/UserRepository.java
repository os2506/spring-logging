package com.dproduction.repository;


import com.dproduction.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
		Optional<User> findByUsername(String username);
	  
		Optional<User> findByEmail(String email);
	 
	//	public int getUserCount();
	  
		User save(User user);
	 	
}
