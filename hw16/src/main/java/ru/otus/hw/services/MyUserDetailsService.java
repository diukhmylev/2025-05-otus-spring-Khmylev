package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Load user by username='{}'", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found by username='{}'", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("User '{}' successfully loaded with role={}", user.getUsername(), user.getRole());


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
