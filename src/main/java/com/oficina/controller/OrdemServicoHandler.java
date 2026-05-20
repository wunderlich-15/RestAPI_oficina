package com.oficina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.oficina.dao.OrdemServicoDAO;
import com.oficina.model.OrdemServico;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class OrdemServicoHandler implements HttpHandler {

    private final OrdemServicoDAO osDAO = new OrdemServicoDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        String method = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    tratarGet(exchange);
                    break;
                case "POST":
                    tratarPost(exchange);
                    break;
                case "PUT":
                    tratarPut(exchange);
                    break;
                case "DELETE":
                    tratarDelete(exchange);
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

    private void tratarGet(HttpExchange exchange) throws SQLException, IOException {
        String query = exchange.getRequestURI().getQuery();
    
        if (query != null && query.contains("id=")) {
            Long id = Long.parseLong(query.split("id=")[1]);
            OrdemServico os = osDAO.buscarPorId(id);
            
            if (os != null) {
                enviarResposta(exchange, 200, mapper.writeValueAsString(os));
            } else {
                enviarResposta(exchange, 404, "{\"erro\": \"Ordem de Serviço não encontrada\"}");
            }
        } else {
            // LISTAR TODAS
            List<OrdemServico> lista = osDAO.listarTodas();
            enviarResposta(exchange, 200, mapper.writeValueAsString(lista));
        }
    }

    private void tratarPost(HttpExchange exchange) throws IOException, SQLException {
        OrdemServico os = mapper.readValue(exchange.getRequestBody(), OrdemServico.class);
        
        if (os.getVeiculoId() == null || os.getDescricaoProblema() == null || os.getDescricaoProblema().trim().isEmpty()) {
            enviarResposta(exchange, 400, "{\"erro\": \"Os campos 'veiculoId' e 'descricaoProblema' são obrigatórios.\"}");
            return;
        }

        if (os.getStatus() == null) {
            os.setStatus("ABERTA");
        }

        osDAO.salvar(os);
        String jsonResposta = mapper.writeValueAsString(os);
        enviarResposta(exchange, 201, jsonResposta);
    }

    private void tratarPut(HttpExchange exchange) throws IOException, SQLException {
        OrdemServico os = mapper.readValue(exchange.getRequestBody(), OrdemServico.class);

        if (os.getId() == null) {
            enviarResposta(exchange, 400, "{\"erro\": \"O campo 'id' é obrigatório.\"}");
            return;
        }

        osDAO.atualizar(os);
        enviarResposta(exchange, 200, "{\"mensagem\": \"Ordem de Serviço atualizada com sucesso!\"}");
    }

    private void tratarDelete(HttpExchange exchange) throws SQLException, IOException {
        String query = exchange.getRequestURI().getQuery();
        Long id = null;

        if (query != null && query.contains("id=")) {
            id = Long.parseLong(query.split("id=")[1]);
        }

        if (id == null) {
            enviarResposta(exchange, 400, "{\"erro\": \"O parâmetro 'id' é obrigatório na URL.\"}");
            return;
        }

        osDAO.deletar(id);
        enviarResposta(exchange, 200, "{\"mensagem\": \"Ordem de Serviço excluída com sucesso!\"}");
    }

    private void enviarResposta(HttpExchange exchange, int statusCode, String resposta) throws IOException {
        byte[] bytes = resposta.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}