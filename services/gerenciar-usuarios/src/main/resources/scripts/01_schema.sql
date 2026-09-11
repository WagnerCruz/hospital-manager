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
