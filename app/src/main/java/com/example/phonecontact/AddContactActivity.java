package com.example.phonecontact;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {


    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize views

        dbHelper = new DBHelper(this);




        // Button save
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> {
            // Get input data
            String firstName = ((EditText) findViewById(R.id.editTextName)).getText().toString();
            String lastName = ((EditText) findViewById(R.id.editTextLastName)).getText().toString();
            String phone = ((EditText) findViewById(R.id.editTextPhone)).getText().toString();
            String address = ((EditText) findViewById(R.id.editTextAddress)).getText().toString();
            if (firstName.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "First Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (lastName.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (phone.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (!isValidPhoneNumber(phone)) {
                Toast.makeText(AddContactActivity.this, "Phone number must be at least 7 digits long and contain only numbers", Toast.LENGTH_SHORT).show();
            } else if (address.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Prepare the list of phone numbers (can be expanded to handle multiple phones)
                ArrayList<String> phoneNumbers = new ArrayList<>();
                phoneNumbers.add(phone);  // Add phone number

                // Add contact to the database
                boolean isAdded = dbHelper.addContact(firstName, lastName, phoneNumbers, address);
                if (isAdded) {
                    // Successfully added the contact, notify MainActivity to refresh
                    setResult(RESULT_OK);  // Notify that the contact was added
                    finish();  // Close this activity and go back to MainActivity
                    Toast.makeText(AddContactActivity.this, "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddContactActivity.this, "Error saving contact", Toast.LENGTH_SHORT).show();
                }

                // Optionally, clear the input fields after saving
                clearFields();
            }

        });
    }

    // Helper method to validate phone number
    private boolean isValidPhoneNumber(String phone) {
        // Check if the phone number contains only digits and has at least 7 digits
        return phone.matches("^\\d{7,}$");
    }

    // Helper method to clear input fields after saving
    private void clearFields() {
        ((EditText) findViewById(R.id.editTextName)).setText("");
        ((EditText) findViewById(R.id.editTextLastName)).setText("");
        ((EditText) findViewById(R.id.editTextPhone)).setText("");
        ((EditText) findViewById(R.id.editTextAddress)).setText("");
    }
}
