# 🎓 Plataforma de Mentoria Acadêmica e Profissional

![Status](https://img.shields.io/badge/Status-Iteração%201%20(Configuração)-blue?style=for-the-badge&logo=github)
![Java](https://img.shields.io/badge/Backend-Java%2025%20%7C%20Spring%20Boot-green?style=for-the-badge&logo=spring)
![Angular](https://img.shields.io/badge/Frontend-Angular-red?style=for-the-badge&logo=angular)
![Tailwind](https://img.shields.io/badge/Tailwind-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge)

## 📌 Sobre o Projeto
A **Plataforma de Mentoria** é uma solução de software desenvolvida para o ambiente acadêmico da **Universidade Federal do Agreste de Pernambuco (UFAPE)**.

O sistema conecta discentes (**Mentorados**) a professores, ex-alunos e profissionais experientes (**Mentores**), facilitando o agendamento de mentorias, a troca de conhecimentos e o direcionamento de carreira.

> 📝 Projeto desenvolvido na disciplina de **Engenharia de Software** do curso de Bacharelado em Ciência da Computação.
> * **Professora:** Thaís Alves Burity Rocha
> * **Semestre:** 2025.2

---

## 👥 Equipe de Desenvolvimento
Time responsável pela construção do projeto:

| Nome do Integrante | GitHub |
|:-------------------|:-------|
| **Thayson Guedes de Medeiros** | [![GitHub](https://img.shields.io/badge/GitHub-ThaysonScript-100000?style=flat&logo=github&logoColor=white)](https://github.com/ThaysonScript) |
| **Joneilson César Botelho Júnior** | [![GitHub](https://img.shields.io/badge/GitHub-Joneilson-100000?style=flat&logo=github&logoColor=white)](https://github.com/Joneilson) |
| **Vinícius Mendes de Carvalho** | [![GitHub](https://img.shields.io/badge/GitHub-imvmc-100000?style=flat&logo=github&logoColor=white)](https://github.com/imvmc) |
| **João Victor Iane Góis** | [![GitHub](https://img.shields.io/badge/GitHub-JoaoPresideu-100000?style=flat&logo=github&logoColor=white)](https://github.com/JoaoPresideu) |
| **Maria Luiza Marques da Silva** | [![GitHub](https://img.shields.io/badge/GitHub-TheSerian-100000?style=flat&logo=github&logoColor=white)](https://github.com/TheSerian) |
| **Augusto Sérgio Belarmino Leite** | [![GitHub](https://img.shields.io/badge/GitHub-aaugustoo-100000?style=flat&logo=github&logoColor=white)](https://github.com/aaugustoo) |

---

## 🛠️ Arquitetura e Tecnologias
O projeto segue uma arquitetura moderna dividida em dois módulos:

### 🎨 Frontend (Cliente)
* **Framework:** [Angular](https://angular.io/)
* **Linguagem:** [TypeScript](https://www.typescriptlang.org/)
* **Estilização** [Tailwind](https://tailwindcss.com/docs/installation/framework-guides/angular)
* **Diretório:** [`/frontend`](./frontend)

### ⚙️ Backend (Servidor)
* **Framework:** [Spring Boot](https://spring.io/projects/spring-boot)
* **Linguagem:** [Java 25](https://www.oracle.com/java/technologies/downloads/#java25)
* **Build Tool:** [Maven](https://maven.apache.org/)
* **Diretório:** [`/backend`](./backend)
* **Deploy no Render:** [Mentoria Agil](https://mentoriaagil.onrender.com)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* [Java JDK 25+](https://www.oracle.com/java/technologies/downloads/#java25)
* [Node.js e NPM](https://nodejs.org/)
* [Angular CLI](https://angular.io/cli)
* [Tailwind install](https://tailwindcss.com/docs/installation/framework-guides/angular)

### 1️⃣ Clonar o Repositório
```bash
git clone https://github.com/MentoriaAgil/MentoriaAgil.git
```

### 2️⃣ Executar o Backend
Abra o terminal na pasta `backend` e execute:
```bash
./mvnw spring-boot:run
# O servidor iniciará na porta 8080 confirmando que a configuração do Spring Boot está correta.
```

### 3️⃣ Executar o Frontend
Abra o terminal na pasta `frontend` e execute:
```bash
npm install
ng serve
# Acesse no navegador: http://localhost:4200 para ver a tela inicial do Angular.
```

---

## 📅 Organização e Metodologia
* **Metodologia:** Scrum
* **Ferramenta:** GitHub Projects (Template "Iterative Development")
* **Status:** ✅ Configuração Inicial Concluída

---

---

## 🌎 Gestão de Ambientes

O backend do sistema foi configurado com múltiplos perfis de execução para suportar os diferentes ambientes de desenvolvimento, testes e produção:

| Ambiente | Perfil Spring | Arquivo de Configuração | Descrição |
|----------|---------------|--------------------------|-----------|
| Desenvolvimento | dev | application.properties | Execução local |
| Testes | test | application-test.properties | Utilizado no GitHub Actions com banco H2 em memória |
| Produção | prod | application-prod.properties | Deploy no Render com PostgreSQL |

A ativação do perfil é realizada através da variável de ambiente:

SPRING_PROFILES_ACTIVE

---

---

### 🧪 Ambiente de Testes

O ambiente de testes é utilizado durante a execução do pipeline de Integração Contínua (CI) no GitHub Actions.

Nesse ambiente:

- O perfil `test` é ativado automaticamente;
- Utiliza-se o banco de dados H2 em memória;
- As configurações são carregadas a partir do arquivo:

backend/src/main/resources/application-test.properties

---

---

### 🚀 Ambiente de Produção

O ambiente de produção está implantado na plataforma Render, utilizando:

- Perfil Spring: `prod`
- Banco de Dados: PostgreSQL (Render)
- Variáveis de ambiente para conexão segura com o banco de dados
- Configurações carregadas a partir do arquivo:

backend/src/main/resources/application-prod.properties

---

---

## 🌐 Deploy em Produção

O sistema encontra-se implantado na plataforma Render:

### 🔗 Backend
[https://mentoria-agil-backend-1f3r.onrender.com](https://mentoria-agil-backend-1f3r.onrender.com)

### 🎨 Frontend
[![STATUS](https://img.shields.io/badge/STATUS-ONLINE-green)](https://stats.uptimerobot.com/u5ZyVIiXk0)
<br>
[https://mentoria-agil-frontend.onrender.com](https://mentoria-agil-frontend.onrender.com)

### Service Status Page
#### https://stats.uptimerobot.com/u5ZyVIiXk0

### Actions Status Badge
[![CI Backend - Mentoria Ágil](https://github.com/ThaysonScript/MentoriaAgil/actions/workflows/backend-ci.yml/badge.svg?branch=main)](https://github.com/ThaysonScript/MentoriaAgil/actions/workflows/backend-ci.yml)

[![Frontend CI - Mentoria Ágil](https://github.com/ThaysonScript/MentoriaAgil/actions/workflows/frontend-ci.yml/badge.svg)](https://github.com/ThaysonScript/MentoriaAgil/actions/workflows/frontend-ci.yml)
---
