package com.codingstuff.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private NoteAdapter noteAdapter;

    private NoteViewModel noteViewModel;
    private static final int ADD_NOTE_REQUEST_CODE = 1;
    private static final int EDIT_NOTE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.floatingActionButton);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , AddNoteActivity.class);
                startActivityForResult(intent , ADD_NOTE_REQUEST_CODE);
            }
        });

        noteViewModel = new ViewModelProvider(this ,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(NoteViewModel.class);


        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
               if (direction == ItemTouchHelper.RIGHT){
                   noteViewModel.delete(noteAdapter.getNote(viewHolder.getAdapterPosition()));
                   Toast.makeText(MainActivity.this, "Note Deleted Successfully !!", Toast.LENGTH_SHORT).show();
               }else{
                   Intent intent = new Intent(MainActivity.this , AddNoteActivity.class);
                   intent.putExtra(AddNoteActivity.EXTRA_ID , noteAdapter.getNote(viewHolder.getAdapterPosition()).getId());
                   intent.putExtra(AddNoteActivity.EXTRA_TITLE , noteAdapter.getNote(viewHolder.getAdapterPosition()).getTitle());
                   intent.putExtra(AddNoteActivity.EXTRA_DESC , noteAdapter.getNote(viewHolder.getAdapterPosition()).getDescription());
                   intent.putExtra(AddNoteActivity.EXTRA_PRIORITY , noteAdapter.getNote(viewHolder.getAdapterPosition()).getPriority());

                   startActivityForResult(intent , EDIT_NOTE_REQUEST_CODE );

               }
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(AddNoteActivity.EXTRA_DESC);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY , 1);

            Note note = new Note(title , desc , priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note Saved !!", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddNoteActivity.EXTRA_ID , -1);
            if (id == -1){
                Toast.makeText(this, "Note Cant be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(AddNoteActivity.EXTRA_DESC);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY , 1);

            Note note = new Note(title ,desc  , priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note Updated !!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Failed!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_notes){
            noteViewModel.deleteAll();
            Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}