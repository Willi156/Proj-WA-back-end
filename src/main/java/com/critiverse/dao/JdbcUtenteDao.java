package com.critiverse.dao;

import com.critiverse.model.Utente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUtenteDao implements UtenteDao {

    private final JdbcTemplate jdbc;

    public JdbcUtenteDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final class UtenteRowMapper implements RowMapper<Utente> {
        @Override
        public Utente mapRow(ResultSet rs, int rowNum) throws SQLException {
            Utente u = new Utente();
            u.setId(rs.getLong("id"));
            try { u.setNome(rs.getString("nome")); } catch (SQLException ignored) {}
            try { u.setEmail(rs.getString("email")); } catch (SQLException ignored) {}
            return u;
        }
    }

    @Override
    public Optional<Utente> findFirst() {
        List<Utente> list = jdbc.query("SELECT * FROM utente ORDER BY id ASC LIMIT 1", new UtenteRowMapper());
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }
}
