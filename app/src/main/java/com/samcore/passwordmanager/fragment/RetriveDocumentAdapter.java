package com.samcore.passwordmanager.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.samcore.passwordmanager.R;
import com.samcore.passwordmanager.model.UploadDocumentModel;

import java.util.List;

public class RetriveDocumentAdapter extends RecyclerView.Adapter<RetriveDocumentAdapter.ViewHolder>{
    private final List<UploadDocumentModel> uploadDocumentModelList;
    private final Context context;

    public RetriveDocumentAdapter(List<UploadDocumentModel> uploadDocumentModelList, Context context) {
        this.uploadDocumentModelList = uploadDocumentModelList;
        this.context=context;
    }
    @NonNull
    @Override
    public RetriveDocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetriveDocumentAdapter.ViewHolder holder, int position) {
       UploadDocumentModel uploadDocumentModel=uploadDocumentModelList.get(position);
        Log.e("TAG", "onBindViewHolder: uploadDocumentModel.getName()"+ uploadDocumentModel.getImageUrl());
        holder.textViewName.setText(uploadDocumentModel.getName());

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(uploadDocumentModel.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.icon_pdf) // Placeholder image
//                        .error(R.drawable.icon_error_image) // Error image in case of loading failure
                )
                .into(holder.imageView);
        holder.downloadImage.setOnClickListener(v->{
           /* Intent intent=new Intent(holder.itemView.getContext(), WebViewActivity.class);
            intent.putExtra("url",uploadDocumentModel.getImageUrl());
            holder.itemView.getContext().startActivity(intent);*/
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uploadDocumentModel.getImageUrl()));
            holder.itemView.getContext().startActivity(browserIntent);
        });

    }

    @Override
    public int getItemCount() {
        return (uploadDocumentModelList != null) ? uploadDocumentModelList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView,downloadImage;
        FirebaseStorage storage = FirebaseStorage.getInstance();



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView);
            downloadImage=itemView.findViewById(R.id.downloadImage);

        }
    }
}
