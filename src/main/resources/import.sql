INSERT INTO Usuario VALUES (1, 'Sanz Mayo' , 1, 'Daniel', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1200, 'danidassler');
INSERT INTO Usuario VALUES (2, 'Moran Aguero' , 1, 'Sergio', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1200, 'sergiom23');
INSERT INTO Usuario VALUES (3, 'Casado Molinero' , 1, 'Alvaro', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1200, 'alvaro09');
INSERT INTO Usuario VALUES (4, 'Ivan Susana' , 1, 'Jin', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1000, 'Ivanchino07');
INSERT INTO Usuario VALUES (5, 'Peng Zhou' , 1, 'JinTao', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN', 1000, 'TheRealJin');

INSERT INTO Producto VALUES (1, 'Zapatillas', 'Nuevas zapatillas colaboracion con Kanye West.', 'Adidas Yeezy 700', '42.5');
INSERT INTO Producto VALUES (2, 'Zapatillas', 'Nuevas zapatillas con un disenio rompedor', 'Adidas Yeezy 500', '45');

INSERT INTO Oferta VALUES (1, 0,'2021-10-10T22:25', '2021-10-01T14:28', NULL, 470, 0, 1, 1, NULL); -- puja dani 470 p1 --
INSERT INTO Oferta VALUES (2, 0,'2021-11-15T12:57', '2021-11-05T10:55', NULL, 500, 1, NULL, 1, 2); -- precio sergio 500 p1 --
INSERT INTO Oferta VALUES (3, 0,'2021-07-24T01:13', '2021-06-30T05:04', NULL, 380, 0, 2, 2, NULL); -- puja sergio 380 p2 --
INSERT INTO Oferta VALUES (4, 0,'2021-03-27T20:25', '2021-03-20T23:45', NULL, 445, 0, 3, 1, NULL); -- puja casado 445 p1 --
INSERT INTO Oferta VALUES (5, 0,'2021-04-17T22:50', '2021-04-10T10:23', NULL, 510, 1, NULL, 1, 4); -- precio jin 510 p1 --

INSERT INTO Oferta VALUES (6, 1,'2021-10-15T22:25', '2021-10-01T14:28', '2021-10-09T11:58', 405, 0, 1, 2, 3); -- Transaccion dani 405 p2 --
INSERT INTO Oferta VALUES (7, 1,'2021-04-02T22:50', '2021-03-22T10:23', '2021-04-01T11:58', 490, 1, 5, 1, 4); -- Transaccion sergio 405 p1 --
INSERT INTO Oferta VALUES (8, 1,'2021-04-02T22:50', '2021-03-22T10:23', '2021-04-02T11:58', 550, 1, 5, 1, 4); -- Transaccion sergio 405 p1 --

INSERT INTO Mensaje VALUES (1, '2021-05-10T11:25', 'Donde esta mi pedido', 1, 3); -- usuario 1 emisor --
INSERT INTO Mensaje VALUES (2, '2021-05-10T16:12', 'Su pedido esta en camino, relajese', 3, 1); -- usuario 1 receptor --
INSERT INTO Mensaje VALUES (3, '2021-05-22T10:15', 'Sabes si puedo devolver las yeezy?', 2, 4); -- usuario 2 emisor, 4 receptor --
INSERT INTO Mensaje VALUES (4, '2021-05-10T14:47', 'Si claro, si aun no has abierto el paquete', 4, 2); -- usuario 4 emisor, 2 receptor --