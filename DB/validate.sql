USE banco_circadiano;

SHOW TABLES;
DESCRIBE usuarios;
DESCRIBE avaliacoes;

INSERT INTO usuarios (Id, Nome, Email, Senha) VALUES (UUID(),"Maria", "maria122@gmail.com", "maria1234");
SELECT* FROM usuarios;

INSERT INTO avaliacoes (
    Id,
    Usuario_id,
    Nota_regularidade,
    Nota_saude_sono,
    Nota_luz_estimulante,
    Classificacao
)
VALUES (UUID(),(SELECT Id FROM usuarios WHERE Email = 'maria122@gmail.com'), 8, 9, 7, 'Excelente');