package com.boardtarefas.BoardTarefas.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id, String title, String description, boolean blocked, OffsetDateTime blockeAt, String blockReason, int blockAmount, Long columnId, String columnName) {
}
