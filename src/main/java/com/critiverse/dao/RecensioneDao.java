package com.critiverse.dao;

import java.util.List;

import com.critiverse.model.Recensione;

public interface RecensioneDao{

    List<Recensione> findByContenutoId(Long contenutoId);
}
