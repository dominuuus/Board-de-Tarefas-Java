package com.boardtarefas.BoardTarefas.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.boardtarefas.BoardTarefas.persistence.dao.CardDAO;
import com.boardtarefas.BoardTarefas.persistence.entity.CardEntity;

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

}
