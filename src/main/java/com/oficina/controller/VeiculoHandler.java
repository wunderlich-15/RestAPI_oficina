package com.oficina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.dao.VeiculoDAO;
import com.oficina.model.Veiculo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class VeiculoHandler implements HttpHandler {
    private final VeiculoDAO dao = new VeiculoDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                handleGet(exchange, path);
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
            System.err.println("Erro no VeiculoHandler: " + e.getMessage());
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); 
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException, SQLException {
        String query = exchange.getRequestURI().getQuery();
        Long clienteId = null;

       
        if (query != null && query.contains("clienteId=")) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length > 1 && "clienteId".equals(kv[0])) {
                    clienteId = Long.parseLong(kv[1]);
                }
            }
        }

        
        if (clienteId != null) {
            
            List<Veiculo> veiculos = dao.listarPorCliente(clienteId);
            enviarResposta(exchange, veiculos);
        } else {
            
            List<Veiculo> todosVeiculos = dao.listarTodos();
            enviarResposta(exchange, todosVeiculos);
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
    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        byte[] requestBody = exchange.getRequestBody().readAllBytes();
        Veiculo veiculo = objectMapper.readValue(requestBody, Veiculo.class);

        dao.salvar(veiculo);
        String json = objectMapper.writeValueAsString(veiculo);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, json.length()); 

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException, SQLException {
        byte[] requestBody = exchange.getRequestBody().readAllBytes();
        Veiculo veiculo = objectMapper.readValue(requestBody, Veiculo.class);

        if (veiculo.getId() == null) {
            exchange.sendResponseHeaders(400, -1); 
            return;
        }

        dao.atualizar(veiculo);
        exchange.sendResponseHeaders(204, -1); 
    }
    private void handleDelete(HttpExchange exchange, String path) throws IOException, java.sql.SQLException {
        String query = exchange.getRequestURI().getQuery(); // Pega a parte do "?id=1"

        if (query != null && query.contains("id=")) {
            try {
                // Extrai o número depois do "id="
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
}