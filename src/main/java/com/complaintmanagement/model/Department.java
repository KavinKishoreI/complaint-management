package com.complaintmanagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dept_id;
    
    @Column(unique = true, nullable = false)
    private String dept_name;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complaint> complaints;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Authority> authorities;
    
    public Department() {}
    
    public Department(String dept_name) {
        this.dept_name = dept_name;
    }
    
    // Getters and setters
    public Long getDept_id() {
        return dept_id;
    }
    
    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id;
    }
    
    public String getDept_name() {
        return dept_name;
    }
    
    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
    
    public List<Complaint> getComplaints() {
        return complaints;
    }
    
    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }
    
    public List<Authority> getAuthorities() {
        return authorities;
    }
    
    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
    
    @Override
    public String toString() {
        return dept_name;
    }
}
