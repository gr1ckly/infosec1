package ru.itmo.secureapi.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 2000) String content) {
}
