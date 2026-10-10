package ru.itmo.secureapi.auth.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
