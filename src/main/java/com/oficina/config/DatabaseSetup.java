package com.oficina.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void inicializarBanco() {
        String[] queries = {
            // 1. Clientes
            """
            CREATE TABLE IF NOT EXISTS clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf TEXT UNIQUE NOT NULL,
                telefone TEXT NOT NULL
            );
            """,
            // 2. Veículos
            """
            CREATE TABLE IF NOT EXISTS veiculos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                placa TEXT UNIQUE NOT NULL,
                modelo TEXT NOT NULL,
                marca TEXT NOT NULL,
                ano INTEGER,
                cliente_id INTEGER NOT NULL,
                FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
            );
            """,
            // 3. Serviços (Catálogo)
            """
            CREATE TABLE IF NOT EXISTS servicos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome_servico TEXT NOT NULL,
                valor_base DECIMAL(10, 2) NOT NULL
            );
            """,
            // 4. Ordens de Serviço
            """
            CREATE TABLE IF NOT EXISTS ordens_servico (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descricao_problema TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'ABERTA',
                data_abertura DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
                data_fechamento DATETIME,
                valor_total DECIMAL(10, 2) DEFAULT 0.00,
                veiculo_id INTEGER NOT NULL,
                FOREIGN KEY (veiculo_id) REFERENCES veiculos(id) ON DELETE CASCADE
            );
            """,
            // 5. Itens de Serviço (Relacionamento N..N)
            """
            CREATE TABLE IF NOT EXISTS itens_servico (
                os_id INTEGER NOT NULL,
                servico_id INTEGER NOT NULL,
                PRIMARY KEY (os_id, servico_id),
                FOREIGN KEY (os_id) REFERENCES ordens_servico(id) ON DELETE CASCADE,
                FOREIGN KEY (servico_id) REFERENCES servicos(id)
            );
            """
        };

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            for (String sql : queries) {
                stmt.execute(sql);
            }
            
            System.out.println("Banco de dados SQLite (oficina.db) configurado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro crítico ao inicializar o banco de dados:");
            e.printStackTrace();
        }
    }
}