package com.boardtarefas.BoardTarefas.service;

import static com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum.CANCELED;
import static com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum.FINAL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.boardtarefas.BoardTarefas.dto.BoardColumnInfoDTO;
import com.boardtarefas.BoardTarefas.persistence.dao.BlockDAO;
import com.boardtarefas.BoardTarefas.persistence.dao.CardDAO;
import com.boardtarefas.BoardTarefas.persistence.entity.CardEntity;
import com.boardtarefas.BoardTarefas.exception.CardBlockedException;
import com.boardtarefas.BoardTarefas.exception.CardFinishedException;
import com.boardtarefas.BoardTarefas.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("Card não encontrado com o id %s".formatted(cardId))
            );
            if(dto.blocked()) {
                var message = "Card bloqueado, desbloqueie antes de mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card já pertence a outro board"));
            if(currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado, não pode ser movido");
            }
            var nextColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("Nenhum card encontrado com o id %s".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "Card bloqueado, desbloqueie antes de cancelar".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Card já pertence a outro board"));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardBlockedException("O card já foi finalizado, não pode ser cancelado");
            }
            boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }


    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("Card não encontrado com o id %s".formatted(id))
            );
            if (dto.blocked()) {
                var message = "Card bloqueado, desbloqueie antes de cancelar".formatted(id);
                throw new CardBlockedException(message);
            }
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCELED)) {
                var message = "O card está na coluna do tipo %s e não pode ser bloqueado".formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                        () -> new EntityNotFoundException("Card não encontrado com o id %s".formatted(id))
            );
            if(!dto.blocked()) {
                var message = "Card %s não está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}