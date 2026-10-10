USE banco_circadiano;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE avaliacoes;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;

SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_avaliacoes FROM avaliacoes;