package com.example.theprotector;

 public class ContactsInfo {
    private String contactId;
    private String displayName;
    private String phoneNumber;
     private boolean selected;

     public boolean isSelected() {
         return selected;
     }

     public void setSelected(boolean selected) {
         this.selected = selected;
     }

     public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}