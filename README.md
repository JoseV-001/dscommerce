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
* H2 Database (Banco de dados em memória para testes)
* Maven

**Testes:**

* Spring Boot Starter Test (JUnit 5, Mockito)
* Spring Security Test

## Estrutura do projeto

Abaixo está a representação da arquitetura de diretórios do sistema:

```text
com.josev001.dscommerce
├── config/
│   ├── customgrant/         # Implementação customizada do Password Grant para OAuth2
│   ├── AuthorizationServerConfig.java
│   └── ResourceServerConfig.java
├── controllers/
│   ├── handlers/            # Interceptadores globais de erro (ControllerAdvice)
│   ├── CategoryController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   └── UserController.java
├── dto/                     # Classes de Transferência de Dados e Validações
├── entities/                # Classes de domínio e mapeamento ORM (JPA)
├── projections/             # Interfaces de projeção de dados (SQL Nativo)
├── repositories/            # Interfaces de persistência com Spring Data JPA
├── services/
│   ├── exceptions/          # Exceções de negócio customizadas
│   ├── AuthService.java
│   ├── CategoryService.java
│   ├── OrderService.java
│   ├── ProductService.java
│   └── UserService.java
├── DscommerceApplication.java # Classe principal de inicialização
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

A aplicação está preparada para suportar bancos de dados relacionais em ambiente de produção (MySQL/PostgreSQL), onde o script de banco de dados cria automaticamente o schema no MySQL.

Para desenvolvimento e testes (perfil `test` em `application-test.properties`), o projeto utiliza o banco de dados em memória **H2**.

* **Migração / Inicialização:** A aplicação utiliza a estratégia do JPA para validação e criação automática de tabelas. O arquivo `import.sql` localizado em `src/main/resources` é executado automaticamente no arranque da aplicação para realizar a carga de dados iniciais (*seeding*), criando categorias, produtos, usuários de teste e pedidos de exemplo.

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


3. **Configure as propriedades da aplicação (Opcional):**
O projeto utiliza o perfil `test` por padrão. Para modificar credenciais JWT, CORS, ou conectar a um banco externo, edite o arquivo `src/main/resources/application.properties` e defina `spring.profiles.active=dev` (ou `prod`), ajustando as propriedades adequadas.
4. **Execute a aplicação:**
```bash
mvn spring-boot:run

```


A aplicação subirá na porta `8080` (por padrão). O banco de dados H2 poderá ser acessado em `http://localhost:8080/h2-console`.

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
