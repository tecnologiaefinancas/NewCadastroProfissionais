package com.tecnologiaefinancas.newcadastroprofissionais.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Doctor;

@Database(entities = {Doctor.class}, version = 1, exportSchema = false)
public abstract class DoctorDatabase extends RoomDatabase {

    public abstract DoctorDao getDoctorDao();

    private static DoctorDatabase instance;

    public static DoctorDatabase getDatabase(final Context context){

        if (instance == null){

            synchronized (DoctorDatabase.class){

                if (instance == null){

                    instance = Room.databaseBuilder(context,
                                                    DoctorDatabase.class,
                                                    "doctor.db").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }
}
