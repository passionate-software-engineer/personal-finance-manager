INSERT INTO app_user (username, password, first_name, last_name)
VALUES ('test_user_1', 'test_password_1', 'Test Name 1', 'Test Last 1'),
       ('test_user_2', 'test_password_2', 'Test Name 2', 'Test Last 2'),
       ('test_user_3', 'test_password_3', 'Test Name 3', 'Test Last 3');

INSERT INTO account (name, balance, user_id)
VALUES ('Account 1', 1.0, 1),
       ('Account 2', 2.0, 1),
       ('Account 3', 3.0, 1),
       ('Account 4', 3.0, 2),
       ('Account 5', 3.0, 2);

