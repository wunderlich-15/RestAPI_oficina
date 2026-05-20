package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.OrdemServico;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDAO {

public void salvar(OrdemServico os) throws SQLException {
    String sql = "INSERT INTO ordens_servico (descricao_problema, status, veiculo_id, valor_total) VALUES (?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, os.getDescricaoProblema());
        stmt.setString(2, os.getStatus());
        stmt.setLong(3, os.getVeiculoId());
        
        if (os.getValorTotal() != null) {
            stmt.setBigDecimal(4, os.getValorTotal());
        } else {
            stmt.setBigDecimal(4, BigDecimal.ZERO);
        }
        
        stmt.executeUpdate();

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                os.setId(generatedKeys.getLong(1));
            }
        }
    }
}
    public List<OrdemServico> listarTodas() throws SQLException {
        String sql = "SELECT * FROM ordens_servico";
        List<OrdemServico> lista = new ArrayList<>();
        ItemServicoDAO itensDAO = new ItemServicoDAO();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long idOs = rs.getLong("id");
                OrdemServico os = new OrdemServico();
                os.setId(idOs);
                os.setDescricaoProblema(rs.getString("descricao_problema"));
                os.setStatus(rs.getString("status"));
                os.setDataAbertura(rs.getTimestamp("data_abertura"));
                os.setDataFechamento(rs.getTimestamp("data_fechamento"));
                os.setValorTotal(rs.getBigDecimal("valor_total"));
                os.setVeiculoId(rs.getLong("veiculo_id"));
                
                os.setItens(itensDAO.listarItensComDetalhes(idOs));
                
                lista.add(os);
            }
        }
        return lista;
    }

    public void atualizar(OrdemServico os) throws SQLException {
        String sql = "UPDATE ordens_servico SET descricao_problema = ?, status = ?, data_fechamento = ?, valor_total = ?, veiculo_id = ? WHERE id = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, os.getDescricaoProblema());
        stmt.setString(2, os.getStatus());


        if ("FINALIZADA".equalsIgnoreCase(os.getStatus())) {
            Timestamp dataAgora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, dataAgora);
            os.setDataFechamento(dataAgora); 
        } else {
            stmt.setNull(3, java.sql.Types.TIMESTAMP);
            os.setDataFechamento(null);
        }

        stmt.setBigDecimal(4, os.getValorTotal());
        stmt.setLong(5, os.getVeiculoId());
        stmt.setLong(6, os.getId());
        
        stmt.executeUpdate();
    }
}

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM ordens_servico WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public OrdemServico buscarPorId(Long id) throws SQLException {
    String sql = "SELECT * FROM ordens_servico WHERE id = ?";
    ItemServicoDAO itensDAO = new ItemServicoDAO();
    
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrdemServico os = new OrdemServico();
                    os.setId(rs.getLong("id"));
                    os.setDescricaoProblema(rs.getString("descricao_problema"));
                    os.setStatus(rs.getString("status"));
                    os.setDataAbertura(rs.getTimestamp("data_abertura"));
                    os.setDataFechamento(rs.getTimestamp("data_fechamento"));
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
        
        String sqlSomaItens = "SELECT SUM(s.valor_base) FROM itens_servico i " +
                            "JOIN servicos s ON i.servico_id = s.id WHERE i.os_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            BigDecimal totalItens = BigDecimal.ZERO;
            try (PreparedStatement stmtSoma = conn.prepareStatement(sqlSomaItens)) {
                stmtSoma.setLong(1, osId);
                try (ResultSet rs = stmtSoma.executeQuery()) {
                    if (rs.next() && rs.getBigDecimal(1) != null) {
                        totalItens = rs.getBigDecimal(1);
                    }
                }
            }
            String sqlUpdate = "UPDATE ordens_servico SET valor_total = valor_total + ? WHERE id = ?";

        }

    }

    public void atualizarValorTotalComItem(Long osId, BigDecimal valorItem, boolean isAdicao) throws SQLException {
    // Se isAdicao for true, soma (+). Se for false (delete), subtrai (-).
        String sql = isAdicao ? 
            "UPDATE ordens_servico SET valor_total = valor_total + ? WHERE id = ?" :
            "UPDATE ordens_servico SET valor_total = valor_total - ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, valorItem);
            stmt.setLong(2, osId);
            stmt.executeUpdate();
        }
    }


}