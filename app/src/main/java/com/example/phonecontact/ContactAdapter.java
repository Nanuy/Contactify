package com.example.phonecontact;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> contactList;
    private DBHelper dbHelper;
    private OnContactDeleteListener deleteListener;

    public interface OnContactDeleteListener {
        void onContactDeleted(Contact contact, int position);
        void onContactAdded(Contact contact); // For adding new contacts
    }

    public void setOnContactDeleteListener(OnContactDeleteListener listener) {
        this.deleteListener = listener;
    }

    public ContactAdapter(ArrayList<Contact> contactList, DBHelper dbHelper, MainActivity mainActivity) {
        this.contactList = contactList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // Set contact name
        holder.contactName.setText(contact.getName() + " " + contact.getLastName());

        // Format and set phone numbers
        if (contact.getPhoneNumbers() != null && !contact.getPhoneNumbers().isEmpty()) {
            String phoneNumber1 = contact.getPhoneNumbers().get(0);

            holder.contactPhone1.setText(phoneNumber1);

            if (contact.getPhoneNumbers().size() > 1) {
                String phoneNumber2 = contact.getPhoneNumbers().get(1);
                holder.contactPhone2.setText(phoneNumber2);
                holder.contactPhone2.setVisibility(View.VISIBLE);
            } else {
                holder.contactPhone2.setVisibility(View.GONE);
            }
        } else {
            holder.contactPhone1.setText("No Phone Number");
            holder.contactPhone2.setVisibility(View.GONE);
        }

        // Set address if available
        if (contact.getAddress() != null && !contact.getAddress().isEmpty()) {
            holder.contactAddress.setText(contact.getAddress());
            holder.contactAddress.setVisibility(View.VISIBLE);
        } else {
            holder.contactAddress.setVisibility(View.GONE);
        }

        // Set state code if available


        // Toggle contact actions visibility when the contact item is clicked
        holder.itemView.setOnClickListener(v -> {
            if (holder.contactActions.getVisibility() == View.VISIBLE) {
                holder.contactActions.setVisibility(View.GONE);
            } else {
                holder.contactActions.setVisibility(View.VISIBLE);
            }
        });

        // Edit button click listener
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditContactActivity.class);
            intent.putExtra("contact_id", contact.getId());  // Pass the contact ID to EditContactActivity
            v.getContext().startActivity(intent);
        });

        // Delete button click listener
        holder.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete this contact?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the contact from the database
                        boolean isDeleted = dbHelper.deleteContact(contact.getId());
                        if (isDeleted) {
                            // Remove the contact from the list
                            contactList.remove(position);
                            // Notify the adapter that the item was removed
                            notifyItemRemoved(position);
                            // Optionally, show a toast for success
                            Toast.makeText(v.getContext(), "Contact deleted", Toast.LENGTH_SHORT).show();

                            // Notify the listener about the deleted contact, if registered
                            if (deleteListener != null) {
                                deleteListener.onContactDeleted(contact, position);
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Error deleting contact", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // Helper method to format phone numbers with state code


    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView contactName, contactPhone1, contactPhone2, contactAddress, contactStateCode;
        Button buttonEdit, buttonDelete;
        LinearLayout contactActions;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.contactName);
            contactPhone1 = itemView.findViewById(R.id.contactPhone1);
            contactPhone2 = itemView.findViewById(R.id.contactPhone2);
            contactAddress = itemView.findViewById(R.id.contactAddress);
            contactStateCode = itemView.findViewById(R.id.contactStateCode);
            contactActions = itemView.findViewById(R.id.contactActions);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
