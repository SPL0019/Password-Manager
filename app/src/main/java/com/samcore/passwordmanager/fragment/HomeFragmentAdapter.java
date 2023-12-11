package com.samcore.passwordmanager.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.model.PasswordModel;

import java.util.List;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder>{
    private List<PasswordModel> passwordList;
    private Context context;

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
        holder.name.setText(passwordModel.getName());
        Glide.with(context)
                .load(R.drawable.logo)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(holder.imageView);

        Log.e("TAG", "onBindViewHolder: passwordModel.getName()"+ passwordModel.getName());

    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "getItemCount: "+passwordList.size());
        return (passwordList != null) ? passwordList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,type,email;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.card_password_name);
            type=itemView.findViewById(R.id.card_password_type);
            email=itemView.findViewById(R.id.card_password_email);
            imageView=itemView.findViewById(R.id.card_image);

        }
    }
}
