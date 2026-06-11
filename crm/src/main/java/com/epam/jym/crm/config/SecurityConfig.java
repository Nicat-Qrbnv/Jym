package com.epam.jym.crm.config;

import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.UserRepository;
import com.epam.jym.crm.service.BruteForceProtectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;
  private final BruteForceProtectionService bruteForceProtectionService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/v1/trainees", "/api/v1/trainers")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/auth/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      if (bruteForceProtectionService.isBlocked(username)) {
        throw new LockedException("User is temporarily blocked");
      }
      return userRepository
          .findByUsername(username)
          .map(this::toUserDetails)
          .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private UserDetails toUserDetails(User user) {
    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword())
        .disabled(!user.isActive())
        .roles("USER")
        .build();
  }
}
