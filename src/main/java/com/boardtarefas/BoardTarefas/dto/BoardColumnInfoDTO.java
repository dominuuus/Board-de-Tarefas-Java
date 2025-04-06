package com.boardtarefas.BoardTarefas.dto;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
} 
