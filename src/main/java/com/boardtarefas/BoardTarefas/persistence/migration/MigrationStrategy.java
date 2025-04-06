package com.boardtarefas.BoardTarefas.persistence.migration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;
    /**
     * Método responsável por executar a migração do banco de dados utilizando o Liquibase.
     * Ele cria um arquivo de log para registrar as mensagens de saída e erro durante o processo.
     * 
     * @throws SQLException se ocorrer um erro ao conectar ao banco de dados
     * @throws LiquibaseException se ocorrer um erro durante a migração
     */

    public void executeMigration() {
        var originalOut = System.out;
        var originalErr = System.err;
        
        try (var fos = new FileOutputStream("liquibase.log")) {
                System.setOut(new PrintStream(fos));
                System.setErr(new PrintStream(fos));
                try (
                    var connection = getConnection();
                    var jdbcconnection = new JdbcConnection(connection);
                    ) {
                        System.out.println("Iniciando a migração...");
                    try (var liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcconnection
                    )) {
                        liquibase.update();
                        System.out.println("Migração concluída com sucessso");
                    }
                } catch (SQLException | LiquibaseException e) {
                    System.err.println("Erro ao executar a migração " + e.getMessage());
                    e.printStackTrace();
                    System.setErr(originalErr);
                }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
        
    }
}
