package com.boardtarefas.BoardTarefas.ui;

import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;
import java.util.Scanner;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnEntity;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardEntity;
import com.boardtarefas.BoardTarefas.persistence.entity.CardEntity;
import com.boardtarefas.BoardTarefas.service.BoardColumnQueryService;
import com.boardtarefas.BoardTarefas.service.BoardQueryService;
import com.boardtarefas.BoardTarefas.service.CardService;
import com.boardtarefas.BoardTarefas.dto.BoardColumnInfoDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("/n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem-vindo ao Board de Tarefas %s! Escolha uma opção\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1. Criar card");
                System.out.println("2. Mover card");
                System.out.println("3. Bloquear card");
                System.out.println("4. Desbloquear card");
                System.out.println("5. Excluir card");
                System.out.println("6. Ver board");
                System.out.println("7. Ver coluna com cards");
                System.out.println("8. Ver card");
                System.out.println("9. Voltar ao menu anterior");
                System.out.println("10. Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCard();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumnWithCards();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando ao menu anterior...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do Card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do Card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()) {
            new CardService(connection).create(card);
        }
    }

    private void moveCard() throws SQLException {
        System.out.println("Informe o ID do Card a ser movido");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).moveCard(cardId, boardColumnsInfo);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Informe o ID da coluna de destino");
        var columnId = scanner.nextLong();
        try(var connection = getConnection()) {
            new CardService(connection).moveCard(cardId, columnId);
        }
     
    }

    private void blockCard() throws SQLException{
        System.out.println("Informe o ID do Card a ser bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o ID do Card a ser desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio");
        var reason = scanner.next();
        try(var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        

    }

    private void cancelCard() throws SQLException{
        System.out.println("Informe o ID do Card a ser excluído");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo); 
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        

    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(board -> {
                System.out.printf("Board [%s,%s]\n", board.id(), board.name());
                board.columns().forEach(column ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards \n", column.name(), column.kind(), column.cardsAmount()));
            });
        }

    }

    private void showColumnWithCards() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while(!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }

        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna [%s] tipo: [%s] tem %s cards \n", co.getName(), co.getKind());
                co.getCards().forEach(card -> System.out.printf("Card [%s] - %s\n Descrição: %s", card.getId(), card.getTitle(), card.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o ID do Card a ser exibido");
        var selectedCard = scanner.nextLong();
        try(var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCard)
                .ifPresentOrElse(
                    c -> {
                        System.out.printf("Card [%s] - %s\n Descrição: %s", c.id(), c.title());
                        System.out.printf("Descrição: %s", c.description());
                        System.out.printf(c.blocked() ?
                            "Card bloqueado em %s, motivo: %s\n" : "Card desbloqueado em %s, motivo: %s\n",
                            c.blockedAt(), c.blockReason());
                        System.out.printf("Já foi bloqueado: %s vezes\n", c.blocksAmount());
                        System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                    },
                    () -> System.out.println("Card não encontrado com o id %s\n", selectedCard));
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }

    }


}
}