# Ciclo Circadiano

Aplicação web para autoavaliação de hábitos relacionados ao sono e ao ritmo circadiano. O questionário considera dez perguntas sobre regularidade dos horários, saúde do sono, exposição à luz e consumo de estimulantes. Ao concluir, a aplicação apresenta uma pontuação geral, resultados por domínio e sugestões para reflexão. O resultado é informativo e não substitui uma avaliação profissional.

## Tecnologias

- Java 21 e Spring Boot 4.1.1
- Maven
- MySQL
- HTML, CSS e JavaScript

## Como executar

1. Instale o JDK 21 e o MySQL.
2. Crie um banco de dados MySQL vazio.
3. Configure as variáveis de ambiente do backend em `backend/ciclo-circadiano/.env`:

   ```properties
   DB_URL=jdbc:mysql://localhost:3306/banco_circadiano?useSSL=false&allowPublicKeyRetrieval=true
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   JWT_SECRET=uma_chave_secreta_com_pelo_menos_32_caracteres
   ```

4. Inicie o backend:

   ```powershell
   cd backend\ciclo-circadiano
   .\mvnw.cmd spring-boot:run
   ```

   A API estará disponível em `http://localhost:8080/api`. O Hibernate atualiza as tabelas do banco conforme as entidades da aplicação.

5. Sirva os arquivos da pasta `frontend` com um servidor web local e abra `index.html`. A interface usa a API em `http://localhost:8080`.

Para executar os testes automatizados do backend:

```powershell
cd backend\ciclo-circadiano
.\mvnw.cmd test
```

## Uso da aplicação

1. Crie uma conta na tela de cadastro.
2. Entre com e-mail e senha.
3. Inicie a avaliação e responda às dez perguntas. Considere, de preferência, os últimos 14 dias.
4. Consulte a pontuação geral, a classificação e os resultados por domínio.
5. Na página inicial, consulte a avaliação mais recente ou saia da conta.

As respostas do questionário feitas pela interface web são processadas no dispositivo e não são enviadas ao backend. As rotas de avaliação documentadas abaixo permitem consultar e registrar avaliações pela API.

## API

Todas as rotas usam o prefixo `/api`. As rotas de cadastro e login são públicas. As demais exigem o cabeçalho:

```http
Authorization: Bearer <token>
```

### Rotas públicas

| Método | Rota | Descrição | Respostas principais |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Cadastra um usuário | `201` sucesso, `409` e-mail já cadastrado, `400` dados inválidos |
| `POST` | `/api/auth/login` | Autentica um usuário | `200` sucesso, `401` credenciais inválidas, `400` dados inválidos |

Exemplo de cadastro:

```json
{
  "nome": "Nome do usuário",
  "email": "usuario@example.com",
  "senha": "senha12345"
}
```

Exemplo de login:

```json
{
  "email": "usuario@example.com",
  "senha": "senha12345"
}
```

As respostas de sucesso incluem o token de autenticação. Envie o token como `Bearer <token>` nas rotas protegidas.

### Rotas autenticadas

| Método | Rota | Descrição | Respostas principais |
| --- | --- | --- | --- |
| `GET` | `/api/perguntas` | Lista as perguntas e opções do questionário | `200` sucesso |
| `GET` | `/api/avaliacoes/ultima` | Consulta a avaliação mais recente do usuário | `200` com avaliação, `204` se ainda não houver |
| `GET` | `/api/avaliacoes` | Lista as avaliações do usuário | `200` sucesso |
| `POST` | `/api/avaliacoes` | Cria uma avaliação com todas as respostas | `201` sucesso, `400` respostas inválidas ou incompletas |

Para criar uma avaliação, consulte primeiro `GET /api/perguntas` e envie uma resposta para cada pergunta, usando os identificadores retornados:

```json
{
  "respostas": [
    {
      "perguntaId": 1,
      "opcaoId": 1
    },
    {
      "perguntaId": 2,
      "opcaoId": 2
    }
  ]
}
```

O exemplo mostra apenas duas respostas. Na requisição real, inclua uma resposta para cada pergunta listada pela API.
