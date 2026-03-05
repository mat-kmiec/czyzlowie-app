package pl.czyzlowie.modules.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.user.entity.User;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
}
