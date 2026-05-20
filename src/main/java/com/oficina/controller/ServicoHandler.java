package com.oficina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.dao.ServicoDAO;
import com.oficina.model.Servico;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class ServicoHandler implements HttpHandler {
    private final ServicoDAO dao = new ServicoDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();


        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    enviarResposta(exchange, 405, "{\"erro\": \"Método não permitido\"}");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            enviarResposta(exchange, 500, "{\"erro\": \"Erro no banco de dados: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            enviarResposta(exchange, 400, "{\"erro\": \"Erro na requisição: " + e.getMessage() + "\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException {
        List<Servico> servicos = dao.listarTodos();
        String json = objectMapper.writeValueAsString(servicos);
        enviarResposta(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        Servico servico = objectMapper.readValue(body, Servico.class);

        dao.salvar(servico);
        String json = objectMapper.writeValueAsString(servico);
        enviarResposta(exchange, 201, json);
    }

    private void handlePut(HttpExchange exchange) throws IOException, SQLException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        Servico servico = objectMapper.readValue(body, Servico.class);

        if (servico.getId() == null) {
            enviarResposta(exchange, 400, "{\"erro\": \"O ID do serviço é obrigatório para atualização.\"}");
            return;
        }

        dao.atualizar(servico);
        String json = objectMapper.writeValueAsString(servico);
        enviarResposta(exchange, 200, json);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        String[] parts = path.split("/");
        
        if (parts.length >= 4) { 
            try {
                Long id = Long.parseLong(parts[3]);
                dao.deletar(id);
                enviarResposta(exchange, 200, "{\"mensagem\": \"Serviço excluído com sucesso\"}");
            } catch (NumberFormatException e) {
                enviarResposta(exchange, 400, "{\"erro\": \"ID inválido na URL\"}");
            }
        } else {
            enviarResposta(exchange, 400, "{\"erro\": \"ID não fornecido na URL\"}");
        }
    }

    private void enviarResposta(HttpExchange exchange, int codigo, String json) throws IOException {
        byte[] responseBytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(codigo, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}