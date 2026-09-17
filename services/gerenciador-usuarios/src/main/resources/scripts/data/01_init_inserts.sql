-- TODO: RETIRAR COMENTÁRIO QUANDO FOR EXECUTAR PELO POSTGRESQL
-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- INSERT INTO hospital.perfis (id, nome, descricao) VALUES
--     (gen_random_uuid(), 'ADMINISTRADOR', 'Administrador'),
--     (gen_random_uuid(), 'PACIENTE', 'Paciente'),
--     (gen_random_uuid(), 'MEDICO', 'Médico'),
--     (gen_random_uuid(), 'ENFERMEIRO', 'Enfermeiro');

INSERT INTO hospital.usuarios (id, login, nome, email, senha, ativo)
VALUES
    (RANDOM_UUID(), 'admin', 'Administrador', 'admin@example.com', '$2a$12$ERt5PPqRsP45iQB8.dbefeyIltATCKxrrplinZHUzw2FwDiPliYBm', true),
    (RANDOM_UUID(), 'paciente', 'Paciente', 'paciente@example.com', '$2a$12$n6ltKZwv4aE.Z3OGm9c.UuoUgsnkPJ15BunCmo382PM0GqDJSCMQC', true),
    (RANDOM_UUID(), 'medico', 'Médico', 'medico@example.com', '$2a$12$xn1S8KkDTzZHmFUNF8gWAe39taS94e6TQC6XGnBF/Y6g16qmjhoU6', true),
    (RANDOM_UUID(), 'enfermeiro', 'Enfermeiro', 'enfermeiro@example.com', '$2a$12$/bcOb1GmOuPXD81zof.8IenjcedJwYuKorw3D1RbTIf77nA42NZj2', true);

INSERT INTO hospital.perfis (id, nome, descricao)
VALUES
    (RANDOM_UUID(), 'ADMINISTRADOR', 'Administrador'),
    (RANDOM_UUID(), 'PACIENTE', 'Paciente'),
    (RANDOM_UUID(), 'MEDICO', 'Médico'),
    (RANDOM_UUID(), 'ENFERMEIRO', 'Enfermeiro');

INSERT INTO hospital.permissoes (id, nome, descricao)
VALUES
    (RANDOM_UUID(), 'USUARIO_ATUALIZAR', 'Atualizar usuário'),
    (RANDOM_UUID(), 'USUARIO_ATUALIZAR_SENHA', 'Atualizar senha'),
    (RANDOM_UUID(), 'USUARIO_VISUALIZAR', 'Visualizar usuário'),
    (RANDOM_UUID(), 'ADMINISTRADOR_USUARIO_CRIAR', 'Criar usuários'),
    (RANDOM_UUID(), 'ADMINISTRADOR_USUARIO_ATUALIZAR', 'Atualizar usuários'),
    (RANDOM_UUID(), 'ADMINISTRADOR_USUARIO_VISUALIZAR', 'Visualizar usuários'),
    (RANDOM_UUID(), 'ADMINISTRADOR_USUARIO_DESATIVAR', 'Desativar usuários'),
    (RANDOM_UUID(), 'ADMINISTRADOR_USUARIO_GERENCIAR_PERFIL', 'Gerenciar perfis dos usuários'),
    (RANDOM_UUID(), 'AGENDAMENTO_CRIAR', 'Criar Agendamentos'),
    (RANDOM_UUID(), 'AGENDAMENTO_VIZUALIZAR', 'Visualizar Agendamentos'),
    (RANDOM_UUID(), 'AGENDAMENTO_ATUALIZAR', 'Atualizar Agendamentos');


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