package pl.czyzlowie.modules.user_panel.notes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class NoteDto {

    public record CreateNoteRequest(
            @NotBlank String noteTitle,
            @NotNull NoteCategory noteCategory,
            @NotBlank String noteContent,
            boolean pinned
    ) {}

    public record Response(
            Long id,
            String title,
            String categoryRawName,
            String categoryDisplayName,
            String categoryIcon,
            String content,
            boolean pinned,
            LocalDateTime createdAt
    ) {
        public static Response fromEntity(Note note) {
            return new Response(
                    note.getId(),
                    note.getTitle(),
                    note.getCategory().name(),
                    note.getCategory().getDisplayName(),
                    note.getCategory().getIconName(),
                    note.getContent(),
                    note.isPinned(),
                    note.getCreatedAt()
            );
        }
    }
}