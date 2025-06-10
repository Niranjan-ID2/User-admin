package com.example.useradmin.security;

import com.example.useradmin.model.User;
import com.example.useradmin.model.UserStatus;
import com.example.useradmin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections; // For simple, single role
// import java.util.Set;
// import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true) // Good practice for read operations
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
                );

        // For JWT authentication, the password stored in UserDetails might not be directly used
        // for authentication by Spring Security if the JWT filter authenticates first.
        // However, it's good practice to populate it. If users don't have traditional passwords
        // (e.g. only SSO or magic links), this might be an empty string or a placeholder.
        // For now, we assume no separate password field is being actively managed for login.
        // A "password" field would typically be on the User entity if traditional login was also supported.
        // Since this app focuses on OTP activation then JWT, we'll use a placeholder for password.
        String password = ""; // Placeholder, as JWT is the primary auth mechanism after activation.
                             // If User entity had a password, it would be user.getPassword().

        // For authorities/roles:
        // If you have a roles system (e.g., user.getRoles() returning a Set<Role>), map them:
        // Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
        //       .map(role -> new SimpleGrantedAuthority(role.getName()))
        //       .collect(Collectors.toSet());
        // For now, let's grant a default "ROLE_USER" to everyone.
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // Check for user status (e.g. is user active, not locked, etc.)
        // The UserDetails object has flags for these.
        boolean accountNonExpired = true; // Can be based on user properties
        boolean credentialsNonExpired = true; // e.g. if password expiry is implemented
        boolean accountNonLocked = user.getStatus() != UserStatus.PENDING_ACTIVATION; // Example: don't allow login if not active
                                                                                  // Consider other statuses like SUSPENDED

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                password, // User's password (hashed if using password-based auth)
                user.getStatus() == UserStatus.ACTIVE, // enabled
                accountNonExpired,      // accountNonExpired
                credentialsNonExpired,  // credentialsNonExpired
                accountNonLocked,       // accountNonLocked
                authorities             // authorities
        );
    }
}
