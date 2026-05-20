package com.oficina.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.dao.ItemServicoDAO;
import com.oficina.dao.OrdemServicoDAO;
import com.oficina.model.ItemServico;
import com.oficina.model.ItemServicoDetalhado;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemServicoHandler implements HttpHandler {
    private final ItemServicoDAO itemDao = new ItemServicoDAO();
    private final OrdemServicoDAO osDao = new OrdemServicoDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    enviarResposta(exchange, 405, "{\"erro\": \"Método não permitido\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            enviarResposta(exchange, 500, "{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = getQueryParams(exchange.getRequestURI());
        String osIdStr = params.get("osId");

        if (osIdStr == null) {
            enviarResposta(exchange, 400, "{\"erro\": \"O parâmetro osId é obrigatório na URL.\"}");
            return;
        }

        List<ItemServicoDetalhado> itens = itemDao.listarItensComDetalhes(Long.parseLong(osIdStr));
        enviarResposta(exchange, 200, mapper.writeValueAsString(itens));
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        ItemServico item = mapper.readValue(exchange.getRequestBody(), ItemServico.class);
    

        BigDecimal preco = itemDao.buscarPrecoServico(item.getServicoId());
        

        itemDao.adicionarServicoOs(item);
        

        osDao.atualizarValorTotalComItem(item.getOsId(), preco, true);
        
        enviarResposta(exchange, 201, "{\"mensagem\": \"Serviço adicionado e valor somado!\"}");

    }

    private void handleDelete(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = getQueryParams(exchange.getRequestURI());
        Long osId = Long.parseLong(params.get("osId"));
        Long servId = Long.parseLong(params.get("servicoId"));


        BigDecimal preco = itemDao.buscarPrecoServico(servId);
        
        itemDao.removerServicoOs(osId, servId);
        

        osDao.atualizarValorTotalComItem(osId, preco, false);
        
        enviarResposta(exchange, 200, "{\"mensagem\": \"Serviço removido e valor subtraído!\"}");
    }

    private Map<String, String> getQueryParams(URI uri) {
        Map<String, String> queryParams = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) queryParams.put(entry[0], entry[1]);
            }
        }
        return queryParams;
    }

    private void enviarResposta(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}