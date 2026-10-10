package ru.itmo.secureapi.note;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.secureapi.note.dto.CreateNoteRequest;
import ru.itmo.secureapi.note.dto.NoteResponse;
import ru.itmo.secureapi.user.User;
import ru.itmo.secureapi.user.UserRepository;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteSanitizer sanitizer;

    public NoteService(
            NoteRepository noteRepository,
            UserRepository userRepository,
            NoteSanitizer sanitizer) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.sanitizer = sanitizer;
    }

    @Transactional
    public NoteResponse create(UUID ownerId, CreateNoteRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user does not exist"));
        String title = sanitizeWithinLimit(request.title(), 120, "title");
        String content = sanitizeWithinLimit(request.content(), 2000, "content");
        Note note = new Note(UUID.randomUUID(), owner, title, content, Instant.now());
        return toResponse(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> findAll(UUID ownerId) {
        return noteRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private String sanitizeWithinLimit(String value, int maximumLength, String field) {
        String sanitized = sanitizer.sanitize(value);
        if (sanitized.length() > maximumLength) {
            throw new IllegalArgumentException(field + " exceeds maximum length");
        }
        return sanitized;
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(), note.getTitle(), note.getContent(), note.getCreatedAt());
    }
}
