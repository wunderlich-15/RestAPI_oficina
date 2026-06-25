package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.ItemServico;
import com.oficina.model.ItemServicoDetalhado;
import com.oficina.model.Servico;
import com.oficina.model.Veiculo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemServicoDAO {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    public void adicionarServicoOs(ItemServico item) throws SQLException {
        String sql = "INSERT INTO itens_servico (os_id, servico_id) VALUES (?, ?)";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, item.getOsId());
            stmt.setLong(2, item.getServicoId());
            stmt.executeUpdate();
        }
    }

    public void removerServicoOs(Long osId, Long servicoId) throws SQLException {
        String sql = "DELETE FROM itens_servico WHERE os_id = ? AND servico_id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);
            stmt.setLong(2, servicoId);
            stmt.executeUpdate();
        }
    }

    public List<Long> listarServicosDaOs(Long osId) throws SQLException {
        String sql = "SELECT servico_id FROM itens_servico WHERE os_id = ?";

        List<Long> ids = new ArrayList<>();

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getLong("servico_id"));
            }
        }

        return ids;
    }

    public List<ItemServicoDetalhado> listarItensComDetalhes(Long osId) throws SQLException {
        String sql = """
                SELECT s.id, s.nome_servico, s.valor_base
                FROM itens_servico i
                JOIN servicos s ON i.servico_id = s.id
                WHERE i.os_id = ?
                """;

        List<ItemServicoDetalhado> lista = new ArrayList<>();

        Veiculo veiculo = buscarVeiculoDaOs(osId);

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Servico servico = new Servico(
                        rs.getLong("id"),
                        rs.getString("nome_servico"),
                        rs.getBigDecimal("valor_base")
                );

                BigDecimal valorCalculado = servico.calcularValor(veiculo);

                lista.add(new ItemServicoDetalhado(
                        servico.getId(),
                        servico.getNomeServico(),
                        valorCalculado
                ));
            }
        }

        return lista;
    }

    public BigDecimal buscarPrecoServico(Long servicoId) throws SQLException {
        String sql = "SELECT valor_base FROM servicos WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, servicoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("valor_base");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal buscarPrecoServicoComMaoDeObra(Long osId, Long servicoId) throws SQLException {
        Veiculo veiculo = buscarVeiculoDaOs(osId);
        Servico servico = servicoDAO.buscarPorId(servicoId);

        if (servico == null) {
            return BigDecimal.ZERO;
        }

        return servico.calcularValor(veiculo);
    }

    private Veiculo buscarVeiculoDaOs(Long osId) throws SQLException {
        String sql = "SELECT veiculo_id FROM ordens_servico WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long veiculoId = rs.getLong("veiculo_id");
                return veiculoDAO.buscarPorId(veiculoId);
            }
        }

        return null;
    }

    public void substituirServicosDaOs(Long osId, List<Long> servicosIds) throws SQLException {
        String sqlDelete = "DELETE FROM itens_servico WHERE os_id = ?";
        String sqlInsert = "INSERT INTO itens_servico (os_id, servico_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
                    PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)
            ) {
                stmtDelete.setLong(1, osId);
                stmtDelete.executeUpdate();

                if (servicosIds != null) {
                    for (Long servicoId : servicosIds) {
                        stmtInsert.setLong(1, osId);
                        stmtInsert.setLong(2, servicoId);
                        stmtInsert.addBatch();
                    }

                    stmtInsert.executeBatch();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        }
}