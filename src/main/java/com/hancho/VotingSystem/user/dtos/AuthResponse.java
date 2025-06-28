package com.hancho.VotingSystem.user.dtos;

import java.util.Date;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    Date expiresAt,
    Long id,
    String email,
    String name,
    String department) {}
