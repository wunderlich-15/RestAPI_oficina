package com.oficina.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T> {
    void salvar(T entidade) throws SQLException;
    List<T> listarTodos() throws SQLException;
    void deletar(Long id) throws SQLException;
}