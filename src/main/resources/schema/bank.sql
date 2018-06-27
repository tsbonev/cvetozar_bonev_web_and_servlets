CREATE TABLE IF NOT EXISTS users(
  id int NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  PRIMARY KEY(id))

CREATE TABLE IF NOT EXISTS transactions(
  id int NOT NULL AUTO_INCREMENT,
  userId int NOT NULL,
  operation varchar(255) NOT NULL,
  amount double NOT NULL,
  transactionDate date NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY(userId) REFERENCES users(id))

CREATE TABLE IF NOT EXISTS sessions(
  id nvarchar(255) NOT NULL UNIQUE,
  userId int NOT NULL,
  expiresOn timestamp NOT NULL,
  PRIMARY KEY(id, userId),
  FOREIGN KEY(userId) REFERENCES users(id))