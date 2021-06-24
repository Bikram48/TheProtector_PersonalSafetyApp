package com.example.theprotector.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.theprotector.R;
import com.example.theprotector.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder> {
    private Map<String,String> emergencyContacts=new HashMap<>();;
    private Context context;
    private List<Contact> contactList;
    public EmergencyContactAdapter(Context context,List<Contact> contactList){
        this.context=context;
        this.contactList=contactList;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_contact_item,parent,false);
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
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Contact contact;

        //private ArrayList<Contact> emergencyContacts;
        private TextView shortname;
        private TextView mName;
        private TextView mNumber;
        private ImageView mImageView;
        private FloatingActionButton floatingActionButton;
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;
        private FloatingActionButton nameDisplayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName=itemView.findViewById(R.id.contactName);
            mNumber=itemView.findViewById(R.id.contactNumber);
            mImageView=itemView.findViewById(R.id.addContact);
            floatingActionButton=itemView.findViewById(R.id.floatingActionButton);
            nameDisplayer=itemView.findViewById(R.id.floatingActionButton4);
            shortname=itemView.findViewById(R.id.textView14);
            floatingActionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showAlertDialog();
        }

        public void showAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you really want to remove this number?");
            builder.setTitle("THE PROTECTOR");
            builder.setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences=context.getSharedPreferences("emergency_contacts",Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove(contact.getName()).commit();
                    contactList.remove(contact);
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),contactList.size());
                    Toast.makeText(context, contact.getNumber()+" has been removed from your emergency number list",
                            Toast.LENGTH_LONG).show();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
