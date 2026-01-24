package com.critiverse.dao;

import java.util.List;

public interface PreferitiDao {

    List<Long> findContenutoIdsByUtente(Long idUtente);

    boolean addPreferito(Long idUtente, Integer idContenuto);

    boolean deletePreferito(Long idUtente, Integer idContenuto);
}
