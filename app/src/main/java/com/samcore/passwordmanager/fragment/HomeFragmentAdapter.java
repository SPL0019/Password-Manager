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
import com.samcore.passwordmanager.model.PasswordModel;

import java.util.List;
import java.util.Objects;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder>{
    private final List<PasswordModel> passwordList;
    private final Context context;

    // Constructor, onCreateViewHolder, onBindViewHolder, getItemCount methods


    public HomeFragmentAdapter(List<PasswordModel> passwordList,Context context) {
        this.passwordList = passwordList;
        this.context=context;
    }
    @NonNull
    @Override
    public HomeFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentAdapter.ViewHolder holder, int position) {
        PasswordModel passwordModel = passwordList.get(position);
        holder.name.setText(passwordModel.getName());
        holder.type.setText(passwordModel.getPassword_type());
        holder.email.setText(passwordModel.getUsername());
        Glide.with(context)
                .load(R.drawable.logo)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v->{
            holder.showPopup(passwordModel.getName(),passwordModel.getPassword_type(),passwordModel.getUsername(),passwordModel.getPassword());
        });

    }

    @Override
    public int getItemCount() {
        return (passwordList != null) ? passwordList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,type,email;
        ImageView imageView;
        AppCompatButton closeButton;
        TextInputLayout textInputLayoutUsername,textInputLayoutPassword;
        TextView headerName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.card_password_name);
            type=itemView.findViewById(R.id.card_password_type);
            email=itemView.findViewById(R.id.card_password_email);
            imageView=itemView.findViewById(R.id.card_image);

        }
        public void showPopup(String name, String passwordType, String username, String password){
            final View popupView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.password_popup_window, null);
            final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            closeButton =popupView.findViewById(R.id.closeButton);
            textInputLayoutUsername=popupView.findViewById(R.id.popup_card_username);
            textInputLayoutPassword=popupView.findViewById(R.id.popup_card_password);
            headerName=popupView.findViewById(R.id.popup_card_name);

            Objects.requireNonNull(textInputLayoutUsername.getEditText()).setText(username);
            Objects.requireNonNull(textInputLayoutPassword.getEditText()).setText(password);
            headerName.setText(name);

            closeButton.setOnClickListener(v -> popupWindow.dismiss());


            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        }
    }
}
