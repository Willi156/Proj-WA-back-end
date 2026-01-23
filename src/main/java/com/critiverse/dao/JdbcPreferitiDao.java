package com.critiverse.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPreferitiDao implements PreferitiDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcPreferitiDao.class);

    public JdbcPreferitiDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Long> findContenutoIdsByUtente(Long idUtente) {
        try {
            final String sql = "SELECT id_contenuto FROM preferiti WHERE id_utente = ?";
            return jdbc.queryForList(sql, Long.class, idUtente);
        } catch (DataAccessException ex) {
            log.error("Error fetching preferiti for id_utente={}", idUtente, ex);
            return List.of();
        }
    }
}
