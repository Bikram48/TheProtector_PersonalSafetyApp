package com.example.theprotector.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theprotector.R;
import com.example.theprotector.model.CompanionInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CompanionAdapter extends RecyclerView.Adapter<CompanionAdapter.CompanionHolder> {
    List<CompanionInfo> companionInfoList;
    Context context;
    public CompanionAdapter(Context context,List<CompanionInfo> companionInfoList){
        this.context=context;
        this.companionInfoList=companionInfoList;
    }
    @NonNull
    @Override
    public CompanionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.companion_items,parent,false);
        CompanionHolder companionHolder=new CompanionHolder(view);
        return companionHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompanionHolder holder, int position) {

        holder.name.setText(companionInfoList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(companionInfoList.size()>0)
            return companionInfoList.size();
        return 0;
    }

    class CompanionHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView name;
        public CompanionHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.add_companion);
            name=itemView.findViewById(R.id.companion_nickname);

        }

    }
}
