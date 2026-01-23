package com.critiverse.dao;

import java.util.List;

public interface PreferitiDao {

    List<Long> findContenutoIdsByUtente(Long idUtente);
}
