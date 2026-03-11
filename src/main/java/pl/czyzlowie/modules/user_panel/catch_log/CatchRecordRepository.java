package pl.czyzlowie.modules.user_panel.catch_log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link CatchRecord} entities.
 * This interface extends {@link JpaRepository}, providing CRUD operations and additional
 * query methods for {@link CatchRecord}.
 *
 * The repository allows retrieval of fishing catch records based on specified criteria.
 * It is automatically implemented by Spring Data JPA, supporting dynamic query derivation.
 *
 * Methods:
 * - `findAllByUserIdOrderByCatchDateDesc`: Retrieves a paginated list of catch records
 *   associated with a specific user, ordered by the catch date in descending order.
 */
@Repository
public interface CatchRecordRepository extends JpaRepository<CatchRecord, Long> {
    Page<CatchRecord> findAllByUserIdOrderByCatchDateDesc(Long userId, Pageable pageable);
}
