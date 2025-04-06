package com.boardtarefas.BoardTarefas.persistence.entity;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime blocked_at;
    private String block_reason;
    private OffsetDateTime unblocked_at;
    private String unblock_reason;
}
