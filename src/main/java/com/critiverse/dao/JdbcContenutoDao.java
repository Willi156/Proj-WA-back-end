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
    public Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, Integer annoPubblicazione, String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni) {
        try {
            final String insertSql = "INSERT INTO contenuto (titolo, descrizione, genere, link, tipo, anno_pubblicazione) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

            Long id = jdbc.queryForObject(insertSql, new Object[] { titolo, descrizione, genere, link, tipo, annoPubblicazione }, Long.class);
            if (id == null) {
                log.warn("INSERT returned null id for titolo='{}' tipo='{}'", titolo, tipo);
                return Optional.empty();
            }

            // Insert into specific table based on tipo
            if (tipo != null) {
                insertIntoSpecificTable(id, tipo, casaProduzione, casaEditrice, inCorso, stagioni);
            }

            List<Contenuto> list = jdbc.query("SELECT id, titolo, descrizione, genere, link, tipo, anno_pubblicazione FROM contenuto WHERE id = ?", new Object[] { id }, new ContenutoRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error inserting contenuto", ex);
            return Optional.empty();
        }
    }

    private void insertIntoSpecificTable(Long idContenuto, String tipo, String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni) {
        try {
            String tableName = null;
            String insertSpecificSql = null;
            // Normalize string inputs and log values for debugging
            String normCasaProduzione = normalizeString(casaProduzione);
            String normCasaEditrice = normalizeString(casaEditrice);
            log.debug("Preparing specific insert: tipo={}, id={}, casaProduzione={}, casaEditrice={}, inCorso={}, stagioni={}",
                    tipo, idContenuto, normCasaProduzione, normCasaEditrice, inCorso, stagioni);

            // Determina la tabella in base al tipo e prepara l'INSERT con colonne specifiche
            if ("film".equalsIgnoreCase(tipo)) {
                tableName = "film";
                insertSpecificSql = "INSERT INTO film (id_contenuto, casa_produzione) VALUES (?, ?)";
                jdbc.update(insertSpecificSql, idContenuto, normCasaProduzione);
            } else if ("gioco".equalsIgnoreCase(tipo)) {
                tableName = "gioco";
                insertSpecificSql = "INSERT INTO gioco (id_contenuto, casa_editrice) VALUES (?, ?)";
                jdbc.update(insertSpecificSql, idContenuto, normCasaEditrice);
            } else if ("serie_tv".equalsIgnoreCase(tipo) || "serietv".equalsIgnoreCase(tipo)) {
                tableName = "serie_tv";
                insertSpecificSql = "INSERT INTO serie_tv (id_contenuto, in_corso, stagioni) VALUES (?, ?, ?)";
                jdbc.update(insertSpecificSql, idContenuto, inCorso, stagioni);
            } else {
                log.warn("Unknown tipo='{}', skipping specific table insertion", tipo);
            }

            if (insertSpecificSql != null) {
                log.info("Inserted into {} table with id_contenuto={}", tableName, idContenuto);
            }
        } catch (DataAccessException ex) {
            log.error("Error inserting into specific table for tipo='{}', id_contenuto={}", tipo, idContenuto, ex);
            // Non rilanciamo l'eccezione per non bloccare l'inserimento principale
        }
    }

    private String normalizeString(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        if ("null".equalsIgnoreCase(trimmed)) return null;
        return s;
    }

    @Override
    public List<Contenuto> findAll() {
        try {
            final String sql = "SELECT id, titolo, descrizione, genere, link, tipo, anno_pubblicazione FROM contenuto ORDER BY id";
            return jdbc.query(sql, new ContenutoRowMapper());
        } catch (DataAccessException ex) {
            log.error("Error fetching all contenuti", ex);
            return List.of();
        }
    }

    
}
