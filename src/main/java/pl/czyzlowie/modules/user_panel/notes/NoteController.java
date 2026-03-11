package pl.czyzlowie.modules.user_panel.notes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for managing user notes. Provides endpoints for displaying, adding,
 * deleting, and editing user notes. All routes are secured and require authentication.
 */
@Controller
@RequestMapping("/notatki")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * Handles the request to display user notes in a paginated format.
     *
     * @param page the current page number, defaults to 0 if not specified
     * @param size the number of notes per page, defaults to 10 if not specified
     * @param principal the currently authenticated user
     * @param model the model object for adding attributes to render in the view
     * @return the name of the view to be rendered
     */
    @GetMapping
    public String showNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal,
            Model model) {

        String username = principal.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteDto.Response> notesPage = noteService.getAllNotes(username, pageable);
        model.addAttribute("notesPage", notesPage);

        model.addAttribute("totalCount", noteService.getTotalCount(username));
        model.addAttribute("pinnedCount", noteService.getPinnedCount(username));
        model.addAttribute("categories", NoteCategory.values());

        return "profil/private-notes";
    }

    /**
     * Handles the HTTP POST request to add a new note.
     *
     * @param request   the data transfer object containing note creation details, validated for correctness
     * @param principal the security principal representing the currently authenticated user
     * @return a redirect string to the user's notes page
     */
    @PostMapping("/dodaj")
    public String addNote(@ModelAttribute @Valid NoteDto.CreateNoteRequest request, Principal principal) {
        noteService.createNote(request, principal.getName());
        return "redirect:/notatki";
    }

    /**
     * Deletes a note with the specified ID associated with the currently authenticated user.
     *
     * @param id the unique identifier of the note to be deleted
     * @param principal the security principal representing the currently authenticated user
     * @return a string indicating the redirection path after the note is successfully deleted
     */
    @PostMapping("/{id}/usun")
    public String deleteNote(@PathVariable Long id, Principal principal) {
        noteService.deleteNote(id, principal.getName());
        return "redirect:/notatki";
    }

    /**
     * Edits an existing note based on the provided request data.
     *
     * @param id the unique identifier of the note to be edited
     * @param request the request object containing the updated note information
     * @param principal the security principal representing the currently authenticated user
     * @return a redirection string indicating the endpoint to navigate to after editing the note
     */
    @PostMapping("/{id}/edytuj")
    public String editNote(@PathVariable Long id, @ModelAttribute @Valid NoteDto.CreateNoteRequest request, Principal principal) {
        noteService.updateNote(id, request, principal.getName());
        return "redirect:/notatki";
    }
}