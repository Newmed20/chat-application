
-- Base de données : `chatapp`


CREATE TABLE `login` (
  `username` varchar(30) primary key NOT NULL,
  `password` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL
);
