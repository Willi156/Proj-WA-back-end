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
            c.setImageLink(rs.getString("img_link"));
            // anno_pubblicazione is stored as integer year
            int anno = rs.getInt("anno_pubblicazione");
            if (!rs.wasNull()) {
                c.setAnnoPubblicazione(anno);
            }
            // avg_voto may be returned by queries that join recensioni
            double avg = rs.getDouble("avg_voto");
            if (!rs.wasNull()) {
                c.setMediaVoti(avg);
            }
            return c;
        }
    }

    @Override
    public Optional<Contenuto> newContenuto(String titolo, String descrizione, String genere, String link, String tipo, Integer annoPubblicazione, String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni, String imageLink) {
        try {
            final String insertSql = "INSERT INTO contenuto (titolo, descrizione, genere, link, tipo, anno_pubblicazione, img_link) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

            Long id = jdbc.queryForObject(insertSql, new Object[] { titolo, descrizione, genere, link, tipo, annoPubblicazione, imageLink }, Long.class);
            if (id == null) {
                log.warn("INSERT returned null id for titolo='{}' tipo='{}'", titolo, tipo);
                return Optional.empty();
            }

            // Insert into specific table based on tipo
            if (tipo != null) {
                insertIntoSpecificTable(id, tipo, casaProduzione, casaEditrice, inCorso, stagioni);
            }

                final String selectInserted = "SELECT c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link, AVG(r.voto) AS avg_voto "
                    + "FROM contenuto c LEFT JOIN recensione r ON c.id = r.id_contenuto "
                    + "WHERE c.id = ? GROUP BY c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link";
                List<Contenuto> list = jdbc.query(selectInserted, new Object[] { id }, new ContenutoRowMapper());
            return list.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error inserting contenuto", ex);
            return Optional.empty();
        }
    }

    // @Override
    // public Optional<Contenuto> updateContenuto(Long id, String titolo, String descrizione, String genere, String link, String tipo, Integer annoPubblicazione,
    //         String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni, String imageLink) {
    //     try {
    //         String currentTipo;
    //         try {
    //             currentTipo = jdbc.queryForObject("SELECT tipo FROM contenuto WHERE id = ?", String.class, id);
    //         } catch (DataAccessException notFound) {
    //             log.info("Contenuto id={} not found for update", id);
    //             return Optional.empty();
    //         }

    //         final String updateSql = "UPDATE contenuto SET titolo = ?, descrizione = ?, genere = ?, link = ?, tipo = ?, anno_pubblicazione = ?, img_link = ? WHERE id = ?";
    //         int updated = jdbc.update(updateSql, titolo, descrizione, genere, link, tipo, annoPubblicazione, imageLink, id);
    //         if (updated == 0) {
    //             return Optional.empty();
    //         }

    //         updateSpecificTable(id, currentTipo, tipo, casaProduzione, casaEditrice, inCorso, stagioni);
    //         return fetchById(id);
    //     } catch (DataAccessException ex) {
    //         log.error("Error updating contenuto id={} titolo={} tipo={}", id, titolo, tipo, ex);
    //         return Optional.empty();
    //     }
    // }

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

    // private void updateSpecificTable(Long idContenuto, String currentTipo, String newTipo, String casaProduzione, String casaEditrice, Boolean inCorso, Integer stagioni) {
    //     try {
    //         String normNewTipo = normalizeTipo(newTipo);
    //         String normOldTipo = normalizeTipo(currentTipo);

    //         if (normOldTipo != null && !normOldTipo.equals(normNewTipo)) {
    //             jdbc.update("DELETE FROM film WHERE id_contenuto = ?", idContenuto);
    //             jdbc.update("DELETE FROM gioco WHERE id_contenuto = ?", idContenuto);
    //             jdbc.update("DELETE FROM serie_tv WHERE id_contenuto = ?", idContenuto);
    //         }

    //         if (normNewTipo == null) {
    //             return;
    //         }

    //         String normCasaProduzione = normalizeString(casaProduzione);
    //         String normCasaEditrice = normalizeString(casaEditrice);

    //         switch (normNewTipo) {
    //             case "film" ->  {
    //                 int rows = jdbc.update("UPDATE film SET casa_produzione = ? WHERE id_contenuto = ?", normCasaProduzione, idContenuto);
    //                 if (rows == 0) {
    //                     jdbc.update("INSERT INTO film (id_contenuto, casa_produzione) VALUES (?, ?)", idContenuto, normCasaProduzione);
    //                 }
    //             }
    //             case "gioco" ->  {
    //                 int rows = jdbc.update("UPDATE gioco SET casa_editrice = ? WHERE id_contenuto = ?", normCasaEditrice, idContenuto);
    //                 if (rows == 0) {
    //                     jdbc.update("INSERT INTO gioco (id_contenuto, casa_editrice) VALUES (?, ?)", idContenuto, normCasaEditrice);
    //                 }
    //             }
    //             case "serie_tv", "serietv" -> {
    //                 int rows = jdbc.update("UPDATE serie_tv SET in_corso = ?, stagioni = ? WHERE id_contenuto = ?", inCorso, stagioni, idContenuto);
    //                 if (rows == 0) {
    //                     jdbc.update("INSERT INTO serie_tv (id_contenuto, in_corso, stagioni) VALUES (?, ?, ?)", idContenuto, inCorso, stagioni);
    //                 }
    //             }
    //             default -> log.warn("Unknown tipo='{}' while updating specific table for id_contenuto={}", newTipo, idContenuto);
    //         }
    //     } catch (DataAccessException ex) {
    //         log.error("Error updating specific table for id_contenuto={} tipo={}", idContenuto, newTipo, ex);
    //     }
    // }

    private String normalizeString(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        if ("null".equalsIgnoreCase(trimmed)) return null;
        return s;
    }

    // private String normalizeTipo(String tipo) {
    //     if (tipo == null) {
    //         return null;
    //     }
    //     String trimmed = tipo.trim().toLowerCase();
    //     if (trimmed.isEmpty()) {
    //         return null;
    //     }
    //     return trimmed;
    // }

    // private Optional<Contenuto> fetchById(Long idContenuto) {
    //     final String sql = "SELECT c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link, AVG(r.voto) AS avg_voto "
    //         + "FROM contenuto c LEFT JOIN recensione r ON c.id = r.id_contenuto "
    //         + "WHERE c.id = ? GROUP BY c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link";
    //     List<Contenuto> list = jdbc.query(sql, new ContenutoRowMapper(), idContenuto);
    //     return list.stream().findFirst();
    // }

    // @Override
    // public boolean deleteContenuto(Long id) {
    //     try {
    //         jdbc.update("DELETE FROM film WHERE id_contenuto = ?", id);
    //         jdbc.update("DELETE FROM gioco WHERE id_contenuto = ?", id);
    //         jdbc.update("DELETE FROM serie_tv WHERE id_contenuto = ?", id);
    //         jdbc.update("DELETE FROM preferiti WHERE id_contenuto = ?", id);
    //         jdbc.update("DELETE FROM recensione WHERE id_contenuto = ?", id);

    //         int affected = jdbc.update("DELETE FROM contenuto WHERE id = ?", id);
    //         return affected > 0;
    //     } catch (DataAccessException ex) {
    //         log.error("Error deleting contenuto id={}", id, ex);
    //         return false;
    //     }
    // }

    @Override
    public List<Contenuto> findAll() {
        try {
                final String sql = "SELECT c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link, AVG(r.voto) AS avg_voto "
                    + "FROM contenuto c LEFT JOIN recensione r ON c.id = r.id_contenuto "
                    + "GROUP BY c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link "
                    + "ORDER BY c.id";
            return jdbc.query(sql, new ContenutoRowMapper());
        } catch (DataAccessException ex) {
            log.error("Error fetching all contenuti", ex);
            return List.of();
        }
    }

    @Override
    public List<Contenuto> findByTipo(String tipo) {
        try {
                final String sql = "SELECT c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link, AVG(r.voto) AS avg_voto "
                    + "FROM contenuto c LEFT JOIN recensione r ON c.id = r.id_contenuto "
                    + "WHERE c.tipo = ? "
                    + "GROUP BY c.id, c.titolo, c.descrizione, c.genere, c.link, c.tipo, c.anno_pubblicazione, c.img_link "
                    + "ORDER BY c.id";
            return jdbc.query(sql, new ContenutoRowMapper(), tipo);
        } catch (DataAccessException ex) {
            log.error("Error fetching all contenuti", ex);
            return List.of();
        }
    }

    @Override
    public List<String> findDistinctGeneriByTipo(String tipo) {
        try {
            final String sql = "SELECT DISTINCT c.genere FROM contenuto c WHERE c.tipo = ? AND c.genere IS NOT NULL ORDER BY c.genere";
            return jdbc.query(sql, (rs, rowNum) -> rs.getString("genere"), tipo);
        } catch (DataAccessException ex) {
            log.error("Error fetching distinct generi for tipo={}", tipo, ex);
            return List.of();
        }
    }
}
