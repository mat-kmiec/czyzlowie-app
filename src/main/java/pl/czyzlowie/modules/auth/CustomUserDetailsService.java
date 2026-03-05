package pl.czyzlowie.modules.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.AuthProvider;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika z emailem: " + email));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new BadCredentialsException("Zaloguj się używając opcji: " + user.getProvider());
        }

        if (!user.isEmailVerified()) {
            throw new DisabledException("Konto nie zostało jeszcze aktywowane. Sprawdź swoją skrzynkę email (również folder SPAM) i kliknij w link.");
        }

        return new CustomUserDetails(user);
    }
}
