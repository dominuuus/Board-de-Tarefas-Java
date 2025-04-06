package com.boardtarefas.BoardTarefas.persistence.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.boardtarefas.BoardTarefas.dto.BoardColumnDTO;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnEntity;
import com.boardtarefas.BoardTarefas.persistence.entity.CardEntity;

import static com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum.findByName;
import static java.util.Objects.isNull;

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

    public List<BoardColumnDTO> findBoardByIdDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
                SELECT  bc.id, 
                        bc.name, 
                        bc.kind, 
                        (SELECT COUNT (c.id) FROM CARDS c WHERE c.board_column_id = bc.id) cards_amount
                        FROM BOARD_COLUMNS bc
                        WHERE bc.board_id = ?
                        ORDER BY `order` ASC;
                """;
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                        
                dtos.add(dto);
                
            }
            return dtos;
    }
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql = """
                SELECT  bc.name, 
                        bc.kind,
                        c.id,
                        c.title,
                        c.description
                        FROM BOARD_COLUMNS bc
                        LEFT JOIN CARDS c ON c.board_column_id = bc.id
                        WHERE bc.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setKind(findByName(resultSet.getString("bc.kind")));
                do {
                    var card = new CardEntity();
                    if(isNull(resultSet.getString("c.title"))) {
                        break;
                    }
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while(resultSet.next());
                return Optional.of(entity);
            }
            return Optional.empty();
    }

}
}