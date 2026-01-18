package com.critiverse.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.critiverse.model.Recensione;

@Repository
public class JdbcRecensioneDao implements RecensioneDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcRecensioneDao.class);

    public JdbcRecensioneDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final class RecensioneRowMapper implements RowMapper<Recensione> {
        @Override
        public Recensione mapRow(ResultSet rs, int rowNum) throws SQLException {
            Recensione r = new Recensione();
            r.setId(rs.getLong("id"));
            r.setTitolo(rs.getString("titolo"));
            r.setTesto(rs.getString("testo"));
            int voto = rs.getInt("voto");
            if (!rs.wasNull()) r.setVoto(voto);
            java.sql.Timestamp ts = rs.getTimestamp("data");
            if (ts != null) r.setData(new java.util.Date(ts.getTime()));
            // some DBs may return 0 for long if null, so check wasNull
            long idUtente = rs.getLong("id_utente");
            if (!rs.wasNull()) r.setIdUtente(idUtente);
            long idContenuto = rs.getLong("id_contenuto");
            if (!rs.wasNull()) r.setIdContenuto(idContenuto);
            return r;
        }
    }

    @Override
    public List<Recensione> findByContenutoId(Long contenutoId) {
        try {
            final String sql = "SELECT id, titolo, testo, voto, data, id_utente, id_contenuto FROM recensione WHERE id_contenuto = ? ORDER BY data DESC";
            return jdbc.query(sql, new RecensioneRowMapper(), contenutoId);
        } catch (DataAccessException ex) {
            log.error("Error fetching recensioni for contenutoId={}", contenutoId, ex);
            return List.of();
        }
    }
}
