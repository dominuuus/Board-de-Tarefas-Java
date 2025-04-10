--liquibase formatted sql
--changeset domingos:202504052100
--comment: boards_columns table create

CREATE TABLE BOARD_COLUMNS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `order` int NOT NULL,
    kind VARCHAR(100) NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_id, `order`)
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS_COLUMNS