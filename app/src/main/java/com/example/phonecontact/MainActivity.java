package com.example.phonecontact;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactList;
    private FloatingActionButton fabAddContact;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and helper class
        recyclerView = findViewById(R.id.recyclerViewContacts);
        fabAddContact = findViewById(R.id.fabAddContact);
        dbHelper = new DBHelper(this);

        // Initialize the list of contacts and the adapter
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList, dbHelper, this);

        // Set the RecyclerView with a LinearLayoutManager and the ContactAdapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        // Load contacts from the database
        loadContactsFromDatabase();  // Ensure the data is loaded properly

        // Handle the click event of the FloatingActionButton to add a new contact
        fabAddContact.setOnClickListener(v -> openAddContactActivity());
    }

    // Method to open AddContactActivity
    private void openAddContactActivity() {
        Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
        startActivityForResult(intent, 1);  // Request code 1 to handle the result
    }

    // Method to load contacts from the database
    private void loadContactsFromDatabase() {
        try {
            contactList.clear();  // Clear the existing list to avoid duplication
            List<DBHelper.Contact> dbContacts = dbHelper.getAllContacts();  // Get data from the database

            // Ensure contacts is not null and not empty
            if (dbContacts != null && !dbContacts.isEmpty()) {
                for (DBHelper.Contact dbContact : dbContacts) {
                    // Create a new Contact object (MainActivity's Contact) and add it to the list
                    Contact contact = new Contact(dbContact.getId(), dbContact.getFirstName(),
                            dbContact.getLastName(), (ArrayList<String>) dbContact.getPhoneNumbers(),
                            dbContact.getAddress(), dbContact.getStateId());
                    contactList.add(contact);
                }
                contactAdapter.notifyDataSetChanged();  // Notify adapter to update RecyclerView
            } else {
                // Optionally show a message if no contacts are available
                Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error loading contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Override onActivityResult to handle the result when AddContactActivity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches and the result is OK
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload contacts after adding or editing a contact
            loadContactsFromDatabase();
        }
    }


    // Method to delete a contact from the database and update UI
    public void deleteContact(int position) {
        Contact contactToDelete = contactList.get(position);

        // Delete from DB
        boolean isDeleted = dbHelper.deleteContact(contactToDelete.getId());
        if (isDeleted) {
            // Remove contact from list and notify adapter
            contactList.remove(position);
            contactAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting contact", Toast.LENGTH_SHORT).show();
        }
    }

    // You can add other methods here to handle editing a contact if needed.
}
