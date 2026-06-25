package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.OrdemServico;
import com.oficina.model.Servico;
import com.oficina.model.Veiculo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDAO {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    public void salvar(OrdemServico os) throws SQLException {
        String sql = """
                INSERT INTO ordens_servico
                (
                    descricao_problema,
                    status,
                    veiculo_id,
                    valor_inicial,
                    valor_total
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        BigDecimal valorInicial = BigDecimal.ZERO;

        if (os.getValorInicial() != null) {
            valorInicial = os.getValorInicial();
        } else if (os.getValorTotal() != null) {
            valorInicial = os.getValorTotal();
        }

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, os.getDescricaoProblema());

            if (os.getStatus() == null || os.getStatus().isBlank()) {
                stmt.setString(2, "ABERTA");
                os.setStatus("ABERTA");
            } else {
                stmt.setString(2, os.getStatus());
            }

            stmt.setLong(3, os.getVeiculoId());
            stmt.setBigDecimal(4, valorInicial);
            stmt.setBigDecimal(5, valorInicial);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    os.setId(generatedKeys.getLong(1));
                }
            }

            os.setValorInicial(valorInicial);
            os.setValorTotal(valorInicial);
        }
    }

    public List<OrdemServico> listarTodas() throws SQLException {
        String sql = "SELECT * FROM ordens_servico";

        List<OrdemServico> lista = new ArrayList<>();
        ItemServicoDAO itensDAO = new ItemServicoDAO();

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Long idOs = rs.getLong("id");

                OrdemServico os = new OrdemServico();
                os.setId(idOs);
                os.setDescricaoProblema(rs.getString("descricao_problema"));
                os.setStatus(rs.getString("status"));
                os.setDataAbertura(rs.getTimestamp("data_abertura"));
                os.setDataFechamento(rs.getTimestamp("data_fechamento"));
                os.setValorInicial(rs.getBigDecimal("valor_inicial"));
                os.setValorTotal(rs.getBigDecimal("valor_total"));
                os.setVeiculoId(rs.getLong("veiculo_id"));

                os.setItens(itensDAO.listarItensComDetalhes(idOs));

                lista.add(os);
            }
        }

        return lista;
    }

    public void atualizar(OrdemServico os) throws SQLException {
        String sql = """
                UPDATE ordens_servico
                SET descricao_problema = ?,
                    status = ?,
                    data_fechamento = ?,
                    valor_inicial = ?,
                    veiculo_id = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, os.getDescricaoProblema());
            stmt.setString(2, os.getStatus());

            if ("FINALIZADA".equalsIgnoreCase(os.getStatus())) {
                Timestamp dataAgora = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(3, dataAgora);
                os.setDataFechamento(dataAgora);
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
                os.setDataFechamento(null);
            }

            if (os.getValorInicial() != null) {
                stmt.setBigDecimal(4, os.getValorInicial());
            } else {
                stmt.setBigDecimal(4, BigDecimal.ZERO);
            }

            stmt.setLong(5, os.getVeiculoId());
            stmt.setLong(6, os.getId());

            stmt.executeUpdate();
        }
    }


    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM ordens_servico WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public OrdemServico buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM ordens_servico WHERE id = ?";

        ItemServicoDAO itensDAO = new ItemServicoDAO();

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrdemServico os = new OrdemServico();
                    os.setId(rs.getLong("id"));
                    os.setDescricaoProblema(rs.getString("descricao_problema"));
                    os.setStatus(rs.getString("status"));
                    os.setDataAbertura(rs.getTimestamp("data_abertura"));
                    os.setDataFechamento(rs.getTimestamp("data_fechamento"));
                    os.setValorInicial(rs.getBigDecimal("valor_inicial"));
                    os.setValorTotal(rs.getBigDecimal("valor_total"));
                    os.setVeiculoId(rs.getLong("veiculo_id"));
                    os.setItens(itensDAO.listarItensComDetalhes(id));

                    return os;
                }
            }
        }

        return null;
    }

    public void recalcularValorTotal(Long osId) throws SQLException {
        BigDecimal total = calcularValorTotal(osId);

        String sql = "UPDATE ordens_servico SET valor_total = ? WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setBigDecimal(1, total);
            stmt.setLong(2, osId);
            stmt.executeUpdate();
        }
    }

    private BigDecimal buscarValorInicialDaOs(Long osId) throws SQLException {
        String sql = "SELECT valor_inicial FROM ordens_servico WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal valorInicial = rs.getBigDecimal("valor_inicial");

                if (valorInicial != null) {
                    return valorInicial;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal calcularValorTotal(Long osId) throws SQLException {
        Long veiculoId = buscarVeiculoIdDaOs(osId);

        if (veiculoId == null) {
            return BigDecimal.ZERO;
        }

        Veiculo veiculo = veiculoDAO.buscarPorId(veiculoId);

        if (veiculo == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = buscarValorInicialDaOs(osId);

        ItemServicoDAO itemServicoDAO = new ItemServicoDAO();
        List<Long> servicosIds = itemServicoDAO.listarServicosDaOs(osId);

        for (Long servicoId : servicosIds) {
            Servico servico = servicoDAO.buscarPorId(servicoId);

            if (servico != null) {
                total = total.add(
                        servico.calcularValor(veiculo)
                );
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void atualizarValorTotalComItem(Long osId, BigDecimal valorItem, boolean isAdicao) throws SQLException {
        recalcularValorTotal(osId);
    }

    private Long buscarVeiculoIdDaOs(Long osId) throws SQLException {
        String sql = "SELECT veiculo_id FROM ordens_servico WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, osId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("veiculo_id");
            }
        }

        return null;
    }
}