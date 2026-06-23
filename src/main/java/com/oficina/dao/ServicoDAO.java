package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO implements CrudDAO<Servico> {

    public void salvar(Servico servico) throws SQLException {
        String sql = "INSERT INTO servicos (nome_servico, valor_base) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, servico.getNomeServico());
            stmt.setBigDecimal(2, servico.getValorBase());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) servico.setId(rs.getLong(1));
        }
    }

    public List<Servico> listarTodos() throws SQLException {
        String sql = "SELECT * FROM servicos";
        List<Servico> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Servico(rs.getLong("id"), rs.getString("nome_servico"), rs.getBigDecimal("valor_base")));
            }
        }
        return lista;
    }
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Servico buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM servicos WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Servico s = new Servico();
                    s.setId(rs.getLong("id"));
                    s.setNomeServico(rs.getString("nome_servico"));
                    s.setValorBase(rs.getBigDecimal("valor_base"));
                    return s;
                }
            }
        }
        return null;
    }

    public void atualizar(Servico servico) throws SQLException {
    String sql = "UPDATE servicos SET nome_servico = ?, valor_base = ? WHERE id = ?";
    
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, servico.getNomeServico());
            stmt.setBigDecimal(2, servico.getValorBase());
            stmt.setLong(3, servico.getId());
            
            stmt.executeUpdate();
        }
    }
}