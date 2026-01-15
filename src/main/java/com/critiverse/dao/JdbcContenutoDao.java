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

import com.critiverse.model.Contenuto;


@Repository
public class JdbcContenutoDao implements ContenutoDao {

    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(JdbcContenutoDao.class);

    public JdbcContenutoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final class ContenutoRowMapper implements RowMapper<Contenuto> {
        @Override
        public Contenuto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Contenuto c = new Contenuto();
            c.setId(rs.getLong("id"));
            c.setTitolo(rs.getString("titolo"));
            c.setDescrizione(rs.getString("descrizione"));
            c.setGenere(rs.getString("genere"));
            c.setLink(rs.getString("link"));
            c.setTipo(rs.getString("tipo"));
            // anno_pubblicazione is stored as integer year
            int anno = rs.getInt("anno_pubblicazione");
            if (!rs.wasNull()) {
                c.setAnnoPubblicazione(anno);
            }
            return c;
        }
    }

    @Override
    public Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, Integer annoPubblicazione) {
        try {
            final String insertSql = "INSERT INTO contenuto (titolo, descrizione, genere, link, tipo, anno_pubblicazione) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

            Long id = jdbc.queryForObject(insertSql, new Object[] { titolo, descrizione, genere, link, tipo, annoPubblicazione }, Long.class);
            if (id == null) {
                log.warn("INSERT returned null id for titolo='{}' tipo='{}'", titolo, tipo);
                return Optional.empty();
            }

            List<Contenuto> list = jdbc.query("SELECT id, titolo, descrizione, genere, link, tipo, anno_pubblicazione FROM contenuto WHERE id = ?", new Object[] { id }, new ContenutoRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error inserting contenuto", ex);
            return Optional.empty();
        }
    }
}
