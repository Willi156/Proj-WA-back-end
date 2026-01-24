package com.critiverse.dao;

import java.util.List;

import com.critiverse.model.Recensione;
import com.critiverse.model.RecensioneConContenuto;

public interface RecensioneDao{

    List<Recensione> findByContenutoId(Long contenutoId);

    List<RecensioneConContenuto> findByUtenteIdWithContenuto(Long utenteId);
}
