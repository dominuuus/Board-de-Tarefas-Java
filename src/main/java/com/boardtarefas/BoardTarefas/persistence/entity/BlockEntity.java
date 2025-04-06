package com.boardtarefas.BoardTarefas.persistence.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import lombok.Data;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime blocked_at;
    private String block_reason;
    private OffsetDateTime unblocked_at;
    private String unblock_reason;
    
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn() {
        return getFilteredColumn(bc -> bc.getKind().equals(BoardColumnKindEnum.INITIAL));
    }

    private BoardColumnEntity getFilteredColumn (Predicate<BoardColumnEntity> filter) {
        return boardColumns.stream()
                .filter(filter)
                .findFirst()
                .orElseThrow();
    }

    public BoardColumnEntity getCanceledColumn() {
        return getFilteredColumn(bc -> bc.getKind().equals(BoardColumnKindEnum.CANCELED));
    }
}
