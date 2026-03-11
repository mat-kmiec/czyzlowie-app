package pl.czyzlowie.modules.user_panel.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    private Long getUserIdByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika: " + username));
    }

    @Transactional(readOnly = true)
    public Page<NoteDto.Response> getAllNotes(String username, Pageable pageable) {
        Long userId = getUserIdByUsername(username);
        return noteRepository.findAllByUserIdOrderByPinnedDescCreatedAtDesc(userId, pageable)
                .map(NoteDto.Response::fromEntity);
    }

    @Transactional(readOnly = true)
    public long getTotalCount(String username) {
        Long userId = getUserIdByUsername(username);
        return noteRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getPinnedCount(String username) {
        Long userId = getUserIdByUsername(username);
        return noteRepository.countByUserIdAndPinnedTrue(userId);
    }

    @Transactional
    public void updateNote(Long noteId, NoteDto.CreateNoteRequest request, String username) {
        Long userId = getUserIdByUsername(username);

        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Notatka nie istnieje lub brak uprawnień."));

        note.setTitle(request.noteTitle());
        note.setCategory(request.noteCategory());
        note.setContent(request.noteContent());
        note.setPinned(request.pinned());

        noteRepository.save(note);
    }

    @Transactional
    public void createNote(NoteDto.CreateNoteRequest request, String username) {
        Long userId = getUserIdByUsername(username);

        Note note = Note.builder()
                .userId(userId)
                .title(request.noteTitle())
                .category(request.noteCategory())
                .content(request.noteContent())
                .pinned(request.pinned())
                .build();

        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId, String username) {
        Long userId = getUserIdByUsername(username);
        noteRepository.deleteByIdAndUserId(noteId, userId);
    }
}