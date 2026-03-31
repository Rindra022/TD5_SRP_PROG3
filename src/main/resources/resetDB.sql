-- 1. Supprime tout et remet les séquences à zéro
TRUNCATE TABLE stock_movement, dish_ingredient, dish, ingredient RESTART IDENTITY CASCADE;

-- 2. Remet les données d'origine
INSERT INTO dish (id, name, dish_type, selling_price) VALUES
                                                          (1, 'Salade fraîche', 'START', 3500.00),
                                                          (2, 'Poulet grillé', 'MAIN', 12000.00),
                                                          (3, 'Riz aux légumes', 'MAIN', null),
                                                          (4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
                                                          (5, 'Salade de fruits', 'DESSERT', null);

INSERT INTO ingredient (id, name, price, category) VALUES
                                                       (1, 'Laitue', 800.00, 'VEGETABLE'),
                                                       (2, 'Tomate', 600.00, 'VEGETABLE'),
                                                       (3, 'Poulet', 4500.00, 'ANIMAL'),
                                                       (4, 'Chocolat', 3000.00, 'OTHER'),
                                                       (5, 'Beurre', 2500.00, 'DAIRY');

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
                                                                                  (1, 1, 0.20, 'KG'),
                                                                                  (1, 2, 0.15, 'KG'),
                                                                                  (2, 3, 1.00, 'KG'),
                                                                                  (4, 4, 0.30, 'KG'),
                                                                                  (4, 5, 0.20, 'KG');

INSERT INTO stock_movement (id_ingredient, quantity, unit, creation_datetime, type) VALUES
                                                                                        (1, 5.0,  'KG', '2024-01-05 08:00', 'IN'),
                                                                                        (1, 0.2,  'KG', '2024-01-06 12:00', 'OUT'),
                                                                                        (2, 4.0,  'KG', '2024-01-05 08:00', 'IN'),
                                                                                        (2, 0.15, 'KG', '2024-01-06 12:00', 'OUT'),
                                                                                        (3, 10.0, 'KG', '2024-01-04 09:00', 'IN'),
                                                                                        (3, 1.0,  'KG', '2024-01-06 13:00', 'OUT'),
                                                                                        (4, 3.0,  'KG', '2024-01-05 10:00', 'IN'),
                                                                                        (4, 0.3,  'KG', '2024-01-06 14:00', 'OUT'),
                                                                                        (5, 2.5,  'KG', '2024-01-05 10:00', 'IN'),
                                                                                        (5, 0.2,  'KG', '2024-01-06 14:00', 'OUT');

-- 3. Resynchronise les séquences
SELECT setval('dish_id_seq', (SELECT MAX(id) FROM dish));
SELECT setval('ingredient_id_seq', (SELECT MAX(id) FROM ingredient));
SELECT setval('dish_ingredient_id_seq', (SELECT MAX(id) FROM dish_ingredient));
SELECT setval('stock_movement_id_seq', (SELECT MAX(id) FROM stock_movement));