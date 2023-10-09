ALTER TABLE comment_entity
    ADD author_id BIGINT;

ALTER TABLE comment_entity
    ADD CONSTRAINT FK_COMMENTENTITY_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES user_entity (id);

ALTER TABLE comment_entity
DROP
COLUMN author;

ALTER TABLE comment_entity
DROP
COLUMN author_avatar;