package com.boardtarefas.BoardTarefas.ui;

import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnEntity;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardColumnKindEnum;
import com.boardtarefas.BoardTarefas.persistence.entity.BoardEntity;
import com.boardtarefas.BoardTarefas.service.BoardQueryService;
import com.boardtarefas.BoardTarefas.service.BoardService;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem-vindo ao Board de Tarefas! Escolha uma opção");
        var option = -1;
        while (true) {
            System.out.println("1. Criar Board");
            System.out.println("2. Selecionar Board");
            System.out.println("3. Excluir Board");
            System.out.println("4. Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.out.println("Saindo do programa...");          
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do Board:");
        entity.setName(scanner.next());

        System.out.println("Digite a quantidade adicionais do seu Board. Digite 0 e serão adicionadas apenas as colunas Inicial, Final e Cancelada:");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna de tarefas iniciais do Board: ");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, BoardColumnKindEnum.INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna adicional do Board: ");
            var pendingColumnName = scanner.next();
            var pendingColumn =    createColumn(pendingColumnName, BoardColumnKindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna de tarefas finais do board:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, BoardColumnKindEnum.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de tarefas canceladas do board:");
        var canceledColumnName = scanner.next();
        var canceledColumn = createColumn(canceledColumnName, BoardColumnKindEnum.CANCELED, additionalColumns + 2);
        columns.add(canceledColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do Board que deseja selecionar: ");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
            );  
        }

    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do Board que deseja excluir: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("Board %s excluído com sucesso \n", id);
            } else {
                System.out.printf("Erro ao excluir o Board %s :", id, "/n Verifique se o ID está correto.");
            }
        }
    }


    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }


    
}
