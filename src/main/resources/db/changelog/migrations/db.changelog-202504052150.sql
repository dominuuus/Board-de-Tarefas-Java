--liquibase formatted sql
--changeset domingos:202504052150
--comment: blocks table create

CREATE TABLE BLOCKS (
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(200) NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason VARCHAR(200) NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE BLOCKS