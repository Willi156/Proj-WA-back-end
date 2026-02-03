package com.critiverse.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import com.critiverse.model.Piattaforma;

@Repository
public class JdbcPiattaformaDao implements PiattaformaDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcPiattaformaDao.class);

    private static final RowMapper<Piattaforma> PIATTAFORMA_ROW_MAPPER = (rs, rowNum) -> {
        Piattaforma piattaforma = new Piattaforma();
        piattaforma.setId(rs.getLong("id"));
        piattaforma.setNome(rs.getString("nome"));
        piattaforma.setVersione(rs.getLong("versione"));
        return piattaforma;
    };

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

    @Override
    public List<Piattaforma> findAll() {
        try {
            final String sql = "SELECT id, nome, versione FROM piattaforma ORDER BY nome";
            return jdbc.query(sql, PIATTAFORMA_ROW_MAPPER);
        } catch (DataAccessException ex) {
            log.error("Error fetching piattaforme", ex);
            return List.of();
        }
    }
}
