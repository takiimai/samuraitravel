package com.example.samuraitravel.service.impl;

import java.util.Collections;
import java.util.List;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.samuraitravel.model.VerificationToken;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.repository.VerificationTokenRepository;
import com.example.samuraitravel.service.EmailService;
import com.example.samuraitravel.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            VerificationTokenRepository verificationTokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()))
        );
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
        
        // メール認証を追加したので、初期状態はenabledをfalseに設定
        user.setEnabled(false);
        
        User savedUser = userRepository.save(user);
        
        // 確認トークンを作成してメール送信
        try {
            createVerificationToken(savedUser);
            logger.info("確認トークン作成・メール送信成功: {}", savedUser.getEmail());
        } catch (Exception e) {
            logger.error("確認トークン作成・メール送信失敗: {}", e.getMessage(), e);
        }
        
        return savedUser;
    }
    
    @Override
    @Transactional
    public void createVerificationToken(User user) {
        try {
            logger.info("トークン作成開始 - User ID: {}", user.getId());
            
            // 既存のトークンがあれば削除
            verificationTokenRepository.findByUser(user).ifPresent(token -> {
                logger.info("既存トークン発見: {}", token.getToken());
                verificationTokenRepository.delete(token);
            });
            
            // 新しいトークンを作成
            logger.info("新規トークン作成...");
            VerificationToken token = new VerificationToken(user);
            logger.info("作成済トークン: {}", token.getToken());
            
            // トークンを保存
            verificationTokenRepository.save(token);
            logger.info("トークン保存成功");
            
            // 確認メールを送信
            try {
                emailService.sendVerificationEmail(user.getEmail(), token.getToken());
                logger.info("確認メール送信成功");
            } catch (MessagingException e) {
                logger.error("確認メール送信失敗: {}", e.getMessage(), e);
                throw new RuntimeException("確認メールの送信に失敗しました", e);
            }
        } catch (Exception e) {
            logger.error("エラータイプ: {} - エラーメッセージ: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElse(null);
        
        if (verificationToken == null) {
            logger.warn("トークンが見つかりません: {}", token);
            return "invalid";
        }
        
        if (verificationToken.isExpired()) {
            logger.warn("トークンの有効期限切れ: {}", token);
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        
        User user = verificationToken.getUser();
        verifyUser(user);
        verificationTokenRepository.delete(verificationToken);
        logger.info("トークン検証成功: {}", token);
        
        return "valid";
    }
    
    @Override
    @Transactional
    public void verifyUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
        logger.info("ユーザーを有効化しました: {}", user.getEmail());
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
    
    @Override
    @Transactional
    public void resendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + email));
        
        if (user.isEnabled()) {
            logger.info("ユーザーは既に有効化されています: {}", email);
            throw new RuntimeException("このアカウントは既に有効化されています");
        }
        
        try {
            createVerificationToken(user);
            logger.info("確認トークンを再送信しました: {}", email);
        } catch (Exception e) {
            logger.error("確認トークン再送信失敗: {}", e.getMessage(), e);
            throw new RuntimeException("確認メールの再送信に失敗しました", e);
        }
    }
}
