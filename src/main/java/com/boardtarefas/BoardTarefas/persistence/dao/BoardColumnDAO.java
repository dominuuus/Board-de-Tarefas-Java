package com.boardtarefas.BoardTarefas.persistence.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnEntity;
import static com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum.findByName;
import com.mysql.cj.jdbc.StatementImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARD_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i++, entity.getBoard().getId());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findBoardId(Long boardId) throws SQLException{
        var sql = "DELETE FROM BOARD_COLUMNS WHERE board_id = ? ORDER BY `order` ASC;";
        List<BoardColumnEntity> list = new ArrayList<>();

        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while(resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                list.add(entity);
            }
            return list;
        } catch (SQLException e) {
            throw e;
        }
    }


}   
