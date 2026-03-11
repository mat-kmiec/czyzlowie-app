package pl.czyzlowie.modules.user_panel.notes;

import jakarta.persistence.*;
import lombok.*;
import pl.czyzlowie.modules.user_panel.notes.NoteCategory;

import java.time.LocalDateTime;

/**
 * Represents a note entity, which includes various attributes such as title,
 * content, category, and metadata like creation and update timestamps.
 * The notes are associated with users and categorized using the NoteCategory enum.
 *
 * This entity is mapped to the "notes" table in the database and supports features
 * such as automatic generation of unique identifiers, timestamps for creation
 * and updates, as well as column and enum mappings for its properties.
 *
 * The class uses JPA annotations for entity mapping and lifecycle events,
 * and Lombok annotations for reducing boilerplate code like getters, setters,
 * constructors, and builders.
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoteCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}