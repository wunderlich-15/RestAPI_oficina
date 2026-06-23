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
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("id=")) {
            try {
                String idString = query.split("id=")[1].split("&")[0];
                Long id = Long.parseLong(idString);
                
                dao.deletar(id);
                
                exchange.sendResponseHeaders(204, -1);
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
            }
        } else {
            exchange.sendResponseHeaders(400, -1);
        }
    }
    private void handleGet(HttpExchange exchange) throws IOException, java.sql.SQLException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("id=")) {
            String valorId = query.split("id=")[1].split("&")[0];
            Long id = Long.parseLong(valorId);
            
            Cliente cliente = dao.buscarPorId(id);
            if (cliente != null) {
                enviarResposta(exchange, cliente);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } else {
            List<Cliente> clientes = dao.listarTodos();
            enviarResposta(exchange, clientes);
        }
    }

    private void enviarResposta(HttpExchange exchange, Object objeto) throws IOException {
        String json = objectMapper.writeValueAsString(objeto);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, json.getBytes().length);
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