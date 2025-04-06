package com.boardtarefas.BoardTarefas.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.boardtarefas.BoardTarefas.persistence.dao.BoardColumnDAO;
import com.boardtarefas.BoardTarefas.persistence.dao.BoardDAO;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardEntity;

public class BoardQueryService {
    
    private final Connection connection = null;
    
    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if(optional.isPresent()) {
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
