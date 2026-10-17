# Auto Escola 3ESS - API REST de agendamento de instruções

API REST em Java com Spring Boot para agendar as instruções (aulas práticas) de uma auto escola.

Checkpoint 5 de SOA e WebServices - FIAP

Professor: Carlos Eduardo Machado de Oliveira

## Integrantes

| Nome | RM |
|------|----|
| Ricardo Di Tilia | RM555155 |
| Bento Rangel | RM559124 |
| Eric Yuji | RM554869 |
| Kaue Pires | RM554403 |
| Higor Batista | RM558907 |

## O que foi feito

### Checkpoint 5

| Item pedido | Onde está | Como testar |
|-------------|-----------|-------------|
| Consumo de API/WebService externo | API pública ViaCEP, consumida com `RestClient` em `adapter/out/client/viacep/ViaCepClient.java` (porta `ConsultaCepPort`) | `GET /cep/{cep}`, `ViaCepClientTest`, `CepControllerTest`, `ConsultaCepServiceTest` |
| Documentação automática com Swagger | springdoc-openapi em `config/documentation/SwaggerConfig.java` e `@Tag`/`@Operation`/`@ApiResponse` nos controllers | http://localhost:8080/swagger-ui.html, `SwaggerDocumentationTest` |
| Configuração de CORS | `config/cors/CorsConfig.java`, junto com o Spring Security no `SecurityConfig` | `CorsConfigTest` |
| Testes automatizados para cada entidade | `src/test/java`, com 121 testes de Instrutor, Aluno, Usuário e Instrução | `./mvnw test` |

### Checkpoints anteriores

| Item | Endpoints |
|------|-----------|
| CRUD de Instrutores | `POST/GET/PUT /instrutores`, `GET/DELETE /instrutores/{id}` |
| CRUD de Alunos | `POST/GET/PUT /alunos`, `GET/DELETE /alunos/{id}` |
| CRUD de Usuários | `POST/GET/PUT /usuarios`, `GET/DELETE /usuarios/{id}` |
| Instruções (agendamento e cancelamento) | `POST/GET/DELETE /instrucoes`, `GET /instrucoes/{id}` |
| Login com JWT e perfis ADMIN/USER | `POST /login` |

## Tecnologias

- Java 17+ e Spring Boot 4 (Web MVC, Data JPA, Validation, Security)
- MySQL 8 e Flyway para as migrations
- JWT (`com.auth0:java-jwt`) e BCrypt para as senhas
- springdoc-openapi (Swagger UI)
- `RestClient` do Spring para consumir o ViaCEP
- JUnit 5, Mockito, MockMvc, AssertJ, `MockRestServiceServer` e H2 nos testes

## Arquitetura

O projeto segue a arquitetura hexagonal (ports and adapters) que começamos em aula:

```
br.com.fiap3ess.autoescola3ess
  adapter/in/controller           - controllers, DTOs de request/response e mappers
  adapter/out/client/viacep       - cliente da API do ViaCEP
  adapter/out/repository          - entidades JPA, mappers e repositories do Spring Data
  adapter/out/security            - UserDetailsService e usuário autenticado
  application/core/domain         - classes de domínio (Aluno, Instrutor, Instrucao, Usuario)
  application/core/specification  - validadores de agendamento e cancelamento
  application/port/in e port/out  - portas de entrada e saída (interfaces)
  application/service             - casos de uso (AgendaDeInstrucoes, AlunoService, ...)
  config                          - CORS, Swagger, segurança (JWT) e RestClient
  exception                       - exceções e tratamento de erros
  shared                          - Endereco e enums
```

As classes de domínio não têm anotações do JPA. Cada uma tem uma entidade correspondente em `adapter/out/repository/entity` e um mapper que converte de uma para a outra.

## Como executar

Precisa do JDK 17 ou superior e de um MySQL 8 em `localhost:3306` (usuário `root`, senha `fiap`). Se não tiver o MySQL instalado, dá pra subir um com Docker usando `docker compose up -d`.

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

O banco `autoescola3ess` é criado automaticamente e o Flyway roda todas as migrations. A migration `V11` cria o usuário inicial com login `admin` e senha `admin` (perfil ADMIN).

Para usar outro banco é só definir as variáveis de ambiente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`.

### Testando pelo Swagger

1. Acesse http://localhost:8080/swagger-ui.html
2. Na seção Autenticação, faça o `POST /login` enviando `{"login": "admin", "senha": "admin"}`
3. Copie o `tokenJWT` da resposta, clique em Authorize e cole o token
4. Depois disso dá pra testar todos os endpoints pelo próprio Swagger

A documentação também fica disponível em `/v3/api-docs` (JSON) e `/v3/api-docs.yaml` (YAML). O arquivo [`SwaggerOffLine.yaml`](SwaggerOffLine.yaml) tem uma cópia offline.

## Regras de negócio

Agendamento (`POST /instrucoes`), cada regra é uma classe que implementa `ValidadorAgendamento`:

- A auto escola funciona de segunda a sábado, das 06:00 às 21:00. Como cada instrução dura 1 hora, o último horário é 20:00 (`ValidadorHorarioFuncionamento`)
- Só pode agendar em hora inteira, como 09:00 ou 10:00 (`ValidadorHoraInteira`)
- Antecedência mínima de 30 minutos (`ValidadorHorarioAntecedencia`)
- Aluno e instrutor precisam estar ativos (`ValidadorAlunoAtivo`, `ValidadorInstrutorAtivo`)
- O instrutor não pode ter outra instrução no mesmo horário (`ValidadorConflitoHorarioInstrutor`)
- No máximo 1 instrução por dia para cada aluno (`ValidadorLimiteDiarioAluno`)
- O instrutor é opcional. Se não for informado, a especialidade é obrigatória e a API escolhe um instrutor livre dessa especialidade

Cancelamento (`DELETE /instrucoes`), cada regra é uma classe que implementa `ValidadorCancelamento`:

- Antecedência mínima de 24 horas (`ValidadorCancelamentoAntecedencia`)
- É obrigatório informar o motivo: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`
- Não dá pra cancelar uma instrução que já foi cancelada (`ValidadorCancelamentoInstrucaoAtiva`)
- A instrução não é apagada do banco, ela fica com o motivo do cancelamento e o horário do instrutor volta a ficar livre

Exemplos:

```json
// POST /instrucoes
{ "id_aluno": 1, "especialidade": "CARROS", "data_hora": "26/10/2026 - 10:00" }

// DELETE /instrucoes
{ "id_instrucao": 1, "motivo": "ALUNO_DESISTIU" }
```

Alunos, instrutores e usuários excluídos ficam inativos (exclusão lógica). Usuário inativo não consegue fazer login.

O perfil ADMIN gerencia todos os cadastros, inclusive os usuários. O perfil USER pode listar alunos e instrutores, consultar alunos e agendar, consultar ou cancelar instruções.

## Consumo de API externa (ViaCEP)

O endpoint `GET /cep/{cep}` consulta o [ViaCEP](https://viacep.com.br) e devolve o endereço no mesmo formato do campo `endereco` dos cadastros de aluno e instrutor. Com isso o front-end consegue preencher o formulário a partir do CEP.

O fluxo é: `CepController`, `ConsultaCepService`, porta `ConsultaCepPort` e por fim `ViaCepClient`, que chama `https://viacep.com.br/ws/{cep}/json/`.

```json
// GET /cep/01310-100
{
  "logradouro": "Avenida Paulista",
  "numero": null,
  "complemento": "de 612 a 1510 - lado par",
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "cep": "01310-100"
}
```

| Situação | Resposta |
|----------|----------|
| CEP encontrado | `200` com o endereço |
| CEP com formato inválido (validado antes de chamar o ViaCEP) | `400` |
| CEP que não existe (o ViaCEP responde `{"erro": "true"}`) | `404` |
| ViaCEP fora do ar ou sem resposta em 5 segundos | `503` |

A URL e o timeout ficam no `application.properties` (`api.externa.viacep.url` e `api.externa.timeout-segundos`).

## CORS

A configuração está em `config/cors/CorsConfig.java`, como um `CorsConfigurationSource` usado pelo Spring Security. Desse jeito a requisição de preflight (`OPTIONS`) é respondida antes da autenticação.

- Origens permitidas: propriedade `api.cors.allowed-origins` (ou variável de ambiente `CORS_ALLOWED_ORIGINS`). O padrão libera `localhost:3000` (React), `localhost:4200` (Angular), `localhost:5173` (Vite), `localhost:5500` (Live Server) e `https://*.autoescola3ess.com.br`
- Métodos: `GET, POST, PUT, DELETE, OPTIONS, HEAD`
- Headers: `Authorization, Content-Type, Accept, Origin, X-Requested-With`
- O header `Location` é exposto para o front-end conseguir ler a URL do recurso criado
- `allowCredentials` ligado e cache do preflight de 1 hora (`maxAge = 3600`)

Requisições de origens que não estão na lista recebem `403`.

## Testes automatizados

```bash
./mvnw test        # Windows: mvnw.cmd test
```

Os testes usam um banco H2 em memória no modo MySQL (perfil `test`) com as mesmas migrations do Flyway, então rodam sem precisar do MySQL.

| Entidade / recurso | Classes de teste |
|--------------------|------------------|
| Instrutor | `InstrutorControllerTest`, `InstrutorServiceTest`, `InstrutorRepositoryTest` |
| Aluno | `AlunoControllerTest`, `AlunoServiceTest`, `AlunoRepositoryTest` |
| Usuário | `UsuarioControllerTest`, `UsuarioServiceTest`, `UsuarioRepositoryTest`, `LoginControllerTest` |
| Instrução | `InstrucaoControllerTest`, `AgendaDeInstrucoesTest`, `InstrucaoRepositoryTest` e os testes dos validadores |
| Fluxo completo | `FluxoInstrucaoIntegrationTest` (login, cadastros, agendamento, regras e cancelamento, sem mock) |
| API externa | `ViaCepClientTest`, `CepControllerTest`, `ConsultaCepServiceTest` |
| CORS e Swagger | `CorsConfigTest`, `SwaggerDocumentationTest` |

São 121 testes no total.
