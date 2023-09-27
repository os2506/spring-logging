package com.dproduction.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.dproduction.controler.UserController;
import com.dproduction.service.UserService;
import com.dproduction.dtos.UserDTO;
import com.dproduction.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRegisterUser() {
		// Créez un objet UserDTO pour votre test
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("john");
		userDTO.setPwd("password");
		userDTO.setEmail("john@example.com");

		// Créez un objet User pour simuler le résultat de la sauvegarde
		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setUsername(userDTO.getUsername());
		savedUser.setPwd(userDTO.getPwd());
		savedUser.setEmail(userDTO.getEmail());

		// Configurez le comportement du service userService.save pour renvoyer
		// savedUser lorsqu'il est appelé avec un utilisateur
		when(userService.save(Mockito.<User>any())).thenReturn(savedUser);


		// Appelez la méthode register avec userDTO
		ResponseEntity<?> response = userController.register(userDTO);

		// Vérifiez que la méthode save du service userService a été appelée avec le bon
		// utilisateur

		verify(userService, times(1)).save(any(User.class));


		// Vérifiez que la réponse est un code de statut HTTP 200 OK
		assertEquals(200, response.getStatusCodeValue());

		// Vérifiez que la réponse contient les informations de l'utilisateur enregistré
		User responseUser = (User) response.getBody();
		assertNotNull(responseUser);
		assertEquals(savedUser.getId(), responseUser.getId());
		assertEquals(savedUser.getUsername(), responseUser.getUsername());
		assertEquals(savedUser.getEmail(), responseUser.getEmail());
	}


	@Test
	public void testGetAllUsers() {

		// Create a UserController instance with the mocked UserService
		UserController userController = new UserController(userService);

		// Create a list of users for testing
		List<User> users = new ArrayList<>();
		// Add user objects to the list as needed

		// Define the behavior of usrService.getAllUsers() when called by the controller
		when(userService.getAllUsers()).thenReturn(users);

		// Call the controller method
		ResponseEntity<List<User>> responseEntity = userController.getAllUsers();

		// Verify the response
		assertEquals(users, responseEntity.getBody());
	}


	@Test
	public void testDeleteUser_ExistingUser() {

		// Create a UserController instance with the mocked UserService
		UserController userController = new UserController(userService);

		// Define a user ID for testing
		Long userId = 52L;

		// Mock the behavior of usrService.findUserById
		User existingUser = new User(); // Create an example user
		when(userService.findUserById(userId)).thenReturn(Optional.of(existingUser));

		// Call the controller method
		ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

		// Verify the response // deprecated
		//assertEquals(204, responseEntity.getStatusCodeValue()); // 204 indicates success (no content)
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        
		// Verify that usrService.deleteUserById was called with the correct user ID
		verify(userService, times(1)).deleteUserById(userId);
	}

	@Test
	public void testDeleteUser_NonExistingUser() {

		// Create a UserController instance with the mocked UserService
		UserController userController = new UserController(userService);

		// Define a user ID for testing
		Long userId = 10L;

		// Mock the behavior of usrService.findUserById for a non-existing user
		when(userService.findUserById(userId)).thenReturn(Optional.empty());

		// Call the controller method
		ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

		// Verify the response - deprecated
		//assertEquals(404, responseEntity.getStatusCodeValue()); // 404 indicates not found
		
	    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

		// Verify that usrService.deleteUserById was not called
		verify(userService, never()).deleteUserById(userId);
	}

}
