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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.critiverse.model.Utente;

@Repository
public class JdbcUtenteDao implements UtenteDao {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(JdbcUtenteDao.class);

    public JdbcUtenteDao(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
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
            u.setImmagineProfilo(rs.getString("immagine_profilo"));
            return u;
        }
    }


    @Override
    public Optional<Utente> findByUsernameAndPassword(String username, String password) {
        try {
            String sql = "SELECT id, nome, cognome, email, username, password, ruolo, immagine_profilo FROM utente WHERE username = ? LIMIT 1";
            List<Utente> list = jdbc.query(sql, new Object[]{username}, new UtenteRowMapper());
            return list.stream().filter(u -> {
                String hashed = u.getPassword();
                if (hashed == null) return false;
                return passwordEncoder.matches(password, hashed);
            }).findFirst();
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

    @Override
    public Optional<Utente> newUtente(String nome, String cognome, String email, String username, String password) {
        try {
            String hashedPassword = passwordEncoder.encode(password);
            String insertSql = "INSERT INTO utente (nome, cognome, email, username, password, ruolo) VALUES (?, ?, ?, ?, ?, 'USER')";
            int rowsAffected = jdbc.update(insertSql, nome, cognome, email, username, hashedPassword);
            if (rowsAffected > 0) {
                String querySql = "SELECT id, nome, cognome, email, username, password, ruolo, immagine_profilo FROM utente WHERE username = ? LIMIT 1";
                List<Utente> list = jdbc.query(querySql, new Object[]{username}, new UtenteRowMapper());
                return list.stream().findFirst();
            } else {
                return Optional.empty();
            }
        } catch (DataAccessException ex) {
            log.error("Error creating new utente", ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Utente> findById(Long id) {
        try {
            String sql = "SELECT id, nome, cognome, email, username, password, ruolo, immagine_profilo FROM utente WHERE id = ? LIMIT 1";
            List<Utente> list = jdbc.query(sql, new Object[]{id}, new UtenteRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error querying utente by id {}", id, ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Utente> updateUtenteInfo(Long id, String nome, String cognome, String email, String immagineProfilo) {
        try {
            String sql = "UPDATE utente SET nome = ?, cognome = ?, email = ?, immagine_profilo = ? WHERE id = ?";
            int rows = jdbc.update(sql, nome, cognome, email, immagineProfilo, id);
            if (rows > 0) {
                return findById(id);
            }
            return Optional.empty();
        } catch (DataAccessException ex) {
            log.error("Error updating utente with id {}", id, ex);
            return Optional.empty();
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        try {
            String hashedPassword = passwordEncoder.encode(newPassword);
            String sql = "UPDATE utente SET password = ? WHERE id = ?";
            int rows = jdbc.update(sql, hashedPassword, id);
            return rows > 0;
        } catch (DataAccessException ex) {
            log.error("Error updating password for utente with id {}", id, ex);
            return false;
        }
    }

    @Override
    public Optional<Boolean> verifyPassword(Long id, String password) {
        try {
            Optional<Utente> maybe = findById(id);
            if (maybe.isEmpty()) {
                return Optional.empty();
            }
            Utente u = maybe.get();
            String hashed = u.getPassword();
            if (hashed == null) {
                return Optional.of(Boolean.FALSE);
            }
            return Optional.of(passwordEncoder.matches(password, hashed));
        } catch (DataAccessException ex) {
            log.error("Error verifying password for utente with id {}", id, ex);
            return Optional.empty();
        }
    }
}
