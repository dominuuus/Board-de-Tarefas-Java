package com.boardtarefas.BoardTarefas;

import com.boardtarefas.BoardTarefas.persistence.migration.MigrationStrategy;
import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException{
        try(var connection = getConnection()){
            System.out.println("Conectado ao DB");
            new MigrationStrategy(connection).executeMigration();
        }    }
}
