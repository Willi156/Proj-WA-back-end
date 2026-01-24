package com.critiverse.dao;

import java.util.List;

public interface PreferitiDao {

    List<Long> findContenutoIdsByUtente(Long idUtente);

    boolean addPreferito(Long idUtente, Long idContenuto);

    boolean deletePreferito(Long idUtente, Long idContenuto);
    
    Long findPreferitoId(Long idUtente, Long idContenuto);

    boolean deletePreferitoById(Long preferitoId);
}
