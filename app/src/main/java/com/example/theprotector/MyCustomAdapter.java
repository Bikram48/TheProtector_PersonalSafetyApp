package com.example.theprotector;

import android.content.Context;
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

import com.example.theprotector.model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {
    private Map<String,String> emergencyContacts=new HashMap<>();;
    private Context context;
    private List<Contact> contactList;
    private int total_contacts=0;
    MyCustomAdapter.ItemClicked activity;
    public interface ItemClicked{
        void onItemClicked(int index, Contact contact);
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
        Contact contacts=contactList.get(position);
        holder.itemView.setTag(contacts);
        holder.mName.setText(contacts.getName());
        holder.mNumber.setText(contacts.getNumber());
        holder.contact=contacts;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Contact contact;
        private TextView mName;
        private TextView mNumber;
        private ImageView mImageView;
        private FloatingActionButton floatingActionButton;
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;
        int switchNumber = 0;
        SharedPreferences sharedPreferences;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.contactName);
            mNumber = itemView.findViewById(R.id.contactNumber);
            mImageView = itemView.findViewById(R.id.addContact);
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton);
            floatingActionButton.setOnClickListener(this);
            sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        }

        @Override
        public void onClick(View view) {
            if (emergencyContacts.size() > 0) {
                Toast.makeText(context, "You are not allowed to add more than one contact", Toast.LENGTH_SHORT).show();
                mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.avd_minus_to_plus));
                Drawable drawable = mImageView.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
                switchNumber++;
            } else {
                if (switchNumber == 0) {
                    total_contacts++;
                    emergencyContacts.put(contact.getName().trim().toString(), contact.getNumber().toString());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.KEY_CON1,contact.getName()+"/"+contact.getNumber());
                    /*
                    for (String s : emergencyContacts.keySet()) {
                        editor.putString(s, emergencyContacts.get(s));
                    }

                     */
                    //   editor.putString("contactInfo",new Gson().toJson(emergencyContacts));
                    editor.commit();

                    mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.avd_plus_to_minus));
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable instanceof AnimatedVectorDrawableCompat) {
                        avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    } else if (drawable instanceof AnimatedVectorDrawable) {
                        avd2 = (AnimatedVectorDrawable) drawable;
                        avd2.start();
                    }
                    switchNumber++;
                } else {
                    total_contacts--;
                    emergencyContacts.remove(contact.getName());
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove(Constants.KEY_CON1).commit();
                    mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.avd_minus_to_plus));
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable instanceof AnimatedVectorDrawableCompat) {
                        avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    } else if (drawable instanceof AnimatedVectorDrawable) {
                        avd2 = (AnimatedVectorDrawable) drawable;
                        avd2.start();
                    }
                    switchNumber--;
                }
                activity.onItemClicked(total_contacts,contact);
            }
        }

    }
}
