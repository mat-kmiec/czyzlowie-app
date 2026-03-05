package pl.czyzlowie.modules.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.czyzlowie.modules.user.entity.AuthProvider;
import pl.czyzlowie.modules.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"consents", "statistics"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"consents", "statistics"})
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}