# Api Oficina

## Introdução

<p> Este projeto é um Sistema de Gestão de Ordens de Serviço (O.S.) desenvolvido para o setor automotivo (oficinas mecânicas). 
Ele foi construído seguindo os princípios de uma arquitetura RESTful, focada em robustez de dados, automação de cálculos e integridade relacional.</p>

<p>O sistema automatiza o ciclo de vida do atendimento em uma oficina. Ele permite gerenciar o banco de dados de clientes e veículos, criar ordens de serviço com valores iniciais (taxas de diagnóstico) e vincular serviços de um catálogo pré-definido. 
O diferencial está na inteligência de negócio, onde o sistema recalcula o valor total da fatura em tempo real conforme itens são adicionados ou removidos.</p>

## Como executar o Projeto

### Pré-requisitos

Para rodar este projeto, a máquina precisa ter apenas:

- Java JDK 17 (ou superior).

- Uma IDE de sua preferência (VS Code, IntelliJ IDEA ou Eclipse).

- Maven para o gerenciamento  das bibliotecas.

- Uma API Client a sua preferência (Bruno, Postman ou Thunder Client)

### Execução do projeto

- Sincronizar o Maven
  <p>Ao abrir o projeto na sua IDE, aguarde o Maven baixar as dependências listadas no pom.xml.</p>

- Iniciar a Aplicação
  <p>Abra a classe principal App.java e execute o projeto. O console exibirá as mensagens de sucesso indicando que o banco foi configurado e o servidor está rodando na porta 8080.</p>

- Testar a API
  <p>Utilize a Api CLient para realizar as requisições a API</p>
