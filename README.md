---

# DSCommerce API

## Descrição

O **DSCommerce** é uma API RESTful desenvolvida para gerenciar um sistema de comércio eletrônico. O objetivo do projeto é fornecer uma infraestrutura de backend robusta que suporte catálogo de produtos, categorias, carrinho de compras (pedidos) e gerenciamento de usuários. O problema que ele resolve é a centralização da lógica de negócios de um e-commerce moderno, garantindo segurança na autenticação e controle de acesso baseado em perfis (Role-Based Access Control) para clientes e administradores.

## Funcionalidades

* **Catálogo de Produtos:** Listagem paginada de produtos com suporte a busca dinâmica por nome (`name`), busca por ID, cadastro, atualização e exclusão (operações restritas a administradores).
* **Categorias:** Listagem de todas as categorias disponíveis no sistema.
* **Pedidos (Orders):** Registro de novos pedidos com múltiplos itens vinculados ao cliente autenticado e consulta de pedidos específicos com validação de propriedade (o cliente só acessa o seu; o admin acessa todos).
* **Usuários:** Consulta do perfil do usuário atualmente autenticado (`/users/me`).
* **Segurança (OAuth2 & JWT):** Autenticação de usuários gerando tokens JWT através de um *Authorization Server* customizado (implementação do *Password Grant Type*).
* **Tratamento Global de Exceções:** Retornos padronizados para erros de validação, recursos não encontrados, falhas de integridade no banco de dados e acesso negado (403 Forbidden / 404 Not Found / 422 Unprocessable Entity).

## Arquitetura

O projeto segue o padrão de arquitetura em camadas (N-Tier Architecture), que favorece a separação de responsabilidades e facilita a manutenção:

* **Controllers (`@RestController`):** Responsáveis por receber as requisições HTTP, delegar a lógica para os serviços e retornar as respostas formatadas em JSON.
* **Services (`@Service`):** Contêm a lógica de negócios da aplicação, orquestrando chamadas aos repositórios e lidando com regras de segurança (ex: `AuthService.validateSelfOrAdmin`).
* **Repositories (`@Repository`):** Interfaces que estendem o `JpaRepository` do Spring Data, responsáveis pela persistência e comunicação direta com o banco de dados usando JPQL e Native Queries.
* **Entities (`@Entity`):** Classes de domínio que mapeiam as tabelas do banco de dados relacional.
* **DTOs (Data Transfer Objects):** Objetos utilizados para transferir dados entre a camada de controle e serviço, ocultando dados sensíveis das entidades e validando dados de entrada com *Bean Validation*.
* **Exception Handlers (`@ControllerAdvice`):** Capturam exceções lançadas nos Services/Controllers e as convertem em respostas HTTP legíveis e padronizadas (`CustomError`, `ValidationError`).
* **Configurações (`@Configuration`):** Configurações centralizadas de segurança (`ResourceServerConfig`, `AuthorizationServerConfig`) e CORS.

## Tecnologias

**Backend:**

* Java 21
* Spring Boot 3.4.4
* Spring Web (REST APIs)
* Spring Data JPA / Hibernate
* Spring Security
* Spring Security OAuth2 (Authorization Server & Resource Server)
* Bean Validation (Hibernate Validator)
* PostgreSQL (Banco de dados principal de desenvolvimento)
* Docker / Docker Compose
* Maven

**Testes:**

* Spring Boot Starter Test (JUnit 5, Mockito)
* Spring Security Test
* H2 Database (Banco de dados em memória para ambiente de testes)

## Estrutura do projeto

Abaixo está a representação da arquitetura de diretórios do sistema:

```text
dscommerce
├── docker-compose.yml       # Configuração do container PostgreSQL
├── .env.example             # Modelo de variáveis de ambiente (não versionar o .env real)
├── pom.xml                  # Arquivo de configuração do Maven
└── src/
    └── main/
        ├── java/com/josev001/dscommerce
        │   ├── config/
        │   │   ├── customgrant/     # Implementação customizada do Password Grant para OAuth2
        │   │   ├── AuthorizationServerConfig.java
        │   │   └── ResourceServerConfig.java
        │   ├── controllers/
        │   │   ├── handlers/        # Interceptadores globais de erro (ControllerAdvice)
        │   │   ├── CategoryController.java
        │   │   ├── OrderController.java
        │   │   ├── ProductController.java
        │   │   └── UserController.java
        │   ├── dto/                 # Classes de Transferência de Dados e Validações
        │   ├── entities/            # Classes de domínio e mapeamento ORM (JPA)
        │   ├── projections/         # Interfaces de projeção de dados (SQL Nativo)
        │   ├── repositories/        # Interfaces de persistência com Spring Data JPA
        │   └── services/
        │       ├── exceptions/      # Exceções de negócio customizadas
        │       ├── AuthService.java
        │       ├── CategoryService.java
        │       ├── OrderService.java
        │       ├── ProductService.java
        │       └── UserService.java
        │   └── DscommerceApplication.java # Classe principal de inicialização
        └── resources/
            ├── application.properties
            ├── application-test.properties
            └── import.sql           # Script de carga inicial de dados (Seeding)

```

## Modelo de dados

O domínio da aplicação consiste nas seguintes entidades principais e seus relacionamentos:

* **User:** Representa os usuários do sistema. Tem uma relação `1:N` com `Order` e `N:N` com `Role`.
* **Role:** Representa os perfis de acesso (`ROLE_CLIENT`, `ROLE_ADMIN`).
* **Product:** Representa o catálogo de itens. Tem uma relação `N:N` com `Category`.
* **Category:** Representa a classificação dos produtos.
* **Order:** Representa um pedido. Relaciona-se `N:1` com `User`, `1:1` com `Payment` e `1:N` com `OrderItem`.
* **OrderItem:** Entidade associativa (usando chave composta `OrderItemPk`) entre `Order` e `Product`, guardando quantidade e preço no momento da compra.
* **Payment:** Representa o registro de pagamento associado a um pedido.

## API

### Endpoints de Autenticação (OAuth2)

* **POST** `/oauth2/token`
* **Descrição:** Autentica o usuário e retorna o token JWT de acesso.
* **Parâmetros (Form URL-Encoded):** `grant_type=password`, `username={email}`, `password={senha}`.
* **Autenticação Basic:** Requer as credenciais do Client da aplicação (`security.client-id` e `security.client-secret`).

### Categorias

* **GET** `/categories`
* **Descrição:** Retorna a lista de todas as categorias.
* **Autenticação:** Pública.

### Produtos

* **GET** `/products`
* **Descrição:** Retorna a lista de produtos de forma paginada.
* **Parâmetros (Query):** `name` (filtro opcional), `page`, `size`, `sort`.
* **Autenticação:** Pública.
* **GET** `/products/{id}`
* **Descrição:** Busca um produto pelo seu ID.
* **Autenticação:** Pública.
* **POST** `/products`
* **Descrição:** Cria um novo produto.
* **Autenticação:** Restrito a Administradores (`ROLE_ADMIN`).
* **Corpo:** JSON com `name`, `description`, `price`, `imgUrl`, `categories`.
* **PUT** `/products/{id}`
* **Descrição:** Atualiza os dados de um produto existente.
* **Autenticação:** Restrito a Administradores (`ROLE_ADMIN`).
* **DELETE** `/products/{id}`
* **Descrição:** Remove um produto (se não houver conflito de integridade referencial).
* **Autenticação:** Restrito a Administradores (`ROLE_ADMIN`).

### Pedidos (Orders)

* **GET** `/orders/{id}`
* **Descrição:** Retorna os detalhes de um pedido.
* **Autenticação:** Restrito (`ROLE_ADMIN` ou o `ROLE_CLIENT` que efetuou a compra).
* **POST** `/orders`
* **Descrição:** Insere um novo pedido no sistema. O status inicial é automaticamente definido como `WAITING_PAYMENT`.
* **Autenticação:** Restrito a Clientes (`ROLE_CLIENT`).
* **Corpo:** JSON contendo os itens do pedido (`productId`, `quantity`).

### Usuários

* **GET** `/users/me`
* **Descrição:** Recupera os dados do usuário autenticado no momento.
* **Autenticação:** Autenticado (`ROLE_ADMIN` ou `ROLE_CLIENT`).

## Banco de Dados

A aplicação utiliza PostgreSQL como banco de dados principal em ambiente de desenvolvimento.

O banco é executado através de Docker Compose, facilitando a configuração do ambiente e garantindo que todos os desenvolvedores utilizem a mesma estrutura de banco. O arquivo `docker-compose.yml` é responsável por criar e configurar o container PostgreSQL utilizado pela aplicação, incluindo porta, banco de dados e persistência dos dados.

O PostgreSQL é executado por padrão na porta `5432` através do container Docker.

O Hibernate/JPA realiza o mapeamento objeto-relacional das entidades e gerencia a criação/atualização do schema conforme as configurações definidas no ambiente.

O arquivo `import.sql` localizado em `src/main/resources` realiza a carga inicial de dados (*seeding*), criando categorias, produtos, usuários, pedidos e pagamentos de exemplo.

Para o ambiente de testes (perfil `test` em `application-test.properties`), o projeto utiliza o banco de dados em memória **H2**.

## Configuração de Ambiente

O projeto possui configurações específicas para os ambientes dev e test. O perfil `dev` utiliza PostgreSQL via Docker Compose, enquanto o perfil `test` utiliza H2 para testes automatizados.

As credenciais do banco de dados são carregadas através de variáveis de ambiente:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

```

Essa abordagem evita o armazenamento de informações sensíveis no código fonte e facilita a configuração em diferentes ambientes.

> **Observação:** As variáveis de ambiente podem ser configuradas diretamente no sistema operacional ou através de um arquivo `.env` utilizando ferramentas compatíveis com o seu ambiente de desenvolvimento.

## Como executar

1. **Clone o repositório:**

```bash
git clone https://github.com/JoseV-001/dscommerce.git
cd dscommerce

```

2. **Instale as dependências:**
Assegure-se de ter o Java 21 e o Maven instalados na sua máquina.

```bash
mvn clean install

```

3. **Configurar variáveis de ambiente:**
Antes de executar a aplicação, configure as variáveis utilizadas para conexão com o PostgreSQL de acordo com seu sistema operacional (usando comandos como `setx` no Windows ou `export` no Linux/Mac) ou crie um arquivo `.env` na raiz do projeto baseado no `.env.example`:

```env
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

```

Essas variáveis são utilizadas pela aplicação para autenticação no banco de dados sem expor credenciais no código.

4. **Subir PostgreSQL com Docker Compose:**

```bash
docker compose up -d

```

O PostgreSQL será iniciado em container e estará disponível para conexão da aplicação.

5. **Executar aplicação:**

```bash
mvn spring-boot:run

```

A aplicação subirá no perfil `dev` na porta `8080` (por padrão).

## Como testar

A aplicação utiliza o `spring-boot-starter-test` em conjunto com o `spring-security-test` para validações unitárias e de integração. Você pode executar a suíte de testes utilizando o ciclo do Maven:

```bash
mvn test

```

## Melhorias futuras

* **Checkout de Pagamento:** Implementar uma rota e lógica de negócios para atualizar o status do pedido (`OrderStatus`) via Webhooks ou Gateway de pagamento (ex: de `WAITING_PAYMENT` para `PAID`).
* **Cadastro de Usuário:** Criar endpoint aberto para que novos clientes (`ROLE_CLIENT`) consigam se registrar livremente na plataforma antes da compra.
* **Documentação Interativa:** Integrar *SpringDoc OpenAPI* (Swagger) para documentar visualmente e testar os endpoints diretamente do navegador.
* **Gerenciamento de Estoque:** Adicionar a entidade de controle de estoque que será decrementada a cada novo pedido finalizado.

## Autor

**José Victor da Silva**

* **GitHub:** [https://github.com/JoseV-001](https://github.com/JoseV-001)
* **LinkedIn:** [https://www.linkedin.com/in/josé-victor-460175269/](https://www.linkedin.com/in/jos%C3%A9-victor-460175269/)
