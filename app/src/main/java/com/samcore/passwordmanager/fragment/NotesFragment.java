package com.samcore.passwordmanager.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.components.AppSession;
import com.samcore.passwordmanager.model.NotesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NotesFragment extends Fragment implements View.OnClickListener {
    EditText editTextNotes,editTextTitle;
    ImageView addNotesButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AppSession appSession;
    NotesModel notesModel=new NotesModel();
    RecyclerView rv_notes;
    NotesFragmentAdapter notesFragmentAdapter;
    List<NotesModel> notesList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notes, container, false);
        appSession=new AppSession(requireContext());
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Notes");

        findViewById(view);


        return view;
    }

    private void findViewById(View view) {
        editTextNotes=view.findViewById(R.id.add_notes_text);
        editTextTitle=view.findViewById(R.id.add_notes_title_text);
        addNotesButton=view.findViewById(R.id.add_notes_image);
        rv_notes=view.findViewById(R.id.recyclerView_notes);

        addNotesButton.setOnClickListener(this);

        // Set up RecyclerView

        fetchData(appSession.getKeyUid());
        notesFragmentAdapter=new NotesFragmentAdapter(notesList,getContext());
        rv_notes.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_notes.setAdapter(notesFragmentAdapter);
    }

    private void fetchData(String path) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Notes");

        databaseReference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notesList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    NotesModel notesModel1 = childSnapshot.getValue(NotesModel.class);
                    notesList.add(notesModel1);
                }
                // Update your RecyclerView adapter with the new data
                // For example, passwordAdapter.setPasswords(passwordList);
                // passwordAdapter.notifyDataSetChanged();
                notesFragmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==addNotesButton){
            if (editTextTitle.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Please enter Title ", Toast.LENGTH_SHORT).show();
            else if (editTextNotes.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Please enter notes ", Toast.LENGTH_SHORT).show();
            else {
                notesModel.setNotes(editTextNotes.getText().toString().trim());
                notesModel.setTitle(editTextTitle.getText().toString().trim());
                addNotes();
            }
        }
    }

    private void addNotes() {
        String key=databaseReference.push().getKey();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(appSession.getKeyUid())
                        .child(Objects.requireNonNull(key))
                        .setValue(notesModel);
                Toast.makeText(getActivity(), "Notes added successfully", Toast.LENGTH_SHORT).show();
                editTextNotes.setText("");
                editTextTitle.setText("");
                editTextNotes.clearFocus();
                editTextTitle.clearFocus();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
