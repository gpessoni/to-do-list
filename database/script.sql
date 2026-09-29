-- Script do banco de dados - Lista de Tarefas
-- Execute conectado como superusuário (ex.: psql -U postgres -f script.sql)

CREATE DATABASE todolist;
\connect todolist

CREATE TABLE IF NOT EXISTS tarefa (
    id                 BIGSERIAL PRIMARY KEY,
    nome               VARCHAR(150) NOT NULL,
    descricao          TEXT,
    status             VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE'
                       CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA')),
    observacoes        TEXT,
    data_criacao       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
