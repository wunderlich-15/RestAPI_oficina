package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.Cliente;
import com.oficina.model.Veiculo;
import com.oficina.model.Carro;
import com.oficina.model.Moto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO implements CrudDAO<Veiculo>{

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculos (placa, modelo, marca, ano, tipo, numero_portas, chassi, cilindradas, tipo_moto, cliente_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setInt(4, veiculo.getAno());
            if (veiculo instanceof Carro carro) {

                stmt.setString(5, "CARRO");
                stmt.setInt(6, carro.getNumeroPortas());
                stmt.setString(7, carro.getChassi());

                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.VARCHAR);
            } else {
                Moto moto = (Moto) veiculo;

                stmt.setString(5, "MOTO");

                stmt.setNull(6, Types.INTEGER);
                stmt.setNull(7, Types.VARCHAR);

                stmt.setInt(8, moto.getCilindradas());
                stmt.setString(9, moto.getTipoMoto());
            }
                stmt.setLong(10, veiculo.getClienteId());
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) veiculo.setId(rs.getLong(1));
        }
    }

    public Veiculo buscarPorId(Long id)
        throws SQLException {

        String sql =
                "SELECT * FROM veiculos WHERE id = ?";

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return criarVeiculoDoResultSet(rs);
            }
        }

        return null;
    }
    public List<Veiculo> listarPorCliente(Long clienteId)
            throws SQLException {

        String sql =
                "SELECT * FROM veiculos WHERE cliente_id = ?";

        List<Veiculo> lista = new ArrayList<>();

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, clienteId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                lista.add(criarVeiculoDoResultSet(rs));

            }
        }

        return lista;
    }

    public void atualizar(Veiculo veiculo)
        throws SQLException {

        String sql = """
            UPDATE veiculos
            SET
                placa = ?,
                modelo = ?,
                marca = ?,
                ano = ?,
                tipo = ?,
                numero_portas = ?,
                chassi = ?,
                cilindradas = ?,
                tipo_moto = ?,
                cliente_id = ?
            WHERE id = ?
            """;

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt =
                    conn.prepareStatement(sql)
        ) {

            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setInt(4, veiculo.getAno());

            if (veiculo instanceof Carro carro) {

                stmt.setString(5, "CARRO");
                stmt.setInt(6, carro.getNumeroPortas());
                stmt.setString(7, carro.getChassi());

                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.VARCHAR);

            } else {

                Moto moto = (Moto) veiculo;

                stmt.setString(5, "MOTO");

                stmt.setNull(6, Types.INTEGER);
                stmt.setNull(7, Types.VARCHAR);

                stmt.setInt(8, moto.getCilindradas());
                stmt.setString(9, moto.getTipoMoto());
            }

            stmt.setLong(10, veiculo.getClienteId());

            stmt.setLong(11, veiculo.getId());

            stmt.executeUpdate();
        }
    }

    public List<Veiculo> listarTodos() throws SQLException {

        String sql = "SELECT * FROM veiculos";

        List<Veiculo> lista = new ArrayList<>();

        try (
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(criarVeiculoDoResultSet(rs));

            }
        }

        return lista;
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM veiculos WHERE id = ?";
            
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }  


    private Veiculo criarVeiculoDoResultSet(ResultSet rs) throws SQLException {

        String tipo = rs.getString("tipo");

        Veiculo veiculo;

        if ("CARRO".equalsIgnoreCase(tipo)) {

            Carro carro = new Carro();

            carro.setNumeroPortas(
                    rs.getInt("numero_portas")
            );

            carro.setChassi(
                    rs.getString("chassi")
            );

            veiculo = carro;

        } else {

            Moto moto = new Moto();

            moto.setCilindradas(
                    rs.getInt("cilindradas")
            );

            moto.setTipoMoto(
                    rs.getString("tipo_moto")
            );

            veiculo = moto;
        }

        Long clienteId = rs.getLong("cliente_id");

        veiculo.setId(rs.getLong("id"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setMarca(rs.getString("marca"));
        veiculo.setAno(rs.getInt("ano"));
        veiculo.setClienteId(clienteId);

        Cliente cliente = clienteDAO.buscarPorId(clienteId);
        veiculo.setCliente(cliente);

        return veiculo;
    }
}