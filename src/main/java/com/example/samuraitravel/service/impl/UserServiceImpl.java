package com.example.samuraitravel.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(
			UserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public User registerNewUser(UserDto userDto) {
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new RuntimeException("Email already in use");
		}

		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPhoneNumber(userDto.getPhoneNumber());

		// デフォルトでユーザーロールを設定
		Role userRole = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("Default role not found"));
		user.setRoles(Collections.singleton(userRole));

		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public User updateUser(UserDto userDto) {
		User user = findById(userDto.getId());

		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPhoneNumber(userDto.getPhoneNumber());

		// パスワードが提供されている場合のみ更新
		if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public boolean emailExists(String email) {
		return userRepository.existsByEmail(email);
	}
}
