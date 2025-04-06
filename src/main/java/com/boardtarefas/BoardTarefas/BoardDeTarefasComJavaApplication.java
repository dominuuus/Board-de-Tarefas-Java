package com.boardtarefas.BoardTarefas;

import static com.boardtarefas.BoardTarefas.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.boardtarefas.BoardTarefas.persistence.migration.MigrationStrategy;

@SpringBootApplication
public class BoardDeTarefasComJavaApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(BoardDeTarefasComJavaApplication.class, args);

		try(var connection = getConnection()){
            System.out.println("Conectado ao DB");
            new MigrationStrategy(connection).executeMigration();
        }    }
	}

