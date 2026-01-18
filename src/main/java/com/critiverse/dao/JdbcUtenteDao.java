package com.critiverse.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.critiverse.model.Utente;

@Repository
public class JdbcUtenteDao implements UtenteDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcUtenteDao.class);

    public JdbcUtenteDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final class UtenteRowMapper implements RowMapper<Utente> {
        @Override
        public Utente mapRow(ResultSet rs, int rowNum) throws SQLException {
            Utente u = new Utente();
            u.setId(rs.getLong("id"));
            u.setNome(rs.getString("nome"));
            u.setEmail(rs.getString("email"));
            u.setCognome(rs.getString("cognome"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setRuolo(rs.getString("ruolo"));
            return u;
        }
    }

    @Override
    public Optional<Utente> findFirst() {
        try {
            List<Utente> list = jdbc.query("SELECT id, nome, email, cognome, username, password, ruolo FROM utente ORDER BY id ASC LIMIT 1", new UtenteRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error querying first utente", ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Utente> findByUsernameAndPassword(String username, String password) {
        try {
            List<Utente> list = jdbc.query(
                    "SELECT id, nome, cognome, email, username, ruolo, password FROM utente WHERE username = ? AND password = ? LIMIT 1",
                    new Object[]{username, password}, new UtenteRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error querying utente by username/password", ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> checkUsernameExists(String username) {
        try {
            String sql = "SELECT EXISTS(SELECT 1 FROM utente WHERE username = ? LIMIT 1) AS user_exists";
            Boolean exists = jdbc.queryForObject(sql, new Object[]{username}, Boolean.class);
            return Optional.ofNullable(exists);
        } catch (DataAccessException ex) {
            log.error("Error checking if username exists", ex);
            return Optional.empty();
        }
    }
}
