package com.critiverse.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.critiverse.model.Recensione;
import com.critiverse.model.RecensioneConContenuto;

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
            // read username from joined utente table if present
            String username;
            String immagineProfilo;
            try {
                username = rs.getString("username");
                immagineProfilo = rs.getString("immagine_profilo");
            } catch (SQLException e) {
                // joined columns may not be present in some queries
                username = null;
                immagineProfilo = null;
            }
            r.setUsername(username);
            r.setImmagineProfilo(immagineProfilo);
            return r;
        }
    }

    private static final class RecensioneConContenutoRowMapper implements RowMapper<RecensioneConContenuto> {
        @Override
        public RecensioneConContenuto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Recensione recensione = new Recensione();
            recensione.setId(rs.getLong("r_id"));
            recensione.setTitolo(rs.getString("r_titolo"));
            recensione.setTesto(rs.getString("r_testo"));
            int voto = rs.getInt("r_voto");
            if (!rs.wasNull()) recensione.setVoto(voto);
            java.sql.Timestamp ts = rs.getTimestamp("r_data");
            if (ts != null) recensione.setData(new java.util.Date(ts.getTime()));
            long idUtente = rs.getLong("r_id_utente");
            if (!rs.wasNull()) recensione.setIdUtente(idUtente);
            long idContenuto = rs.getLong("r_id_contenuto");
            if (!rs.wasNull()) recensione.setIdContenuto(idContenuto);
            recensione.setUsername(rs.getString("r_username"));
            recensione.setImmagineProfilo(rs.getString("r_immagine_profilo"));

            Contenuto contenuto = new Contenuto();
            contenuto.setId(rs.getLong("c_id"));
            contenuto.setTitolo(rs.getString("c_titolo"));
            contenuto.setDescrizione(rs.getString("c_descrizione"));
            contenuto.setGenere(rs.getString("c_genere"));
            contenuto.setLink(rs.getString("c_link"));
            contenuto.setTipo(rs.getString("c_tipo"));
            int anno = rs.getInt("c_anno_pubblicazione");
            if (!rs.wasNull()) contenuto.setAnnoPubblicazione(anno);
            contenuto.setImageLink(rs.getString("c_img_link"));

            RecensioneConContenuto result = new RecensioneConContenuto();
            result.setRecensione(recensione);
            result.setContenuto(contenuto);
            return result;
        }
    }

    @Override
    public List<Recensione> findByContenutoId(Long contenutoId) {
        try {
                final String sql = "SELECT r.id, r.titolo, r.testo, r.voto, r.data, r.id_utente, r.id_contenuto, u.username "
                    + ", u.immagine_profilo "
                    + "FROM recensione r LEFT JOIN utente u ON r.id_utente = u.id "
                    + "WHERE r.id_contenuto = ? ORDER BY r.data DESC";
            return jdbc.query(sql, new RecensioneRowMapper(), contenutoId);
        } catch (DataAccessException ex) {
            log.error("Error fetching recensioni for contenutoId={}", contenutoId, ex);
            return List.of();
        }
    }

    @Override
    public List<RecensioneConContenuto> findByUtenteIdWithContenuto(Long utenteId) {
        try {
            final String sql = "SELECT "
                    + "r.id AS r_id, r.titolo AS r_titolo, r.testo AS r_testo, r.voto AS r_voto, r.data AS r_data, r.id_utente AS r_id_utente, r.id_contenuto AS r_id_contenuto, u.username AS r_username, "
                    + "u.immagine_profilo AS r_immagine_profilo, "
                    + "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.genere AS c_genere, c.link AS c_link, c.tipo AS c_tipo, c.anno_pubblicazione AS c_anno_pubblicazione, c.img_link AS c_img_link "
                    + "FROM recensione r "
                    + "LEFT JOIN utente u ON r.id_utente = u.id "
                    + "JOIN contenuto c ON r.id_contenuto = c.id "
                    + "WHERE r.id_utente = ? "
                    + "ORDER BY r.data DESC";
            return jdbc.query(sql, new RecensioneConContenutoRowMapper(), utenteId);
        } catch (DataAccessException ex) {
            log.error("Error fetching recensioni for utenteId={}", utenteId, ex);
            return List.of();
        }
    }

    @Override
    public boolean deleteById(Long recensioneId) {
        try {
            final String sql = "DELETE FROM recensione WHERE id = ?";
            int affected = jdbc.update(sql, recensioneId);
            return affected > 0;
        } catch (DataAccessException ex) {
            log.error("Error deleting recensione with id={}", recensioneId, ex);
            return false;
        }
    }

    @Override
    public Optional<Recensione> createRecensione(String titolo, String testo, Integer voto, java.util.Date data, Long idUtente, Long idContenuto) {
        try {
            final String sql = "INSERT INTO recensione (titolo, testo, voto, data, id_utente, id_contenuto) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                var ps = con.prepareStatement(sql, new String[] { "id" });
                ps.setString(1, titolo);
                ps.setString(2, testo);
                if (voto != null) {
                    ps.setInt(3, voto);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                if (data != null) {
                    ps.setTimestamp(4, new Timestamp(data.getTime()));
                } else {
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                }
                ps.setLong(5, idUtente);
                ps.setLong(6, idContenuto);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) {
                return Optional.empty();
            }

            Recensione created = new Recensione();
            created.setId(key.longValue());
            created.setTitolo(titolo);
            created.setTesto(testo);
            created.setVoto(voto);
            created.setData(data != null ? data : new java.util.Date());
            created.setIdUtente(idUtente);
            created.setIdContenuto(idContenuto);
            return Optional.of(created);
        } catch (DataAccessException ex) {
            log.error("Error creating recensione for idUtente={} idContenuto={}", idUtente, idContenuto, ex);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateRecensione(Long idRecensione, String titolo, String testo, Integer voto, java.util.Date data) {
        try {
            final String sql = "UPDATE recensione SET titolo = ?, testo = ?, voto = ?, data = ? WHERE id = ?";
            int affected = jdbc.update(sql, ps -> {
                ps.setString(1, titolo);
                ps.setString(2, testo);
                if (voto != null) {
                    ps.setInt(3, voto);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                if (data != null) {
                    ps.setTimestamp(4, new Timestamp(data.getTime()));
                } else {
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                }
                ps.setLong(5, idRecensione);
            });
            return affected > 0;
        } catch (DataAccessException ex) {
            log.error("Error updating recensione with id={}", idRecensione, ex);
            return false;
        }
    }

    @Override
    public Optional<Recensione> findMostRecent() {
        try {
            final String sql = "SELECT r.id, r.titolo, r.testo, r.voto, r.data, r.id_utente, r.id_contenuto, u.username "
                    + ", u.immagine_profilo "
                    + "FROM recensione r LEFT JOIN utente u ON r.id_utente = u.id "
                    + "ORDER BY r.data DESC LIMIT 1";
            List<Recensione> results = jdbc.query(sql, new RecensioneRowMapper());
            if (results.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(results.get(0));
        } catch (DataAccessException ex) {
            log.error("Error fetching most recent recensione", ex);
            return Optional.empty();
        }
    }
}
