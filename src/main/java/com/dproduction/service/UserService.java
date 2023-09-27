package com.dproduction.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.dproduction.entity.User;
import com.dproduction.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		Optional<User> usr = userRepository.findById(id);
		return usr.orElse(null);
	}

	public String GetEmailAndUsername(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if (user != null) {
			return user.get().getEmail() + " " + user.get().getUsername();
		} else {
			return "";
		}
	}

	public User save(User usr) {
		return userRepository.save(usr);
	}

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}

	// public int getUserCount() {
	// return userRepository.getUserCount();
	// }

	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public String findPasswordByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user != null) {
			return user.get().getPwd();
		}
		return null;
	}
}
