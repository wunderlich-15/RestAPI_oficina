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

- Endpoints
  <p> Os endpoints suportam Post, Put, Delete e Get sendo o Put e Delete necessário passar o ID na url como por exemplo </p>
  
  ``` http://localhost:8080/api/ordens-servico?id=1 ```

  - Clientes
    ``` http://localhost:8080/api/clientes ```

    Corpo de requisição

    ```
        {
          "nome": "Carlos Silva",
          "cpf": "123.456.789-00",
          "telefone": "(11) 99999-1111"
        }
      
    ```

    

  - Veiculos
    ``` http://localhost:8080/api/veiculos ```

    
    Corpo de requisição

    ```
        {
          "placa": "XYZ-9876",
          "modelo": "Corolla",
          "marca": "Toyota",
          "ano": 2020,
          "clienteId": 1
        }
    ```

  - Ordens
    ``` http://localhost:8080/api/ordens-servico ```

    
    Corpo de requisição

    ```
        {
          "descricaoProblema": "Revisão de 50.000 km",
          "veiculoId": 1,
          "valorTotal": 50.00
        }
    ```

  - Serviços
    ``` http://localhost:8080/api/servicos ```

    
    Corpo de requisição

    ```
      {
        "nomeServico": "Alinhamento e Balanceamento",
        "valorBase": 120.00
      }
      
    ```

  - Adicionar Serviços a Ordem
    ``` http://localhost:8080/api/itens-servico ```

    
    Corpo de requisição

    ```
    {
      "osId": 1,
      "servicoId": 1
    }
    
    ```


## Diagramas do projeto
<img width="763" height="492" alt="Image" src="https://github.com/user-attachments/assets/fc01bafa-816b-463c-b8ba-6236a2037b6a" />
<img width="717" height="742" alt="Image" src="https://github.com/user-attachments/assets/bf8fcae9-8a5d-493f-b7ac-0532f4b57e18" />
