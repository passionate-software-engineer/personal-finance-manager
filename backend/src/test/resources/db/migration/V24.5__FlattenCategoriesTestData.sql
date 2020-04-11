INSERT INTO category (id, name, parent_category_id, user_id)
VALUES (1, 'Category 1', null, 1),
       (2, 'Category 2', 1, 1),
       (3, 'Category 3', 2, 1),
       (4, 'Category 4', 3, 1),
       (5, 'Category 5', 4, 1),
       (6, 'Category 6', null, 2),
       (7, 'Category 7', 6, 2),
       (8, 'Category 8', null, 3),
       (9, 'Category 9', 8, 3),
       (10, 'Category 10', 9, 3),
       (11, 'Category 11', 9, 3),
       (12, 'Category 12', 8, 3);
