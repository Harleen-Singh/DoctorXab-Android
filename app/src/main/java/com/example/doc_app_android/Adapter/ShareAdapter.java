package com.example.doc_app_android.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doc_app_android.R;
import com.example.doc_app_android.data_model.DocData;
import com.example.doc_app_android.databinding.ShareSingleRowBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private ArrayList<DocData> doctorList = new ArrayList<>();
    private Context context;


    public ShareAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<DocData> doctorList){
        this.doctorList = doctorList;
    }

    @NonNull
    @NotNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        ShareSingleRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.share_single_row, parent, false);
        return new ShareViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ShareAdapter.ShareViewHolder holder, int position) {

        if(!TextUtils.isEmpty(doctorList.get(position).getName()) && !TextUtils.isEmpty(doctorList.get(position).getImage()) && !TextUtils.isEmpty(doctorList.get(position).getAge())) {
            holder.binding.shareDocName.setText(doctorList.get(position).getName());
            Picasso.get().load(doctorList.get(position).getImage())
                    .placeholder(R.drawable.doctor_profile_image)
                    .into(holder.binding.shareProfileImage);
            holder.binding.shareAge.setText(doctorList.get(position).getAge());
        }

    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class ShareViewHolder extends RecyclerView.ViewHolder{
        private ShareSingleRowBinding binding;


        public ShareViewHolder(@NonNull @NotNull ShareSingleRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }


}
