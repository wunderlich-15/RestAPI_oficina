package com.oficina.dao;

import com.oficina.config.DatabaseConfig;
import com.oficina.model.Cliente;
import com.oficina.model.Veiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO implements CrudDAO<Veiculo>{

    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculos (placa, modelo, marca, ano, cliente_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setInt(4, veiculo.getAno());
            stmt.setLong(5, veiculo.getClienteId()); 
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) veiculo.setId(rs.getLong(1));
        }
    }

    public List<Veiculo> listarPorCliente(Long clienteId) throws SQLException {
        String sql = "SELECT * FROM veiculos WHERE cliente_id = ?";
        List<Veiculo> lista = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Veiculo(
                    rs.getLong("id"),
                    rs.getString("placa"),
                    rs.getString("modelo"),
                    rs.getString("marca"),
                    rs.getInt("ano"),
                    rs.getLong("cliente_id")
                ));
            }
        }
        return lista;
    }

    public void atualizar(Veiculo veiculo) throws SQLException {
        String sql = "UPDATE veiculos SET placa = ?, modelo = ?, marca = ?, ano = ?, cliente_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setInt(4, veiculo.getAno());
            stmt.setLong(5, veiculo.getClienteId()); 
            stmt.setLong(6, veiculo.getId());
            
            stmt.executeUpdate();
        }
    }

        public List<Veiculo> listarTodos() throws SQLException {
        String sql = "SELECT * FROM veiculos";
        List<Veiculo> veiculos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setId(rs.getLong("id"));
                v.setPlaca(rs.getString("placa"));
                v.setModelo(rs.getString("modelo"));
                v.setMarca(rs.getString("marca"));
                v.setAno(rs.getInt("ano"));
                v.setClienteId(rs.getLong("cliente_id"));
                veiculos.add(v);
            }
        }
        return veiculos;
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM veiculos WHERE id = ?";
            
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }   
}