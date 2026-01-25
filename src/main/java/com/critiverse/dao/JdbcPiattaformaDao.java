package com.critiverse.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPiattaformaDao implements PiattaformaDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcPiattaformaDao.class);

    public JdbcPiattaformaDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<String> findAllNames() {
        try {
            final String sql = "SELECT nome FROM piattaforma ORDER BY nome";
            return jdbc.queryForList(sql, String.class);
        } catch (DataAccessException ex) {
            log.error("Error fetching piattaforma names", ex);
            return List.of();
        }
    }
}
