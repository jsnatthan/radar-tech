# Publicar o Radar Tech no Render + Neon

Este deploy usa a c�pia do projeto com as melhorias WCAG. O reposit�rio original n�o � modificado automaticamente.

## Neon

Criar um projeto gratuito com PostgreSQL 16, preferencialmente em uma regi�o pr�xima de Virginia (regi�o do servi�o Render). Usar um banco novo para a demonstra��o. As migrations do projeto criam as tabelas e o cat�logo inicial.

Na conex�o, selecionar Java/JDBC ou montar os par�metros abaixo. N�o colocar senhas no GitHub, neste documento ou no render.yaml.

- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://HOST_NEON/NOME_BANCO?sslmode=require`
- `SPRING_DATASOURCE_USERNAME`: usu�rio do banco Neon.
- `SPRING_DATASOURCE_PASSWORD`: senha do banco Neon.

O perfil limita o pool a tr�s conex�es. Usar um endpoint direto do Neon para o primeiro deploy e para Flyway.

## Render

Usar um reposit�rio com esta vers�o do c�digo e criar um servi�o Docker gratuito. O arquivo `render.yaml` descreve os par�metros; tamb�m � poss�vel preencher manualmente:

- Dockerfile: `docker/Dockerfile.render`.
- Contexto Docker: raiz do reposit�rio.
- Plano: Free.
- Health check: `/ping`.
- Perfis: `prod,render`.
- Configurar as tr�s vari�veis de banco acima e `ADMIN_PASSWORD`.

O Blueprint gera uma senha administrativa aleat�ria. Se criar o servi�o manualmente, definir uma senha forte no painel. Usu�rio: `admin`.

O perfil aceita a vari�vel `PORT` do Render, escuta em `0.0.0.0`, trata cabe�alhos do proxy e usa cookie de sess�o seguro em HTTPS. O Dockerfile espec�fico n�o carrega o agente de telemetria da implanta��o original e limita a mem�ria da JVM para o plano gratuito.

## Validar a publica��o

1. Aguardar build e deploy conclu�rem no Render.
2. Abrir o endere�o HTTPS fornecido pelo servi�o.
3. Conferir `/ping`: deve retornar `status: ok` e `database: up`.
4. Testar home, busca, detalhes, cadastro, login, perfil e administra��o.
5. Confirmar que as tabelas e os dados permanecem no Neon ap�s reiniciar a aplica��o.

Somente o banco novo da demonstra��o � inicializado; n�o � necess�rio copiar o banco pessoal local. Fotos e curr�culos s�o armazenados em campos do banco pelo c�digo desta vers�o.

Google OAuth, Stripe, SMTP e IA precisam das respectivas credenciais para uso real. A IA e o envio de e-mail ficam desativados por padr�o nesta configura��o. Configurar callback OAuth e URLs externas somente se essas integra��es forem habilitadas. O frontend continua exibindo os recursos previstos pelo produto.

O Render Free pode suspender ap�s inatividade e demorar para responder no primeiro acesso. O Neon Free tem limites de uso. Esta configura��o n�o contrata recursos pagos.
