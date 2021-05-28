INSERT INTO Usuario VALUES (0, 'Administrador' , 1, 'Admin', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN',0, 'Admin');
INSERT INTO Usuario VALUES (1, 'Sanz Mayo' , 1, 'Daniel', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',  'USER', 1200, 'danidassler');
INSERT INTO Usuario VALUES (2, 'Moran Aguero' , 1, 'Sergio', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1200, 'sergiom23');
INSERT INTO Usuario VALUES (3, 'Casado Molinero' , 1, 'Alvaro', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER', 1200, 'alvaro09'); -- bb
INSERT INTO Usuario VALUES (4, 'Ivan Susana' , 1, 'Jin', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'USER',1000, 'Ivanchino07');
INSERT INTO Usuario VALUES (5, 'Peng Zhou' , 1, 'JinTao', '{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', 'ADMIN',1000, 'TheRealJin');

INSERT INTO Producto VALUES (1, 'Zapatillas', 'Nuevas zapatillas colaboracion entre Kanye West y Adidas. Presentan un upper con diferentes materiales como primeknit azul, mesh gris y neopreno negro unidas por una estructura de plastico translucida que brilla en la oscuridad.', 'Adidas Yeezy 700 v3', '42.5');
INSERT INTO Producto VALUES (2, 'Zapatillas', 'Nuevas zapatillas creadas por el rapero Kanye West en colaboracion con la marca alemana. Presenta una mediasuela de BOOST, una tecnologia de adidas para mejorar la comodidad y un upper primeknit que llena zapatilla de diferentes texturas.', 'Adidas Yeezy Boost 350 v2', '45');
INSERT INTO Producto VALUES (3, 'Zapatillas', 'Colaboracion entre Nike y Jordan plasmada en la silueta de la Jordan 1 High OG. Esta zapatilla lanzada en 2019 presenta un upper de cuero con unos detalles en ante desgastado de color azul-verdoso. Ademas contienen un segundo par de cordones.', 'Air Jordan 1 "Turbo Green"', '39');
INSERT INTO Producto VALUES (4, 'Streetwear', 'Sudadera de la conocida marca de Streetwear perneteciente a la temporada FW16. En esta temporada Supreme lanzo al mercado esta sudadera en este color verde oliva, junto con 5 colores mas. Este es uno de los colores mas deseados de la temporada.', 'Supreme Box Logo Hoodie "Olive"', 'L');
INSERT INTO Producto VALUES (5, 'Accesorios', 'Zeppelin inflable de Supreme. Presenta una cuerda larga para poder colgarlo y utilizarlo como decoracion. Perteneciente a la temporada Fall-Winter 17, donde supreme saco un gran numero de accesorios nunca vistos. Hazte con el tuyo ya!', 'Supreme Inflatable Blimp', 'Unica');
INSERT INTO Producto VALUES (6, 'Relojes', 'Reloj de la lujosa marca ROLEX. Solo existen 500 unidades de este lujoso reloj en todo el mundo. En nuestra tienda, puedes obtener este Rolex GTM nuevo y original. Presenta una construcion de acero inoxidable y una esfera negra elegante.', 'Rolex GTM-Master II "Batman"', 'Unica');
INSERT INTO Producto VALUES (7, 'Electronica', 'Consola de ultima generacion de Sony que esta arrasando en el mercado. La consola tiene un color blanco e inluye un mando de color blanco a juego. Con una capacidad de 1TB tienes capacidad para almacenar todos los juegos que necesites!', 'Sony Play Station 5', '1 TB');



INSERT INTO Oferta VALUES (1, 0, /*DATEADD('DAY', 25, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE)*/'2021-05-26', '2021-05-19', NULL, 470, 0, 1, 1, NULL); -- puja dani 470 p1 --
INSERT INTO Oferta VALUES (2, 0, /*DATEADD('MONTH', 1, CURRENT_DATE), DATEADD('DAY', -15, CURRENT_DATE)*/'2021-05-26', '2021-05-19', NULL, 500, 1, NULL, 1, 2); -- precio sergio 500 p1 --
INSERT INTO Oferta VALUES (3, 0, DATEADD('DAY', 2, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE), NULL, 380, 0, 2, 2, NULL); -- puja sergio 380 p2 --
INSERT INTO Oferta VALUES (4, 0, DATEADD('DAY', 6, CURRENT_DATE), DATEADD('DAY', -8, CURRENT_DATE), NULL, 445, 0, 3, 1, NULL); -- puja casado 445 p1 --
INSERT INTO Oferta VALUES (5, 0, DATEADD('DAY', 5, CURRENT_DATE), DATEADD('DAY', -25, CURRENT_DATE), NULL, 510, 1, NULL, 1, 4); -- precio jin 510 p1 --

INSERT INTO Oferta VALUES (6, 1,'2021-10-15', '2021-10-01', '2021-10-09', 405, 0, 1, 2, 3); -- Transaccion dani 405 p2 --
INSERT INTO Oferta VALUES (7, 1,'2021-04-02', '2021-03-22', '2021-04-01', 490, 1, 5, 1, 4); -- Transaccion sergio 405 p1 --
INSERT INTO Oferta VALUES (8, 1,'2021-04-02', '2021-03-22', '2021-04-02', 550, 1, 5, 1, 4); -- Transaccion sergio 405 p1 --