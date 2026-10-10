CREATE DATABASE IF NOT EXISTS banco_circadiano;

USE banco_circadiano;

CREATE TABLE usuarios(
Id CHAR(36) PRIMARY KEY,
Nome VARCHAR(100) NOT NULL,
Email VARCHAR(175) NOT NULL UNIQUE,
Senha VARCHAR(255) NOT NULL,
Data_de_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE avaliacoes(
Id CHAR(36) PRIMARY KEY,
Usuario_id CHAR(36) NOT NULL,
Pontuacao_Total INT GENERATED ALWAYS AS (Nota_regularidade + Nota_saude_sono + Nota_luz_estimulante)
VIRTUAL NOT NULL,
Nota_regularidade INT NOT NULL CHECK (Nota_regularidade BETWEEN 0 AND 10),
Nota_saude_sono INT NOT NULL CHECK (Nota_saude_sono BETWEEN 0 AND 10),
Nota_luz_estimulante INT NOT NULL CHECK (Nota_luz_estimulante BETWEEN 0 AND 10),
Classificacao VARCHAR(20) NOT NULL,
Data_avaliacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (Usuario_id) REFERENCES usuarios(Id)
);