package com.example.theprotector.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.example.theprotector.R;
import com.example.theprotector.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {
    public static Map<String,String> emergencyContacts=new HashMap<>();;
    private Context context;
    private List<Contact> contactList;
    private int total_contacts=0;

    MyCustomAdapter.ItemClicked activity;
    public interface ItemClicked{
        void onItemClicked(int index, Map<String,String > emergencyContacts);
    }
    public MyCustomAdapter(Context context,List<Contact> contactList){
        this.context=context;
        activity=(ItemClicked) context;
        this.contactList=contactList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Contact contact;
        private TextView shortname;
        private TextView mName;
        private TextView mNumber;
        private ImageView mImageView;
        private AppCompatCheckBox checkBox;
        private FloatingActionButton nameDisplayer;
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;
        int switchNumber = 0;
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
          //  floatingActionButton.setOnClickListener(this);
            sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            checkBox.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            Integer pos=(Integer) checkBox.getTag();
            if(contact.isSelected()){
                emergencyContacts.remove(contact.getName());
                sharedPreferences.edit().remove(Constants.KEY_CON1).commit();
                total_contacts--;
                contact.setSelected(false);
            }else{
                total_contacts++;
                Log.d("check", "onClick: "+contact.getName());

                emergencyContacts.put(contact.getName().trim().toString(), contact.getNumber().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(contact.getName(),contact.getNumber());
                editor.commit();
                contact.setSelected(true);
                Log.d("mapcheck", "onClick:"+emergencyContacts);

            }
            activity.onItemClicked(total_contacts,emergencyContacts);
        }

    }
}
