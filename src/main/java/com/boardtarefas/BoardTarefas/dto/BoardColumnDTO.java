package com.boardtarefas.BoardTarefas.dto;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnKindEnum kind, int cardsAmount) {
}
