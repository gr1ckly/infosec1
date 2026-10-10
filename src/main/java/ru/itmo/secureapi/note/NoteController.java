package ru.itmo.secureapi.note;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.secureapi.note.dto.CreateNoteRequest;
import ru.itmo.secureapi.note.dto.NoteResponse;

@RestController
@RequestMapping("/api/data")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateNoteRequest request) {
        NoteResponse response = noteService.create(ownerId(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NoteResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        return noteService.findAll(ownerId(jwt));
    }

    private UUID ownerId(Jwt jwt) {
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated subject is missing");
        }
        return UUID.fromString(subject);
    }
}
