package pl.czyzlowie.modules.user_panel.notes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing `Note` entities in the persistence layer.
 * This interface extends `Jpa*/
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findAllByUserIdOrderByPinnedDescCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndPinnedTrue(Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}