package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.*;
import com.pathway.JobPathway.entity.Candidate;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.Role;
import com.pathway.JobPathway.exception.DuplicateResourceException;
import com.pathway.JobPathway.repository.CandidateRepository;
import com.pathway.JobPathway.repository.UserRepository;
import com.pathway.JobPathway.security.JwtService;
import com.pathway.JobPathway.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final CandidateRepository candidateRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new DuplicateResourceException("Email already registered: " + request.getEmail());
                }

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.CANDIDATE)
                                .build();
                userRepository.save(user);

                Candidate candidate = Candidate.builder()
                                .user(user)
                                .isEnabled(true)
                                .build();
                candidateRepository.save(candidate);

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .id(user.getId())
                                .token(token)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .build();
        }

        @Override
        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow();

                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .id(user.getId())
                                .token(token)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .profilePicture(user.getProfilePicture())
                                .build();
        }
}
