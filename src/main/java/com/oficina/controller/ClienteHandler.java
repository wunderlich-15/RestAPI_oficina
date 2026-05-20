package com.oficina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.dao.ClienteDAO;
import com.oficina.model.Cliente;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ClienteHandler implements HttpHandler {
    private final ClienteDAO dao = new ClienteDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

   @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            // Roteamento simples baseado no método HTTP
            if ("GET".equals(method)) {
                handleGet(exchange);
            } else if ("POST".equals(method)) {
                handlePost(exchange);
            } else if ("PUT".equals(method)) {
                handlePut(exchange);
            } else if ("DELETE".equals(method)) {
                handleDelete(exchange, path);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, java.sql.SQLException {
        Cliente cliente = objectMapper.readValue(exchange.getRequestBody(), Cliente.class);
        dao.atualizar(cliente);
        exchange.sendResponseHeaders(204, -1);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, java.sql.SQLException {
        String[] parts = path.split("/");
        if (parts.length > 3) {
            Long id = Long.parseLong(parts[3]);
            dao.deletar(id);
            exchange.sendResponseHeaders(204, -1);
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
    private void handleGet(HttpExchange exchange) throws IOException, java.sql.SQLException {
        List<Cliente> clientes = dao.listarTodos();
        String json = objectMapper.writeValueAsString(clientes);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length());
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Cliente cliente = objectMapper.readValue(exchange.getRequestBody(), Cliente.class);
        
        try {
            dao.salvar(cliente);
            String json = objectMapper.writeValueAsString(cliente);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, json.length());
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(json.getBytes());
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(400, -1);
        }
    }
}