# 🎓 Plataforma de Mentoria Acadêmica e Profissional

![Status](https://img.shields.io/badge/Status-Iteração%201%20(Configuração)-blue?style=for-the-badge&logo=github)
![Java](https://img.shields.io/badge/Backend-Java%2017%20%7C%20Spring%20Boot-green?style=for-the-badge&logo=spring)
![Angular](https://img.shields.io/badge/Frontend-Angular-red?style=for-the-badge&logo=angular)
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

---

## 🛠️ Arquitetura e Tecnologias
O projeto segue uma arquitetura moderna dividida em dois módulos:

### 🎨 Frontend (Cliente)
* **Framework:** [Angular](https://angular.io/)
* **Linguagem:** TypeScript
* **Diretório:** [`/frontend`](./frontend)

### ⚙️ Backend (Servidor)
* **Framework:** [Spring Boot](https://spring.io/projects/spring-boot)
* **Linguagem:** Java 17
* **Build Tool:** Maven
* **Diretório:** [`/backend`](./backend)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java JDK 17+
* Node.js e NPM
* Angular CLI

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
