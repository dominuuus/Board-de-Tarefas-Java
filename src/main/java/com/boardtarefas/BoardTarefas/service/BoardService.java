package com.boardtarefas.BoardTarefas.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.boardtarefas.BoardTarefas.persistence.dao.BoardColumnDAO;
import com.boardtarefas.BoardTarefas.persistence.dao.BoardDAO;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardService {
    private final Connection connection;

    public BoardEntity insert (final BoardEntity entity) throws Exception {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        try{
            dao.insert(entity);
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();
            for (var column : columns) {
                boardColumnDAO.insert(column);
            }
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
    } return entity;
        }

    public boolean delete(final Long id) throws Exception {
        var dao = new BoardDAO(connection);
        try {
            if(!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
