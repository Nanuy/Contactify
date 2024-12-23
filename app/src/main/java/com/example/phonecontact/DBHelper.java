package com.example.phonecontact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1

    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_PHONE_NUMBER = "phone_number";
    private static final String TABLE_STATE_CODE = "state_code";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LNAME = "Lname";
    private static final String COLUMN_STATE_ID = "state_id";
    private static final String COLUMN_PHONE = "phone_number";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_STATE_CODE = "code";
    private static final String COLUMN_STATE_NAME = "state_name";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Create contacts table
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LNAME + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_STATE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_STATE_ID + ") REFERENCES " + TABLE_STATE_CODE + "(" + COLUMN_ID + ")" +
                ");";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // Create phone number table
        String CREATE_PHONE_NUMBER_TABLE = "CREATE TABLE " + TABLE_PHONE_NUMBER + " (" +
                "id_number INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contact_id INTEGER, " +
                COLUMN_PHONE + " TEXT, " +
                "FOREIGN KEY(contact_id) REFERENCES " + TABLE_CONTACTS + "(" + COLUMN_ID + ")" +
                ");";
        db.execSQL(CREATE_PHONE_NUMBER_TABLE);

        // Create state code table
        String CREATE_STATE_CODES_TABLE = "CREATE TABLE " + TABLE_STATE_CODE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STATE_CODE + " TEXT, " +
                COLUMN_STATE_NAME + " TEXT" +
                ");";
        db.execSQL(CREATE_STATE_CODES_TABLE);

        // Prepopulate state codes
        prepopulateStateCodes(db);
    }

    private void prepopulateStateCodes(SQLiteDatabase db) {
        String[] codes = {
                "INSERT INTO " + TABLE_STATE_CODE + " (code, state_name) VALUES ('+62', 'Indonesia')",
                "INSERT INTO " + TABLE_STATE_CODE + " (code, state_name) VALUES ('+1', 'United States')",
                "INSERT INTO " + TABLE_STATE_CODE + " (code, state_name) VALUES ('+91', 'India')",
                "INSERT INTO " + TABLE_STATE_CODE + " (code, state_name) VALUES ('+44', 'United Kingdom')",
                "INSERT INTO " + TABLE_STATE_CODE + " (code, state_name) VALUES ('+81', 'Japan')"
        };

        for (String code : codes) {
            db.execSQL(code);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE_NUMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE_CODE);
        onCreate(db);
    }

    // Add a new contact to the database
    // Add a new contact to the database
    public boolean addContact(String firstName, String lastName, ArrayList<String> phoneNumbers, String address) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();  // Start a transaction

            // Insert the contact data
            ContentValues contactValues = new ContentValues();
            contactValues.put(COLUMN_NAME, firstName);
            contactValues.put(COLUMN_LNAME, lastName);
            contactValues.put(COLUMN_ADDRESS, address);


            long contactId = db.insert(TABLE_CONTACTS, null, contactValues);
            if (contactId == -1) {
                return false;  // Failed to insert the contact
            }

            // Insert phone numbers for the contact
            for (String phoneNumber : phoneNumbers) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(COLUMN_PHONE, phoneNumber);
                phoneValues.put("contact_id", contactId);  // Associate the phone number with the contact

                long phoneId = db.insert(TABLE_PHONE_NUMBER, null, phoneValues);
                if (phoneId == -1) {
                    return false;  // Failed to insert a phone number
                }
            }

            db.setTransactionSuccessful();  // Mark the transaction as successful
            return true;  // Successfully inserted contact and phone numbers

        } catch (Exception e) {
            Log.e("DBHelper", "Error while inserting contact", e);
            return false;  // Return false if there was an error
        } finally {
            if (db != null) {
                db.endTransaction();  // End the transaction
            }
        }
    }


    // Retrieve all contacts
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Ambil data dari query
                int contactId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LNAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                int stateId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE_ID)); // Periksa kolom state_id

                Contact contact = new Contact();
                contact.setId(contactId);
                contact.setFirstName(firstName);
                contact.setLastName(lastName);
                contact.setAddress(address);
                contact.setStateId(stateId); // Set ID State

                // Ambil nomor telepon berdasarkan id kontak
                List<String> phoneNumbers = getPhoneNumbersForContact(contact.getId(), db);
                contact.setPhoneNumbers(phoneNumbers);

                contacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return contacts;
    }


    // Retrieve phone numbers for a contact
    private List<String> getPhoneNumbersForContact(int contactId, SQLiteDatabase db) {
        List<String> phoneNumbers = new ArrayList<>();
        String query = "SELECT " + COLUMN_PHONE + " FROM " + TABLE_PHONE_NUMBER + " WHERE contact_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(contactId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                phoneNumbers.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return phoneNumbers;
    }

    // Delete a contact by ID
    public boolean deleteContact(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Hapus nomor telepon terkait terlebih dahulu (untuk memastikan foreign key tidak menghalangi)
            db.delete(TABLE_PHONE_NUMBER, "contact_id = ?", new String[]{String.valueOf(contactId)});

            // Hapus kontak dari tabel utama
            int rowsDeleted = db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(contactId)});

            return rowsDeleted > 0; // Jika lebih dari 0 baris dihapus, berarti sukses
        } catch (Exception e) {
            Log.e("DBHelper", "Error deleting contact: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }



    // Retrieve all state codes
    public List<StateCode> getAllStateCodes() {
        List<StateCode> stateCodes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STATE_CODE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String stateCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE_CODE));
                String stateName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE_NAME));
                stateCodes.add(new StateCode(id, stateName, stateCode));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return stateCodes;
    }

    // Update an existing contact in the database
    public boolean updateContact(int contactId, String firstName, String lastName, ArrayList<String> phoneNumbers, String address) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();  // Start a transaction

            // Update the contact data
            ContentValues contactValues = new ContentValues();
            contactValues.put(COLUMN_NAME, firstName);
            contactValues.put(COLUMN_LNAME, lastName);
            contactValues.put(COLUMN_ADDRESS, address);


            int rowsUpdated = db.update(TABLE_CONTACTS, contactValues, COLUMN_ID + " = ?", new String[]{String.valueOf(contactId)});

            if (rowsUpdated == 0) {
                return false;  // Contact not found or failed to update
            }

            // Delete old phone numbers for this contact
            db.delete(TABLE_PHONE_NUMBER, "contact_id = ?", new String[]{String.valueOf(contactId)});

            // Insert updated phone numbers
            for (String phoneNumber : phoneNumbers) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(COLUMN_PHONE, phoneNumber);
                phoneValues.put("contact_id", contactId);  // Associate the phone number with the contact

                long phoneId = db.insert(TABLE_PHONE_NUMBER, null, phoneValues);
                if (phoneId == -1) {
                    return false;  // Failed to insert a phone number
                }
            }

            db.setTransactionSuccessful();  // Mark the transaction as successful
            return true;  // Successfully updated the contact and phone numbers

        } catch (Exception e) {
            Log.e("DBHelper", "Error while updating contact", e);
            return false;  // Return false if there was an error
        } finally {
            if (db != null) {
                db.endTransaction();  // End the transaction
            }
        }
    }



    public StateCode getStateCodeForContact(int stateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STATE_CODE + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(stateId)});

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String stateCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE_CODE));
            String stateName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE_NAME));
            cursor.close();
            return new StateCode(id, stateName, stateCode);
        }

        db.close();
        return null; // Return null if no state code is found
    }

    public Contact getContactById(int contactId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = null;

        // Query to get the contact details along with the state code
        String query = "SELECT c.id, c.name, c.Lname, c.address, c.state_id, " +
                "s.code, s.state_name FROM " + TABLE_CONTACTS + " c " +
                "JOIN " + TABLE_STATE_CODE + " s ON c.state_id = s.id " +
                "WHERE c.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(contactId)});

        if (cursor != null && cursor.moveToFirst()) {
            contact = new Contact();

            // Ensure the column indices are valid before retrieving data
            int idColumnIndex = cursor.getColumnIndex(COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int lastNameColumnIndex = cursor.getColumnIndex(COLUMN_LNAME);
            int addressColumnIndex = cursor.getColumnIndex(COLUMN_ADDRESS);
            int stateCodeColumnIndex = cursor.getColumnIndex(COLUMN_STATE_CODE);
            int stateNameColumnIndex = cursor.getColumnIndex(COLUMN_STATE_NAME);

            // Check for valid column indices (must be >= 0)
            if (idColumnIndex >= 0) contact.setId(cursor.getInt(idColumnIndex));
            if (nameColumnIndex >= 0) contact.setFirstName(cursor.getString(nameColumnIndex));
            if (lastNameColumnIndex >= 0) contact.setLastName(cursor.getString(lastNameColumnIndex));
            if (addressColumnIndex >= 0) contact.setAddress(cursor.getString(addressColumnIndex));

            // Get the state code and name if they are valid columns
            if (stateCodeColumnIndex >= 0 && stateNameColumnIndex >= 0) {
                StateCode stateCode = new StateCode();
                stateCode.setStateCode(cursor.getString(stateCodeColumnIndex));
                stateCode.setStateName(cursor.getString(stateNameColumnIndex));
                contact.setStateCode(stateCode);
            } else {
                Log.e("DBHelper", "State code or state name column is missing or invalid.");
            }

            // Get phone numbers for this contact
            List<String> phoneNumbers = getPhoneNumbersForContact(contact.getId());
            contact.setPhoneNumbers(phoneNumbers);

            cursor.close();
        } else {
            Log.e("DBHelper", "Contact with ID " + contactId + " not found or query error.");
        }

        db.close();
        return contact;
    }

    // Method to fetch phone numbers for a contact
    private List<String> getPhoneNumbersForContact(int contactId) {
        List<String> phoneNumbers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get phone numbers for the specific contact
        String query = "SELECT " + COLUMN_PHONE + " FROM " + TABLE_PHONE_NUMBER +
                " WHERE contact_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(contactId)});

        if (cursor != null) {
            // Get column index for the phone number
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_PHONE);

            // Check if the column index is valid (greater than or equal to 0)
            if (phoneColumnIndex >= 0) {
                while (cursor.moveToNext()) {
                    String phoneNumber = cursor.getString(phoneColumnIndex);
                    phoneNumbers.add(phoneNumber);
                }
            } else {
                Log.e("DBHelper", "Column " + COLUMN_PHONE + " not found in query result.");
            }

            cursor.close();
        } else {
            Log.e("DBHelper", "No cursor data returned for contact ID: " + contactId);
        }

        db.close();
        return phoneNumbers;
    }






    // StateCode class
    public static class StateCode {
        private int id;

        private int idState;
        private String stateName;
        private String stateCode;

        public StateCode(int id, String stateName, String stateCode) {
            this.id = id;
            this.stateName = stateName;
            this.stateCode = stateCode;
        }

        public StateCode() {
            this.id = id;
            this.stateName = stateName;
            this.stateCode = stateCode;
        }


        public int getId() {
            return id;
        }

        public String getStateName() {
            return stateName;
        }

        public String getStateCode() {
            return stateCode;
        }

        public int getIdState() {

            return idState;
        }

        public void setStateCode(String string) {
        }

        public void setStateName(String string) {
        }
    }

    // Contact class
    public static class Contact {
        private int id;
        private String firstName;
        private String lastName;
        private String address;
        private int stateId;
        private List<String> phoneNumbers;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getStateId() {
            return stateId;
        }

        public void setStateId(int stateId) {
            this.stateId = stateId;
        }

        public List<String> getPhoneNumbers() {
            return phoneNumbers;
        }

        public void setPhoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        public void setStateCode(StateCode stateCode) {
        }

        public void setName(String string) {
        }
    }
}
