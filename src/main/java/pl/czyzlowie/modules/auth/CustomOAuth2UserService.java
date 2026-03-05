package pl.czyzlowie.modules.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.*;
import pl.czyzlowie.modules.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        String providerId = registrationId.equals("google") ?
                oAuth2User.getAttribute("sub") :
                oAuth2User.getAttribute("id");

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, providerId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getUsername().equals(name)) {
                user.setUsername(name);
                user = userRepository.save(user);
            }
        } else {
            user = registerNewOAuth2User(email, name, provider, providerId);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User registerNewOAuth2User(String email, String name, AuthProvider provider, String providerId) {
        if (userRepository.existsByEmail(email)) {
            throw new OAuth2AuthenticationException("Konto z tym emailem już istnieje. Zaloguj się tradycyjnie.");
        }

        User newUser = User.builder()
                .email(email)
                .username(name)
                .provider(provider)
                .providerId(providerId)
                .role(Role.USER)
                .isEmailVerified(true)
                .build();

        newUser.setConsents(UserConsents.builder().termsAccepted(true).termsVersion("v1.0").build());
        newUser.setStatistics(UserStatistics.builder().build());

        return userRepository.save(newUser);
    }
}