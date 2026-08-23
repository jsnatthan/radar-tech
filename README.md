# Radar Tech

Plataforma web que aproxima profissionais de tecnologia e empresas. O Radar Tech reúne vagas verificadas, candidatura com currículo, perfil profissional e ferramentas de gestão em uma experiência moderna, responsiva e acessível.

> Este repositório é a evolução pessoal do Radar Tech. O histórico Git foi preservado para manter a atribuição das contribuições anteriores.

## Principais recursos

- catálogo público de vagas com busca e filtros por localização, modalidade e senioridade;
- detalhes da oportunidade, vagas relacionadas e candidatura interna;
- autenticação tradicional com BCrypt e login/cadastro pelo Google OAuth2;
- recuperação de senha por e-mail com token temporário, de uso único e armazenado como hash;
- perfil do candidato com foto, capa, biografia, experiências e currículo em PDF;
- preferência persistente entre tema claro e escuro;
- currículo do perfil anexado à candidatura e disponível somente para administradores;
- painel administrativo com indicadores, gestão de vagas, candidaturas, usuários e auditoria;
- moderação das vagas por estados pendente, publicada e arquivada;
- auditoria persistente de autenticação, perfis, vagas, candidaturas e operações administrativas;
- assistente de carreira opcional, compatível com APIs no formato OpenAI;
- integração opcional com Stripe Checkout e webhooks assinados;
- healthcheck em `GET /ping`, que verifica também a conexão com o banco de dados.

## Tecnologias e arquitetura

- Java 21 e Spring Boot 3;
- Spring MVC, Thymeleaf e CSS/JavaScript modular;
- Spring Security com `SecurityFilterChain`, OAuth2 Client e controle por `ROLE_USER`/`ROLE_ADMIN`;
- Spring Data JPA, PostgreSQL e migrations Flyway;
- JUnit 5, MockMvc, Mockito, H2 e JaCoCo;
- Docker multi-stage e GitHub Actions.

A organização segue as camadas `controller`, `service`, `repository`, `domain`, `dto` e `config`. Mudanças estruturais no banco são feitas exclusivamente pelas migrations em `src/main/resources/db/migration`.

## Qualidade e segurança

O comando `mvn verify` executa os testes e valida a cobertura de linhas pelo JaCoCo. O limite atual é **100%**, portanto o build falha se qualquer linha de produção deixar de ser coberta. Os testes verificam páginas públicas, segurança por papel, serviços, repositórios, integrações, auditoria, perfil, anexos, recuperação de senha e contratos de produção.

As credenciais são recebidas somente por variáveis de ambiente. Arquivos `.env`, tokens, chaves e senhas não são versionados. O workflow de CI repete a validação em cada push e pull request para `main`.

## Executar localmente

### Opção 1 — Maven

Pré-requisitos: Java 21, Maven 3.9+ e PostgreSQL 16.

```bash
createdb radar_tech
mvn spring-boot:run
```

O perfil `dev` usa as configurações de `application-dev.yml`. A aplicação abre em `http://localhost:8080` e o healthcheck em `http://localhost:8080/ping`.

### Opção 2 — Docker Compose

```bash
docker compose up --build
```

O Compose inicia a aplicação e um PostgreSQL local. Os valores padrão existem apenas para desenvolvimento; troque `ADMIN_PASSWORD` ao testar uma conta administrativa.

## Testes e cobertura

```bash
mvn clean verify
```

Relatório HTML:

```text
target/site/jacoco/index.html
```

## Variáveis de ambiente

### Obrigatórias em produção

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<database-user>
SPRING_DATASOURCE_PASSWORD=<database-password>
ADMIN_USERNAME=<admin-user>
ADMIN_PASSWORD=<strong-admin-password>
APP_PUBLIC_URL=https://<your-domain>
```

### Google OAuth2

```dotenv
GOOGLE_CLIENT_ID=<google-client-id>
GOOGLE_CLIENT_SECRET=<google-client-secret>
GOOGLE_REDIRECT_URI=https://<your-domain>/login/oauth2/code/google
```

No Google Cloud Console, cadastre exatamente o mesmo endereço de redirecionamento. Para desenvolvimento, use `http://localhost:8080/login/oauth2/code/google`.

### Recuperação de senha por SMTP

```dotenv
PASSWORD_RESET_EMAIL_ENABLED=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<smtp-user>
MAIL_PASSWORD=<smtp-app-password>
MAIL_FROM=<verified-sender>
MAIL_SMTP_AUTH=true
MAIL_STARTTLS_ENABLE=true
PASSWORD_RESET_TTL_MINUTES=30
```

Use uma senha de aplicativo ou credencial SMTP específica; nunca use nem versione a senha principal da conta.

### Assistente de IA opcional

```dotenv
AI_ENABLED=true
AI_BASE_URL=https://api.openai.com
AI_API_KEY=<ai-api-key>
AI_MODEL=gpt-4o-mini
```

O endpoint deve oferecer a API compatível com `POST /v1/chat/completions`. Sem chave, mantenha `AI_ENABLED=false`.

### Stripe opcional

```dotenv
STRIPE_SECRET_KEY=<stripe-secret-key>
STRIPE_WEBHOOK_SECRET=<stripe-webhook-secret>
STRIPE_MONTHLY_PRICE_ID=<stripe-price-id>
STRIPE_SUCCESS_URL=https://<your-domain>/divulgar/assinar/sucesso
STRIPE_CANCEL_URL=https://<your-domain>/divulgar/assinar/cancelado
```

Webhook público: `POST /webhooks/stripe`.

## Acesso administrativo

Defina `ADMIN_USERNAME` e `ADMIN_PASSWORD` antes da primeira inicialização. O usuário administrativo é criado com senha BCrypt e acessa `/admin`. Usuários comuns continuam sem permissão para rotas administrativas.

Principais telas:

- `/admin` — dashboard;
- `/admin/vagas` — gestão e moderação;
- `/admin/candidaturas` — candidaturas e currículos;
- `/admin/auditoria` — consulta de logs;
- `/admin/usuarios` — usuários cadastrados.

## Publicação gratuita: Koyeb + Neon

Esta configuração separa a aplicação e o banco sem depender de infraestrutura acadêmica.

1. Crie um projeto gratuito no Neon e copie host, banco, usuário e senha.
2. Crie um Web Service gratuito no Koyeb conectado a este repositório GitHub.
3. Selecione construção por Dockerfile e informe `docker/Dockerfile`.
4. Configure o healthcheck HTTP como `/ping` e a porta como `8080`.
5. Cadastre no Koyeb as variáveis obrigatórias listadas acima.
6. Após receber o domínio público, atualize `APP_PUBLIC_URL`, `GOOGLE_REDIRECT_URI` e as URLs autorizadas no Google Cloud.

O Koyeb realiza novos deploys quando a branch `main` é atualizada. Na modalidade gratuita, a aplicação pode reduzir a zero após inatividade; por isso, o primeiro acesso pode ter um pequeno tempo de inicialização.

## Pipeline

O arquivo `.github/workflows/ci.yml` executa `mvn verify` e publica o relatório JaCoCo como artefato. O deploy fica a cargo da integração Git do Koyeb somente depois de a validação do repositório passar.

## Licença e atribuição

Antes de distribuir comercialmente, adicione uma licença compatível com a titularidade do código e com as contribuições existentes. O histórico de commits deve permanecer preservado para reconhecer todos os autores.
