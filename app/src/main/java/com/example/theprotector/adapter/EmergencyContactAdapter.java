package com.example.theprotector.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        Contact contacts=contactList.get(position);
        holder.mName.setText(contacts.getName());
        holder.mNumber.setText(contacts.getNumber());
        holder.contact=contacts;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Contact contact;

        //private ArrayList<Contact> emergencyContacts;
        private TextView mName;
        private TextView mNumber;
        private ImageView mImageView;
        private FloatingActionButton floatingActionButton;
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName=itemView.findViewById(R.id.contactName);
            mNumber=itemView.findViewById(R.id.contactNumber);
            mImageView=itemView.findViewById(R.id.addContact);
            floatingActionButton=itemView.findViewById(R.id.floatingActionButton);
            floatingActionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showAlertDialog();
        }

        public void showAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("\"Do you really want to remove this number?\"");
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
