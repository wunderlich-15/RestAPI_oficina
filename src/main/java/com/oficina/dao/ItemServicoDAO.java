package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.ItemServico;
import com.oficina.model.ItemServicoDetalhado;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemServicoDAO {

    public void adicionarServicoOs(ItemServico item) throws SQLException {
        String sql = "INSERT INTO itens_servico (os_id, servico_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, item.getOsId());
            stmt.setLong(2, item.getServicoId());
            stmt.executeUpdate();
        }
    }

    public void removerServicoOs(Long osId, Long servicoId) throws SQLException {
        String sql = "DELETE FROM itens_servico WHERE os_id = ? AND servico_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, osId);
            stmt.setLong(2, servicoId);
            stmt.executeUpdate();
        }
    }
    
    public List<Long> listarServicosDaOs(Long osId) throws SQLException {
        String sql = "SELECT servico_id FROM itens_servico WHERE os_id = ?";
        List<Long> ids = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, osId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getLong("servico_id"));
            }
        }
        return ids;
    }

    public List<ItemServicoDetalhado> listarItensComDetalhes(Long osId) throws SQLException {
    String sql = "SELECT s.id, s.nome_servico, s.valor_base " +
                 "FROM itens_servico i " +
                 "JOIN servicos s ON i.servico_id = s.id " +
                 "WHERE i.os_id = ?";
    
        List<ItemServicoDetalhado> lista = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, osId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(new ItemServicoDetalhado(
                    rs.getLong("id"),
                    rs.getString("nome_servico"),
                    rs.getBigDecimal("valor_base")
                ));
            }
        }
        return lista;
    }

    public BigDecimal buscarPrecoServico(Long servicoId) throws SQLException {
    String sql = "SELECT valor_base FROM servicos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, servicoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("valor_base");
            }
        }
        return BigDecimal.ZERO;
    }
}