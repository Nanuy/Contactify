package com.example.phonecontact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity {

    private EditText editTextName, editTextLastName, editTextPhone, editTextAddress;

    private Button buttonSave;
    private DBHelper dbHelper;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);

        buttonSave = findViewById(R.id.buttonSave);
        dbHelper = new DBHelper(this);

        // Get the contact's ID passed via Intent
        contactId = getIntent().getIntExtra("contact_id", -1);

        // Load the contact's details
        loadContactDetails(contactId);

        // Handle the save button click event
        buttonSave.setOnClickListener(v -> {
            // Get input data
            String firstName = editTextName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String address = editTextAddress.getText().toString();

            // Get selected state code ID from the spinner


            // Validation check
            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(EditContactActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!isValidPhoneNumber(phone)) {
                Toast.makeText(EditContactActivity.this, "Phone number must be at least 7 digits long and contain only numbers", Toast.LENGTH_SHORT).show();
            } else {
                // Update contact in the database
                ArrayList<String> phoneNumbers = new ArrayList<>();
                phoneNumbers.add(phone);  // Add phone number

                boolean isUpdated = dbHelper.updateContact(contactId, firstName, lastName, phoneNumbers, address);
                if (isUpdated) {
//                    Toast.makeText(EditContactActivity.this, "Contact Updated Successfully", Toast.LENGTH_SHORT).show();
//                    setResult(RESULT_OK);  // Notify MainActivity to refresh
//                    finish();
                    Intent intent = new Intent(EditContactActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditContactActivity.this, "Error updating contact", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadContactDetails(int contactId) {
        // Fetch contact details from the database
        DBHelper.Contact contact = dbHelper.getContactById(contactId);
        if (contact != null) {
            editTextName.setText(contact.getFirstName());
            editTextLastName.setText(contact.getLastName());
            editTextPhone.setText(contact.getPhoneNumbers().get(0));  // Assuming the first phone number
            editTextAddress.setText(contact.getAddress());

            // Optionally, set spinner for the state code
            // This assumes the state code is present in the stateCode table

        }
    }

    // Helper method to validate phone number
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("[0-9]{7,}");  // Must be at least 7 digits and only numbers
    }
}
