package com.example.phonecontact;

import java.util.ArrayList;

public class Contact {
    private int id;
    private String name;
    private String lastName;
    private ArrayList<String> phoneNumbers;

    private String address;

    // Constructor memastikan semua parameter diinisialisasi dengan benar
    public Contact(int contactId, String firstName, String lastName, ArrayList<String> phoneNumbers, String address) {
        this.id = contactId;
        this.name = firstName != null ? firstName : "";
        this.lastName = lastName != null ? lastName : "";
        this.phoneNumbers = phoneNumbers != null ? phoneNumbers : new ArrayList<>();

        this.address = address != null ? address : "";
    }




    // Konstruktor kedua jika hanya diberi stateId
    public Contact(int contactId, String firstName, String lastName, ArrayList<String> phoneNumbers, String address, int stateId) {
        this(contactId, firstName, lastName, phoneNumbers, address);
    }

    // Konstruktor ketiga untuk parameter stateId saja
    public Contact(int id, String firstName, String lastName, String address, ArrayList<String> phoneNumbers, int stateId) {
        this(id, firstName, lastName, phoneNumbers, address);
    }

    // Konstruktor untuk inisialisasi default Contact
    public Contact(String firstName, String lastName, String address, int stateId) {
        this(-1, firstName, lastName, new ArrayList<>(), address);
    }

    // Getter untuk ID
    public int getId() {
        return id;
    }

    // Getter untuk nama depan
    public String getName() {
        return name;
    }

    // Getter untuk nama belakang
    public String getLastName() {
        return lastName;
    }

    // Getter untuk daftar nomor telepon
    public ArrayList<String> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);  // Return a defensive copy
    }

    // Getter untuk StateCode

    // Getter untuk alamat
    public String getAddress() {
        return address != null && !address.isEmpty() ? address : "";
    }

    // Setter untuk daftar nomor telepon
    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        this.phoneNumbers = new ArrayList<>(phoneNumbers);
    }

    // Setter untuk stateCode


    // Setter untuk alamat
    public void setAddress(String address) {
        this.address = address;
    }

    // Mengubah daftar nomor telepon menjadi string yang dipisahkan koma (untuk menyimpan dalam database)
    public String getPhoneNumbersAsString() {
        return String.join(",", phoneNumbers);
    }

    // Mengubah string yang dipisahkan koma kembali ke ArrayList
    public void setPhoneNumbersFromString(String phoneNumbersString) {
        String[] numbers = phoneNumbersString.split(",");
        this.phoneNumbers = new ArrayList<>();
        for (String number : numbers) {
            this.phoneNumbers.add(number.trim());  // Modify the instance variable
        }
    }

    // Override toString() untuk mempermudah logging dan debugging
    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                ", address='" + address + '\'' +
                '}';
    }

    // Override equals() dan hashCode() jika diperlukan untuk perbandingan objek
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return id == contact.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
