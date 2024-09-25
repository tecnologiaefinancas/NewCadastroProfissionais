package com.tecnologiaefinancas.newcadastroprofissionais.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Professional;

@Database(entities = {Professional.class}, version = 1, exportSchema = false)
public abstract class ProfessionalDatabase extends RoomDatabase {

    public abstract ProfessionalDao getProfessionalDao();

    private static ProfessionalDatabase instance;

    public static ProfessionalDatabase getDatabase(final Context context){

        if (instance == null){

            synchronized (ProfessionalDatabase.class){

                if (instance == null){

                    instance = Room.databaseBuilder(context,
                                                    ProfessionalDatabase.class,
                                                    "professional.db").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }
}
