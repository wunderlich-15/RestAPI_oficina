package com.oficina;

import com.oficina.config.DatabaseSetup; // <-- NOVO IMPORT AQUI
import com.oficina.controller.ClienteHandler;
import com.oficina.controller.ItemServicoHandler;
import com.oficina.controller.OrdemServicoHandler;
import com.oficina.controller.VeiculoHandler;
import com.oficina.controller.ServicoHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) {
        
        // 1. GERA O BANCO DE DADOS E AS TABELAS ANTES DO SERVIDOR SUBIR
        DatabaseSetup.inicializarBanco();
        
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/api/clientes", new ClienteHandler());
            server.createContext("/api/veiculos", new VeiculoHandler());
            server.createContext("/api/ordens-servico", new OrdemServicoHandler());
            server.createContext("/api/servicos", new ServicoHandler());
            server.createContext("/api/itens-servico", new ItemServicoHandler());

            server.setExecutor(null);
            
            // Ajustei a mensagem para ficar mais geral
            System.out.println("Servidor API Oficina rodando em: http://localhost:8080/api/clientes");
            server.start();

        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}