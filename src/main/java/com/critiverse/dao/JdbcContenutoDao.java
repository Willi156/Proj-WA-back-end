package com.critiverse.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
            c.setAnnoPubblicazione(rs.getString("anno_pubblicazione"));
            return c;
        }
    }

    @Override
    public Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, String annoPubblicazione) {
        try {
           
            final String sql = "INSERT INTO contenuto (titolo, descrizione, genere, link, tipo, anno_pubblicazione) VALUES (?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update((java.sql.Connection con) -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, titolo);
                ps.setString(2, descrizione);
                ps.setString(3, genere);
                ps.setString(4, link);
                ps.setString(5, tipo);
                ps.setString(6, annoPubblicazione);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) {
                return Optional.empty();
            }

            Long id = key.longValue();
            List<Contenuto> list = jdbc.query("SELECT id, titolo, descrizione, genere, link, tipo, anno_pubblicazione FROM contenuto WHERE id = ?", new Object[] { id }, new ContenutoRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error inserting contenuto", ex);
            return Optional.empty();
        }
    }
}
