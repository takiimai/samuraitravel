package com.example.samuraitravel.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.Role;
import com.example.samuraitravel.model.User;
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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));

		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				user.isEnabled(),
				true,
				true,
				true,
				Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())));
	}

	@Override
	@Transactional
	public User registerUser(UserDto userDto) {
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new RuntimeException("このメールアドレスは既に使用されています");
		}

		User user = new User();
		user.setName(userDto.getName());
		user.setFurigana(userDto.getFurigana());
		user.setPostalCode(userDto.getPostalCode());
		user.setAddress(userDto.getAddress());
		user.setPhoneNumber(userDto.getPhoneNumber());
		user.setEmail(userDto.getEmail());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		// デフォルトでは一般ユーザー権限を付与
		Role userRole = roleRepository.findByName("ROLE_GENERAL")
				.orElseThrow(() -> new RuntimeException("デフォルトロールが見つかりません"));
		user.setRole(userRole);
		user.setEnabled(true);

		return userRepository.save(user);
	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + id));
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + email));
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public User updateUser(UserDto userDto) {
		User user = findById(userDto.getId());

		user.setName(userDto.getName());
		user.setFurigana(userDto.getFurigana());
		user.setPostalCode(userDto.getPostalCode());
		user.setAddress(userDto.getAddress());
		user.setPhoneNumber(userDto.getPhoneNumber());

		// メールアドレスが変更されていて、かつ既に使用されている場合はエラー
		if (!user.getEmail().equals(userDto.getEmail()) &&
				userRepository.existsByEmail(userDto.getEmail())) {
			throw new RuntimeException("このメールアドレスは既に使用されています");
		}

		user.setEmail(userDto.getEmail());

		// パスワードが入力されている場合のみ更新
		if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		// ロールが指定されている場合は更新
		if (userDto.getRoleId() != null) {
			Role role = roleRepository.findById(userDto.getRoleId())
					.orElseThrow(() -> new RuntimeException("指定されたロールが見つかりません"));
			user.setRole(role);
		}

		user.setEnabled(userDto.isEnabled());

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
