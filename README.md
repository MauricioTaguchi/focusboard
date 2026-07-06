# FocusBoard

Aplicação desktop desenvolvida em **Java Swing** para gerenciamento de tarefas.

O projeto permite criar, concluir, excluir, buscar e filtrar tarefas, mantendo os dados salvos localmente.

Este projeto demonstra conhecimentos em **Java, interface gráfica, persistência local, organização com Maven e estruturação de código orientada a objetos**.

## Objetivo do projeto

O objetivo do FocusBoard é criar uma aplicação simples e funcional para organização de tarefas diárias, com uma interface limpa e recursos úteis para produtividade.

Além da funcionalidade, o projeto foi desenvolvido para demonstrar boas práticas de organização de código em uma aplicação desktop Java.

## Funcionalidades

- Cadastro de tarefas
- Marcação de tarefas como concluídas
- Exclusão de tarefas
- Busca por título ou prioridade
- Filtro por todas, ativas ou concluídas
- Limpeza de tarefas concluídas
- Contadores de tarefas totais, ativas e concluídas
- Persistência automática dos dados localmente

## Tecnologias utilizadas

- Java 17
- Java Swing
- Maven
- GitHub Actions

## Estrutura do projeto

```text
src/main/java/com/example/focusboard/
├── FocusBoardApp.java          # Janela principal e composição da interface
├── Task.java                   # Modelo de domínio da tarefa
├── Priority.java               # Enum de prioridade
├── FilterMode.java             # Enum de filtros
├── TaskTableModel.java         # Modelo da tabela e lógica de filtros
├── TaskRepository.java         # Persistência local
└── SimpleDocumentListener.java # Listener para busca em tempo real
```

## Como executar o projeto

### Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

### Executar com Maven

```bash
mvn clean package
mvn exec:java
```

### Executar o arquivo JAR

```bash
java -jar target/focusboard-1.0.0.jar
```

### Executar sem Maven

```bash
mkdir -p out
javac -d out src/main/java/com/example/focusboard/*.java
java -cp out com.example.focusboard.FocusBoardApp
```

## O que este projeto demonstra

- Desenvolvimento de aplicação desktop com Java Swing
- Programação orientada a objetos
- Manipulação de eventos de interface
- Uso de tabelas com JTable
- Separação entre interface, modelo, filtros e persistência
- Persistência local com Java NIO
- Organização de projeto com Maven
- Configuração de CI com GitHub Actions

## Possíveis melhorias

- Adicionar datas de vencimento
- Criar ordenação por prioridade e data
- Adicionar temas claro e escuro
- Implementar testes unitários
- Adicionar exportação para CSV
- Criar empacotamento executável para Windows

## Autor

Maurício Ryo Toita Taguchi  
GitHub: [MauricioTaguchi](https://github.com/MauricioTaguchi)
