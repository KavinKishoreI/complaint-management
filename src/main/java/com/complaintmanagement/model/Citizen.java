package com.complaintmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Citizen")
public class Citizen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citizen_id;

    private String citizen_name;
    private String citizen_city;
    private String citizen_pincode;
    private String citizen_phone;
    private String citizen_email;

    public Citizen() {}

    public Citizen(Long citizen_id, String citizen_name, String citizen_city, String citizen_pincode, String citizen_phone, String citizen_email) {
        this.citizen_id = citizen_id;
        this.citizen_name = citizen_name;
        this.citizen_city = citizen_city;
        this.citizen_pincode = citizen_pincode;
        this.citizen_phone = citizen_phone;
        this.citizen_email = citizen_email;
    }

    public Long getCitizen_id() {
        return citizen_id;
    }

    public void setCitizen_id(Long citizen_id) {
        this.citizen_id = citizen_id;
    }

    public String getCitizen_name() {
        return citizen_name;
    }

    public void setCitizen_name(String citizen_name) {
        this.citizen_name = citizen_name;
    }

    public String getCitizen_city() {
        return citizen_city;
    }

    public void setCitizen_city(String citizen_city) {
        this.citizen_city = citizen_city;
    }

    public String getCitizen_pincode() {
        return citizen_pincode;
    }

    public void setCitizen_pincode(String citizen_pincode) {
        this.citizen_pincode = citizen_pincode;
    }

    public String getCitizen_phone() {
        return citizen_phone;
    }

    public void setCitizen_phone(String citizen_phone) {
        this.citizen_phone = citizen_phone;
    }

    public String getCitizen_email() {
        return citizen_email;
    }

    public void setCitizen_email(String citizen_email) {
        this.citizen_email = citizen_email;
    }
}
