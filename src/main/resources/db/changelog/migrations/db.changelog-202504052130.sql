--liquibase formatted sql
--changeset domingos:202504052130
--comment: cards table create

CREATE TABLE CARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    board_columnn_id BIGINT NOT NULL,
    CONSTRAINT fk_boards_columns__cards FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS