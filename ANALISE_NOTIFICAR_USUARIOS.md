# ANÁLISE ARQUITETURAL: NOTIFICAR-USUARIOS (Microserviço)

## 1. PADRÃO ARQUITETURAL

**Padrão Principal: EVENT-DRIVEN MICROSERVICE**
- ✅ Arquitetura orientada a eventos (Event-Driven Architecture)
- ✅ Baseada em Apache Kafka para mensageria assíncrona
- ✅ Implementa o padrão Observer/Publish-Subscribe
- ✅ Desacoplamento total entre produtor e consumidor de eventos
- ✅ Escalabilidade horizontal através de consumer groups

**Características:**
- Consumer groups garantem processamento paralelo
- Tolerância a falhas com retry automático
- Apenas o serviço "notificar-usuarios" consome eventos
- Sem persistência de dados (stateless)
- Sem banco de dados acoplado

## 2. DIVISÃO DE CAMADAS

### 2.1. Estrutura de Pacotes

notificar-usuarios/
├── config/                 # Configuração do Kafka
├── enums/                  # Enumerações de tipos
├── kafka/
│   ├── consumers/          # Listeners Kafka (Observer)
│   └── events/             # Data Transfer Objects de eventos
├── services/               # Lógica de negócio
│   ├── impl/               # Implementações de serviços
│   └── (interfaces)        # Contratos de serviço
└── NotificarUsuariosApp    # Entrada da aplicação

### 2.2. Camadas Principais

#### Camada 1: Configuração (config/)
- Classe: KafkaConsumerConfig
- Responsabilidade: Configurar deserialização de eventos (Jackson)
- Padrão: Configuration Pattern com @Configuration + @EnableKafka
- Beans criados: 4 ConcurrentKafkaListenerContainerFactory

#### Camada 2: Eventos (kafka/events/)
- Classes: UsuarioEvent, AgendamentoEvent
- Padrão: Data Transfer Object (DTO)
- Responsabilidade: Representar dados transferidos entre sistemas
- Desserialização automática via Jackson

#### Camada 3: Consumidores Kafka (kafka/consumers/)
- Classes: KafkaUsuarioConsumer, KafkaAgendamentoConsumer
- Padrão: Observer (listening para eventos)
- @KafkaListener mapeia tópicos e consumer groups
- Métodos de escuta para diferentes tipos de eventos

#### Camada 4: Enumerações (enums/)
- Classe: TipoNotificacaoEnum
- Valores: USUARIO_CADASTRAR, USUARIO_ATUALIZAR, AGENDAMENTO_CADASTRAR, AGENDAMENTO_ATUALIZAR
- Padrão: Type-Safe Enum

#### Camada 5: Serviços (services/)
- Interfaces: INotificarUsuario, INotificarAgendamento
- Implementações: NotificarUsuarioServiceImpl, NotificarAgendamentoServiceImpl
- Padrão: Strategy (diferentes tipos de notificação)
- Responsabilidade: Construir e enviar mensagens de notificação

## 3. FLUXO DE DADOS

### 3.1. Fluxo Completo de Uma Notificação

1. GERENCIADOR-USUARIOS publica UsuarioEvent no Kafka
   └─ Tópico: usuario-criado (JSON serializado)

2. Kafka armazena mensagem em broker

3. KafkaUsuarioConsumer escuta o tópico
   └─ @KafkaListener inicia polling
   └─ Consumer group: usuario-consumer

4. Mensagem desserializada por Jackson
   └─ JSON → UsuarioEvent object

5. NotificarUsuarioServiceImpl.notificarUsuarioCriado() chamado
   └─ Construir mensagem formatada
   └─ Adicionar dados do usuário

6. Mensagem enviada (atualmente System.out.println)
   └─ Futuramente: Email/SMS/Push

7. Consumer commit offset automaticamente
   └─ Kafka marca mensagem como processada

### 3.2. Eventos Processados

**Usuários:**
- usuario-criado: Quando novo usuário é cadastrado
- usuario-atualizado: Quando usuário é atualizado

**Agendamentos:**
- agendamento-criado: Quando novo agendamento é criado
- agendamento-atualizado: Quando agendamento é atualizado

## 4. TECNOLOGIAS E FRAMEWORKS

### 4.1. Stack Tecnológico

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 21 LTS | Linguagem principal |
| Spring Boot | 4.1.0 | Framework principal |
| Spring Kafka | Starter | Integração Kafka |
| Apache Kafka | (servidor) | Message broker |
| Jackson | Spring default | Serialização JSON |
| Lombok | Spring default | Redução de boilerplate |

### 4.2. Configuração (application.yaml)

server:
  port: 8092
  servlet.context-path: /notificar-usuarios

spring:
  application:
    name: notificar-usuarios
  kafka:
    bootstrap-servers: localhost:9092

app:
  kafka:
    consumers:
      usuario:
        group-id: usuario-consumer
        criado.topic: usuario-criado
        atualizado.topic: usuario-atualizado
      agendamento:
        group-id: agendamento-consumer
        criado.topic: agendamento-criado
        atualizado.topic: agendamento-atualizado

### 4.3. Dependências Maven

- spring-boot (core)
- spring-boot-starter-web
- spring-boot-starter-kafka
- lombok

## 5. BOAS PRÁTICAS E PADRÕES DE PROJETO

### 5.1. Padrões Implementados

1. **Observer Pattern (Publish-Subscribe)**
   - @KafkaListener para escuta de eventos
   - Desacoplamento entre sistemas
   - Evento: Usuário/Agendamento criado/atualizado
   - Observador: KafkaConsumer

2. **Strategy Pattern**
   - Interface: INotificarUsuario, INotificarAgendamento
   - Diferentes estratégias de notificação
   - Fácil adicionar novos tipos

3. **Data Transfer Object (DTO)**
   - UsuarioEvent, AgendamentoEvent
   - Tipagem forte, validação em compile-time
   - Deserialização automática via Jackson

4. **Configuration Pattern**
   - @Configuration + @EnableKafka
   - Factory beans genéricos
   - Centralizar configuração

5. **Injeção de Dependência**
   - @Autowired para ObjectMapper e Services
   - Loosely coupled, testável
   - Configurável e reutilizável

6. **Type-Safe Enum**
   - TipoNotificacaoEnum com valores constantes
   - Prevenção de valores inválidos
   - Refatoração segura

### 5.2. Princípios SOLID

- **S - Single Responsibility**
  - KafkaUsuarioConsumer: apenas escuta usuários
  - KafkaAgendamentoConsumer: apenas escuta agendamentos
  - NotificarUsuarioServiceImpl: apenas formata mensagens de usuário

- **O - Open/Closed**
  - Fácil adicionar novo tipo de notificação sem modificar código

- **L - Liskov Substitution**
  - Implementações intercambiáveis das interfaces

- **I - Interface Segregation**
  - Interfaces pequenas e específicas
  - INotificarUsuario: apenas métodos de usuário
  - INotificarAgendamento: apenas métodos de agendamento

- **D - Dependency Inversion**
  - Consumers dependem de abstrações (interfaces)

### 5.3. Boas Práticas Aplicadas

✅ **Configuração Externalizada**
   - Fácil mudança sem recompilação
   - Diferentes configs por ambiente

✅ **Lombok para Boilerplate**
   - Reduz código repetitivo
   - Menos bugs

✅ **Jackson para Deserialização Segura**
   - Trusted packages configurado
   - Prevenção contra gadget chain attacks

✅ **Stateless Design**
   - Sem estado compartilhado
   - Escalável horizontalmente

✅ **Mensagens Formatadas**
   - Métodos private para construção
   - Template de notificação limpo

## 6. PONTOS FORTES

✅ **Desacoplamento Total**
   - Sistema não precisa saber que está sendo notificado
   - Fácil adicionar novos consumidores

✅ **Escalabilidade**
   - Novos consumidores sem impacto
   - Processamento paralelo via consumer groups

✅ **Confiabilidade**
   - Mensagens persistidas no Kafka
   - Retry automático em caso de falha

✅ **Simplicidade**
   - Código limpo e fácil de entender
   - Poucas dependências

✅ **Configuração Externalizada**
   - Sem necessidade de recompilação

## 7. ÁREAS DE MELHORIA

📌 **Recomendações**

1. Substituir System.out.println por email real
   - Integrar com SendGrid, AWS SES
   - Template profissional

2. Adicionar Error Handling
   - Dead letter queue para falhas
   - Retry com backoff exponencial
   - Logging estruturado

3. Implementar Persistência (Opcional)
   - Redis para cache
   - Database para auditoria

4. Circuit Breaker
   - Resilience4j integration

5. Métricas e Monitoring
   - Prometheus + Grafana
   - Alertas

6. Testes Unitários
   - Coverage > 80%
   - Mockear Kafka consumers

7. Versionamento de Eventos
   - Schema versioning

8. Usar Protobuf/Avro
   - Schema registry
   - Serialização eficiente

## 8. CONCLUSÃO

**Notificar-Usuarios** é um microserviço bem estruturado implementando:

- ✅ Event-Driven Architecture com Kafka
- ✅ Desacoplado e escalável
- ✅ Padrões SOLID e Design Patterns
- ✅ Configuração externalizada
- ✅ Boas práticas de Clean Code

**Próximos passos:**
1. Implementar notificações reais (email)
2. Adicionar error handling
3. Implementar métricas
4. Adicionar testes unitários
5. Considerar Avro/Protobuf

Análise Preparada por: Arquiteto de Software Sênior
Data: 15/09/2026
Serviço: notificar-usuarios
Versão: 1.0
Status: ✅ Completo
