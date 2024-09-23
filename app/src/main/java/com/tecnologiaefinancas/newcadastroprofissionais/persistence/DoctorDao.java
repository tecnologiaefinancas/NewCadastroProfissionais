package com.tecnologiaefinancas.newcadastroprofissionais.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Doctor;

@Dao
public interface DoctorDao {

    @Insert
    long insert(Doctor doctor);

    @Delete
    int delete(Doctor doctor);

    @Update
    int update(Doctor doctor);

    @Query("SELECT * FROM Doctor WHERE id = :id")
    Doctor queryForId(long id);

    @Query("SELECT * FROM Doctor ORDER BY nome ASC")
    List<Doctor> queryAllAscending();

    @Query("SELECT * FROM Doctor ORDER BY nome DESC")
    List<Doctor> queryAllDownward();
}
