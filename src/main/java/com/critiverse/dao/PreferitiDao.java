package com.critiverse.dao;

import java.util.List;
import com.critiverse.model.ContenutoSummary;

public interface PreferitiDao {

    List<Long> findContenutoIdsByUtente(Long idUtente);

    List<ContenutoSummary> findContenutiByUtente(Long idUtente);

    boolean addPreferito(Long idUtente, Long idContenuto);

    boolean deletePreferito(Long idUtente, Long idContenuto);
    
    Long findPreferitoId(Long idUtente, Long idContenuto);

    boolean deletePreferitoById(Long preferitoId);
}
