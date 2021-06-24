package com.example.theprotector.adapter;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.theprotector.Constants;
import com.example.theprotector.ContactPicker;
import com.example.theprotector.R;
import com.example.theprotector.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Map<String,String> emergencyContacts=new HashMap<>();;
    private Context context;
    private List<Contact> contactList;
    private int total_contacts=0;
    ItemClicked activity;
    public interface ItemClicked{
        void onItemClicked(int index);
    }
    public ContactAdapter(Context context,List<Contact> contactList){
        this.context=context;
        activity=(ItemClicked) context;
        this.contactList=contactList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position % 5 == 0){
            holder.nameDisplayer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ripple_color_one)));
            holder.shortname.setTextColor(ContextCompat.getColor(context,R.color.app_color));
        }else if(position % 5 == 1){
            holder.nameDisplayer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ripple_color_two)));
            holder.shortname.setTextColor(ContextCompat.getColor(context,R.color.app_color));
        }else if(position % 5 == 2){
            holder.nameDisplayer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ripple_color_one)));
            holder.shortname.setTextColor(ContextCompat.getColor(context,R.color.app_color));
        }else if(position % 5 == 3){
            holder.nameDisplayer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ripple_color_two)));
            holder.shortname.setTextColor(ContextCompat.getColor(context,R.color.app_color));
        }else if(position % 5 == 4){
            holder.nameDisplayer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ripple_color_one)));
            holder.shortname.setTextColor(ContextCompat.getColor(context,R.color.app_color));
        }
        String sName;

        Contact contacts=contactList.get(position);
        holder.itemView.setTag(contacts);
        holder.mName.setText(contacts.getName());
        holder.mNumber.setText(contacts.getNumber());
        holder.contact=contacts;
        String name=contacts.getName();
        String[] split=name.split(" ");
        if(split.length>1){
            sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
        }else{
            sName=String.valueOf(split[0].charAt(0));
        }
        holder.shortname.setText(sName);
        holder.checkBox.setChecked(contacts.isSelected());
        holder.checkBox.setTag(position);

    }

    @Override
    public int getItemCount() {
       return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Contact contact;
        private TextView shortname;
        private TextView mName;
        private TextView mNumber;
        private ImageView mImageView;
        private AppCompatCheckBox checkBox;
        private FloatingActionButton nameDisplayer;
        SharedPreferences sharedPreferences;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.contactName);
            mNumber = itemView.findViewById(R.id.contactNumber);
            mImageView = itemView.findViewById(R.id.addContact);
            checkBox=itemView.findViewById(R.id.materialCheckBox);
            // floatingActionButton = itemView.findViewById(R.id.floatingActionButton);
            nameDisplayer=itemView.findViewById(R.id.floatingActionButton4);
            shortname=itemView.findViewById(R.id.shortName);
            checkBox.setOnClickListener(this::onClick);
            sharedPreferences  =context.getSharedPreferences("emergency_contacts",Context.MODE_PRIVATE);
        }

        @Override
        public void onClick(View v) {
            Integer pos=(Integer) checkBox.getTag();
            if(contact.isSelected()){
                total_contacts--;
                emergencyContacts.remove(contact.getName());
                SharedPreferences sharedPreferences = context.getSharedPreferences("emergency_contacts", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(contact.getName()).commit();
                contact.setSelected(false);
            }else{
                total_contacts++;
                Log.d("check", "onClick: "+contact.getName());
                contact.setSelected(true);
                emergencyContacts.put(contact.getName().trim().toString(), contact.getNumber().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (String s : emergencyContacts.keySet()) {
                    editor.putString(s, emergencyContacts.get(s));
                }
                //   editor.putString("contactInfo",new Gson().toJson(emergencyContacts));
                editor.commit();

            }
            activity.onItemClicked(total_contacts);
        }

    }
}
