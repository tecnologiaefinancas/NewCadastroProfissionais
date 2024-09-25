package com.tecnologiaefinancas.newcadastroprofissionais.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Professional;

@Dao
public interface ProfessionalDao {

    @Insert
    long insert(Professional professional);

    @Delete
    int delete(Professional professional);

    @Update
    int update(Professional professional);

    @Query("SELECT * FROM Professional WHERE id = :id")
    Professional queryForId(long id);

    @Query("SELECT * FROM Professional ORDER BY nome ASC")
    List<Professional> queryAllAscending();

    @Query("SELECT * FROM Professional ORDER BY nome DESC")
    List<Professional> queryAllDownward();
}
