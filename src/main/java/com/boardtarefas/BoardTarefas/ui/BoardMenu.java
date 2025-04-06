package com.boardtarefas.BoardTarefas.ui;

import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;
import java.util.Scanner;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardEntity;
import com.boardtarefas.BoardTarefas.persistence.entity.CardEntity;

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
                    case 5 -> deleteCard();
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

    private void moveCard() {
     
    }

    private Object blockCard() {

    }

    private Object unblockCard() {

    }

    private Object deleteCard() {

    }

    private Object showBoard() {

    }

    private Object showColumnWithCards() {

    }

    private Object showCard() {

    }


}
