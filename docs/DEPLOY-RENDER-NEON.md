# Publicar o Radar Tech no Render + Neon

Este deploy usa a cópia do projeto com as melhorias WCAG. O repositório original não é modificado automaticamente.

## Neon

Criar um projeto gratuito com PostgreSQL 16, preferencialmente em uma região próxima de Virginia (região do serviço Render). Usar um banco novo para a demonstração. As migrations do projeto criam as tabelas e o catálogo inicial.

Na conexão, selecionar Java/JDBC ou montar os parâmetros abaixo. Não colocar senhas no GitHub, neste documento ou no render.yaml.

- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://HOST_NEON/NOME_BANCO?sslmode=require`
- `SPRING_DATASOURCE_USERNAME`: usuário do banco Neon.
- `SPRING_DATASOURCE_PASSWORD`: senha do banco Neon.

O perfil limita o pool a três conexões. Usar um endpoint direto do Neon para o primeiro deploy e para Flyway.

## Render

Usar um repositório com esta versão do código e criar um serviço Docker gratuito. O arquivo `render.yaml` descreve os parâmetros; também é possível preencher manualmente:

- Dockerfile: `docker/Dockerfile.render`.
- Contexto Docker: raiz do repositório.
- Plano: Free.
- Health check: `/ping`.
- Perfis: `prod,render`.
- Configurar as três variáveis de banco acima e `ADMIN_PASSWORD`.

O Blueprint gera uma senha administrativa aleatória. Se criar o serviço manualmente, definir uma senha forte no painel. Usuário: `admin`.

O perfil aceita a variável `PORT` do Render, escuta em `0.0.0.0`, trata cabeçalhos do proxy e usa cookie de sessão seguro em HTTPS. O Dockerfile específico não carrega o agente de telemetria da implantação original e limita a memória da JVM para o plano gratuito.

## Validar a publicação

1. Aguardar build e deploy concluírem no Render.
2. Abrir o endereço HTTPS fornecido pelo serviço.
3. Conferir `/ping`: deve retornar `status: ok` e `database: up`.
4. Testar home, busca, detalhes, cadastro, login, perfil e administração.
5. Confirmar que as tabelas e os dados permanecem no Neon após reiniciar a aplicação.

Somente o banco novo da demonstração é inicializado; não é necessário copiar o banco pessoal local. Fotos e currículos são armazenados em campos do banco pelo código desta versão.

Google OAuth, Stripe, SMTP e IA precisam das respectivas credenciais para uso real. A IA e o envio de e-mail ficam desativados por padrão nesta configuração. Configurar callback OAuth e URLs externas somente se essas integrações forem habilitadas. O frontend continua exibindo os recursos previstos pelo produto.

O Render Free pode suspender após inatividade e demorar para responder no primeiro acesso. O Neon Free tem limites de uso. Esta configuração não contrata recursos pagos.
