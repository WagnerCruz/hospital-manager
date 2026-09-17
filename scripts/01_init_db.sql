CREATE SCHEMA IF NOT EXISTS hospital;

-- ALTER USER hospital OWNER TO postgres;

----PERMISSOES
CREATE TABLE IF NOT EXISTS hospital.permissoes
(
    id        UUID         NOT NULL,
    nome      VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT pk_permissoes PRIMARY KEY (id)
    );

ALTER TABLE hospital.permissoes
    ADD CONSTRAINT uc_permissoes_nome UNIQUE (nome);

----PERFIS
CREATE TABLE IF NOT EXISTS hospital.perfil_permissao
(
    perfil_id    UUID NOT NULL,
    permissao_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS hospital.perfis
(
    id        UUID         NOT NULL,
    nome      VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT pk_perfis PRIMARY KEY (id)
    );

ALTER TABLE hospital.perfis
    ADD CONSTRAINT uc_perfis_nome UNIQUE (nome);

ALTER TABLE hospital.perfil_permissao
    ADD CONSTRAINT fk_perper_on_perfil FOREIGN KEY (perfil_id) REFERENCES hospital.perfis (id);

ALTER TABLE hospital.perfil_permissao
    ADD CONSTRAINT fk_perper_on_permissao FOREIGN KEY (permissao_id) REFERENCES hospital.permissoes (id);

----USUARIOS
CREATE TABLE IF NOT EXISTS hospital.usuario_perfil
(
    perfil_id  UUID NOT NULL,
    usuario_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS hospital.usuarios
(
    id               UUID         NOT NULL,
    login            VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    nome             VARCHAR(255) NOT NULL,
    senha            VARCHAR(255) NOT NULL,
    ativo            BOOLEAN      NOT NULL,
    data_criacao     TIMESTAMP WITHOUT TIME ZONE,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id)
    );

ALTER TABLE hospital.usuarios
    ADD CONSTRAINT uc_usuarios_email UNIQUE (email);

ALTER TABLE hospital.usuarios
    ADD CONSTRAINT uc_usuarios_login UNIQUE (login);

ALTER TABLE hospital.usuario_perfil
    ADD CONSTRAINT fk_usuper_on_perfil FOREIGN KEY (perfil_id) REFERENCES hospital.perfis (id);

ALTER TABLE hospital.usuario_perfil
    ADD CONSTRAINT fk_usuper_on_usuario FOREIGN KEY (usuario_id) REFERENCES hospital.usuarios (id);

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO hospital.usuarios (id, login, nome, email, senha, ativo)
VALUES
    (gen_random_uuid(), 'admin', 'Administrador', 'admin@example.com', '$2a$12$ERt5PPqRsP45iQB8.dbefeyIltATCKxrrplinZHUzw2FwDiPliYBm', true),
    (gen_random_uuid(), 'paciente', 'Paciente', 'paciente@example.com', '$2a$12$n6ltKZwv4aE.Z3OGm9c.UuoUgsnkPJ15BunCmo382PM0GqDJSCMQC', true),
    (gen_random_uuid(), 'medico', 'Médico', 'medico@example.com', '$2a$12$xn1S8KkDTzZHmFUNF8gWAe39taS94e6TQC6XGnBF/Y6g16qmjhoU6', true),
    (gen_random_uuid(), 'enfermeiro', 'Enfermeiro', 'enfermeiro@example.com', '$2a$12$/bcOb1GmOuPXD81zof.8IenjcedJwYuKorw3D1RbTIf77nA42NZj2', true);
INSERT INTO hospital.perfis (id, nome, descricao)
VALUES
    (gen_random_uuid(), 'ADMINISTRADOR', 'Administrador'),
    (gen_random_uuid(), 'PACIENTE', 'Paciente'),
    (gen_random_uuid(), 'MEDICO', 'Médico'),
    (gen_random_uuid(), 'ENFERMEIRO', 'Enfermeiro');

INSERT INTO hospital.permissoes (id, nome, descricao)
VALUES
    (gen_random_uuid(), 'USUARIO_ATUALIZAR', 'Atualizar usuário'),
    (gen_random_uuid(), 'USUARIO_ATUALIZAR_SENHA', 'Atualizar senha'),
    (gen_random_uuid(), 'USUARIO_VISUALIZAR', 'Visualizar usuário'),
    (gen_random_uuid(), 'ADMINISTRADOR_USUARIO_CRIAR', 'Criar usuários'),
    (gen_random_uuid(), 'ADMINISTRADOR_USUARIO_ATUALIZAR', 'Atualizar usuários'),
    (gen_random_uuid(), 'ADMINISTRADOR_USUARIO_VISUALIZAR', 'Visualizar usuários'),
    (gen_random_uuid(), 'ADMINISTRADOR_USUARIO_DESATIVAR', 'Desativar usuários'),
    (gen_random_uuid(), 'ADMINISTRADOR_USUARIO_GERENCIAR_PERFIL', 'Gerenciar perfis dos usuários'),
    (gen_random_uuid(), 'AGENDAMENTO_CRIAR', 'Criar Agendamentos'),
    (gen_random_uuid(), 'AGENDAMENTO_VIZUALIZAR', 'Visualizar Agendamentos'),
    (gen_random_uuid(), 'AGENDAMENTO_ATUALIZAR', 'Atualizar Agendamentos');
    ;

--- ASSOCIAR PERFIS AS PERMISSÕES
INSERT INTO hospital.perfil_permissao (perfil_id, permissao_id)
SELECT perfil.id, permissao.id
FROM hospital.perfis perfil
    JOIN hospital.permissoes permissao ON permissao.nome like 'ADMINISTRADOR%'
WHERE perfil.nome = 'ADMINISTRADOR';

INSERT INTO hospital.perfil_permissao (perfil_id, permissao_id)
SELECT perfil.id, permissao.id
FROM hospital.perfis perfil
    JOIN hospital.permissoes permissao ON permissao.nome like 'USUARIO%'
WHERE perfil.nome IN ('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMINISTRADOR');

INSERT INTO hospital.perfil_permissao (perfil_id, permissao_id)
SELECT perfil.id, permissao.id
FROM hospital.perfis perfil
         JOIN hospital.permissoes permissao ON permissao.nome IN ('AGENDAMENTO_CRIAR', 'AGENDAMENTO_VIZUALIZAR')
WHERE perfil.nome IN ('ENFERMEIRO');

INSERT INTO hospital.perfil_permissao (perfil_id, permissao_id)
SELECT perfil.id, permissao.id
FROM hospital.perfis perfil
         JOIN hospital.permissoes permissao ON permissao.nome IN ('AGENDAMENTO_VIZUALIZAR', 'AGENDAMENTO_ATUALIZAR')
WHERE perfil.nome IN ('MEDICO');

INSERT INTO hospital.perfil_permissao (perfil_id, permissao_id)
SELECT perfil.id, permissao.id
FROM hospital.perfis perfil
         JOIN hospital.permissoes permissao ON permissao.nome IN ('AGENDAMENTO_VIZUALIZAR')
WHERE perfil.nome IN ('PACIENTE');

