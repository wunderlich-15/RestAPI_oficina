CREATE DATABASE IF NOT EXISTS oficina_db;
USE oficina_db;

CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL
);


CREATE TABLE veiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) UNIQUE NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    ano INT,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

CREATE TABLE servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_servico VARCHAR(100) NOT NULL,
    valor_base DECIMAL(10, 2) NOT NULL
);


CREATE TABLE ordens_servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao_problema TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ABERTA', 
    data_abertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fechamento DATETIME,
    valor_total DECIMAL(10, 2) DEFAULT 0.00,
    veiculo_id BIGINT NOT NULL,
    CONSTRAINT fk_os_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id) ON DELETE CASCADE
);


CREATE TABLE itens_servico (
    os_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    PRIMARY KEY (os_id, servico_id), -- Chave primária composta
    CONSTRAINT fk_item_os FOREIGN KEY (os_id) REFERENCES ordens_servico(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servicos(id)
);