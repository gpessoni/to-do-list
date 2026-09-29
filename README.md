# To-Do List - Backend (CRUD)

Aplicação para gerenciamento de tarefas do dia a dia. API REST feita com **Java 17**, **Spring Boot 3** e **PostgreSQL**.

> Sem sistema de login e sem conceito de usuário.

## Entidade: Tarefa

| Campo | Descrição |
|---|---|
| id | Identificador (gerado automaticamente) |
| nome | Obrigatório, até 150 caracteres |
| descricao | Texto livre |
| status | `PENDENTE` (padrão), `EM_ANDAMENTO` ou `CONCLUIDA` |
| observacoes | Texto livre |
| dataCriacao | Preenchida automaticamente ao criar |
| dataAtualizacao | Atualizada automaticamente a cada alteração |

## Pré-requisitos

- Java 17+
- PostgreSQL rodando em `localhost:5432`
- Maven não é necessário (o projeto já inclui o `mvnw`)

## Como rodar

1. **Criar o banco** com o script:
   ```bash
   psql -U postgres -f database/script.sql
   ```
2. **Conferir a conexão** em `src/main/resources/application.properties` (padrão: usuário `postgres`, senha `postgres`, banco `todolist`). Ajuste se o seu for diferente.
3. **Subir a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```
   A API fica em `http://localhost:8080`.

## Endpoints

| Método | URL | Ação | Retorno |
|---|---|---|---|
| POST | `/api/tarefas` | Criar tarefa | 201 |
| GET | `/api/tarefas` | Listar tarefas | 200 |
| GET | `/api/tarefas/{id}` | Buscar por id | 200 / 404 |
| PUT | `/api/tarefas/{id}` | Alterar tarefa | 200 / 404 |
| DELETE | `/api/tarefas/{id}` | Deletar tarefa | 204 / 404 |

Nome vazio ou ausente retorna **400** com `{"erro": "..."}`.

### Exemplos

```bash
# Criar
curl -X POST localhost:8080/api/tarefas -H "Content-Type: application/json" \
  -d '{"nome":"Estudar Spring","descricao":"Ler a documentação","observacoes":"Até sexta"}'

# Listar
curl localhost:8080/api/tarefas

# Alterar
curl -X PUT localhost:8080/api/tarefas/1 -H "Content-Type: application/json" \
  -d '{"nome":"Estudar Spring","status":"CONCLUIDA"}'

# Deletar
curl -X DELETE localhost:8080/api/tarefas/1
```

## Testes

```bash
./mvnw test
```

- **Unitários:** `TarefaServiceTest` (regras do service, com Mockito)
- **Integração:** `TarefaControllerTest` (API completa, com banco H2 em memória — não precisa do PostgreSQL)

## Estrutura

```
database/script.sql        # script do banco (PostgreSQL)
src/main/java/com/fatec/todolist/
├── controller/            # endpoints REST
├── dto/                   # dados de entrada
├── exception/             # exceções e tratamento de erros
├── model/entity|enums|repository/
└── service/ + impl/       # regras de negócio
```

## Pontos importantes

- O `ddl-auto` está em `validate`: o banco **precisa** ser criado pelo script antes de subir a aplicação.
- Se der erro de conexão, confira se o PostgreSQL está ativo e se usuário/senha estão corretos.
- A porta padrão é `8080`; para mudar, adicione `server.port=XXXX` no `application.properties`.
