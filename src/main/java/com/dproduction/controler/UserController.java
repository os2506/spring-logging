package com.dproduction.controler;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dproduction.dtos.UserDTO;
import com.dproduction.entity.User;
import com.dproduction.repository.UserRepository;
import com.dproduction.service.UserService;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private UserService usrService;

	@Autowired
	UserRepository userRepository;

	public UserController(UserService usrService) {
		this.usrService = usrService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
		logger.info("--> function register---");

		try {
			// Create a new user entity from the DTO
			User user = new User();
			// String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
			user.setUsername(userDTO.getUsername());
			user.setPwd(userDTO.getPwd());
			user.setEmail(userDTO.getEmail());
			// Register the user
			User registeredUser = usrService.save(user);
		
			logger.info("registeredUser - Id user: {}", registeredUser.getId());
			logger.info("registeredUser - Username user: {}", registeredUser.getUsername());
			logger.info("registeredUser - Email user: {}", registeredUser.getEmail());
		
			// Return a success response with the registered user's information
			return ResponseEntity.ok(registeredUser);
		} catch (Exception e) {
			logger.error("Error while getting users", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GetMapping("/")
	public ResponseEntity<List<User>> getAllUsers() {
		logger.info("--> function getAllUsers---");
		try {
			List<User> users = usrService.getAllUsers();
			logger.info("Nb users: {}", users.size());
			if (users != null) {
				return ResponseEntity.ok(users);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Error while getting all users", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		logger.info("--> function getUserById---");
		try {
			Optional<User> user = usrService.findUserById(id);
			logger.info("getUserById - Id user: {}", user.get().getId());
			logger.info("getUserById - Username user: {}", user.get().getUsername());
			logger.info("getUserById - Email user: {}", user.get().getEmail());
			if (user.isPresent()) {
				return ResponseEntity.ok(user.get());
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Error while getting users by id", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		logger.info("--> function deleteUser---");
		try {
			// Check if user exists
			Optional<User> user = usrService.findUserById(id);
			logger.info("deleteUser - Id user: {}", user.get().getId());
			if (user.isPresent()) {
				usrService.deleteUserById(id);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Error while deleting user by id", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
}
