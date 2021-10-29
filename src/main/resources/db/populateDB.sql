DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meal;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meal (description, calories, user_id)
VALUES ('breakfast', 500, 100000),
       ('lunch', 700, 100001);
