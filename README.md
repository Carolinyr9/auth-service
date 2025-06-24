# 🔐 **Authentication and Authorization Microservice**

## 📌 Objetivo e público-alvo da API

Este microserviço foi desenvolvido para fornecer uma plataforma robusta de autenticação e autorização, com funcionalidades de login, geração de token JWT, controle de acesso baseado em papéis e segurança de rotas. O público-alvo inclui:

* Desenvolvedores backend e frontend que desejam integrar funcionalidades de autenticação e autorização em suas aplicações.
* Equipes de QA que precisam testar endpoints REST com autenticação JWT.
* Administradores do sistema responsáveis pela gestão de acessos e controle de permissões.

---

## ⚙️ Funcionalidades implementadas

As funcionalidades foram desenvolvidas com base em histórias de usuário, incluindo:

* ✅ **Cadastro de usuários e autenticação via JWT**
* ✅ **Login com autenticação de senha segura**
* ✅ **Geração de token JWT após login**
* ✅ **Controle de acesso com base em papéis (ADMIN, USER)**
* ✅ **Proteção de endpoints com Spring Security**
* ✅ **Autorização de recursos com base em papéis e permissões**

---

## 🚀 Instruções de execução local

### ✅ Pré-requisitos

* Java 17+
* Maven 3.8+
* PostgreSQL (ou outro banco de dados compatível configurado no `application.properties`)

### 🔧 Build

```bash
./mvnw clean package
```

### ▶️ Run

```bash
./mvnw spring-boot:run
```

Ou execute o .jar:

```bash
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

---

## 🔐 Como obter o token JWT e testar os endpoints

### 1. Obtenha um token

Envie uma requisição POST para:

```http
POST /api/auth/login
```

Corpo da requisição:

```json
{
  "username": "admin",
  "password": "AdminPassword123!"
}
```

Resposta:

```json
"eyJhbGciOiJIUzI1NiIsInR..."  
```

### 2. Use o token nos demais endpoints

Adicione no header das requisições:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Você pode testar com Postman, Insomnia ou Swagger.

---

## 🗂️ Resumo do modelo de dados e regras de validação

### 🧑‍💻 Usuário

| Campo      | Descrição                   | Validação                          |
| ---------- | --------------------------- | ---------------------------------- |
| `username` | Nome de usuário             | Obrigatório, único                 |
| `email`    | Endereço de e-mail          | Obrigatório, formato válido, único |
| `password` | Senha do usuário            | Obrigatório, armazenada com BCrypt |
| `roles`    | Papéis de acesso do usuário | Um ou mais (ex: `ADMIN`, `USER`)   |

---

## 🔐 Autenticação e Autorização

* **JWT**: Gerado após autenticação válida, com expiração e assinatura segura.
* **Spring Security**: Gerencia login, logout, autenticação e controle de rotas.
* **Papéis (roles)**:

  * `ROLE_ADMIN`: acesso total ao sistema
  * `ROLE_USER`: acesso restrito (sem edição de dados, por exemplo)

As rotas são protegidas por filtros e regras de segurança configuradas.


📬 Em caso de dúvidas ou sugestões, fique à vontade para abrir uma *issue* ou enviar um *pull request*!

---

Esse modelo segue a estrutura original com foco em autenticação e autorização. Caso precise de mais detalhes ou ajustes, estou à disposição!
