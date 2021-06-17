package com.codingstuff.notesapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDAO {

    @Insert
    public void insert(Note note);

    @Update
    public void update(Note note);

    @Delete
    public void delete(Note note);

    @Query("DELETE FROM note_table")
    public void deleteAll();

    @Query("SELECT * FROM note_table  ORDER BY priority ASC")
    LiveData<List<Note>> getAllNotes();
}
