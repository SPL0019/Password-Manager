package com.samcore.passwordmanager.fragment;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.model.NotesModel;

import java.util.List;
import java.util.Objects;

public class NotesFragmentAdapter extends RecyclerView.Adapter<NotesFragmentAdapter.ViewHolder>{
    private final List<NotesModel> notesModelList;
    private final Context context;

    // Constructor, onCreateViewHolder, onBindViewHolder, getItemCount methods


    public NotesFragmentAdapter(List<NotesModel> notesModelList, Context context) {
        this.notesModelList = notesModelList;
        this.context=context;
    }
    @NonNull
    @Override
    public NotesFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesFragmentAdapter.ViewHolder holder, int position) {
        NotesModel notesModel = notesModelList.get(position);
        holder.title.setText(notesModel.getTitle());
        holder.notes.setText(notesModel.getNotes());
        Glide.with(context)
                .load(R.drawable.icon_notes)
                .placeholder(R.drawable.icon_notes)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v->{
            holder.showPopup(notesModel.getTitle(),notesModel.getNotes());
        });

    }

    @Override
    public int getItemCount() {
        return (notesModelList != null) ? notesModelList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,notes;
        ImageView imageView;
        AppCompatButton closeButton;
        TextInputLayout textInputLayoutUsername;
        TextView headerName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.card_notes_title);
            notes=itemView.findViewById(R.id.card_notes);
            imageView=itemView.findViewById(R.id.card_image);

        }
        public void showPopup(String title, String notes){
            final View popupView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.notes_popup_window, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            closeButton =popupView.findViewById(R.id.closeButton);
            textInputLayoutUsername=popupView.findViewById(R.id.popup_card_notes);
            headerName=popupView.findViewById(R.id.popup_card_title);

            Objects.requireNonNull(textInputLayoutUsername.getEditText()).setText(notes);
            headerName.setText(title);

            closeButton.setOnClickListener(v -> popupWindow.dismiss());


            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        }
    }
}
