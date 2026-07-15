package com.prueba.backend.service;

import com.prueba.backend.dto.UserDto;
import com.prueba.backend.model.User;
import com.prueba.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertToDto(user);
    }

    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con el nombre de usuario: " + user.getUsername());
        }
        User saved = userRepository.save(user);
        return convertToDto(saved);
    }

    public UserDto updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (!user.getUsername().equals(userDetails.getUsername()) && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Ya existe otro usuario con el nombre de usuario: " + userDetails.getUsername());
        }

        user.setUsername(userDetails.getUsername());
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.getActive());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!user.getActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return convertToDto(user);
    }

    public UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getRole(), user.getActive());
    }
}
