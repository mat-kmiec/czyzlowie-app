package pl.czyzlowie.modules.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.czyzlowie.modules.user.entity.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}