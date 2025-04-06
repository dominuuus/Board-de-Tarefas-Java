package com.boardtarefas.BoardTarefas.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BoardColumnEntity {
    private Long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private BoardEntity board = new BoardEntity();
    private List<CardEntity> cards = new ArrayList<>();

}
