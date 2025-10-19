package com.complaintmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authority_id;
    private String authority_branch;
    private String authority_designation;
    private String authority_email;

    public Authority() {}

    public Authority(String authority_branch, String authority_designation, String authority_email) {
        this.authority_branch = authority_branch;
        this.authority_designation = authority_designation;
        this.authority_email = authority_email;
    }

    // Getters and setters for all fields
    public Long getAuthority_id() { return authority_id; }
    public void setAuthority_id(Long authority_id) { this.authority_id = authority_id; }
    
    public String getAuthority_branch() { return authority_branch; }
    public void setAuthority_branch(String authority_branch) { this.authority_branch = authority_branch; }
    
    public String getAuthority_designation() { return authority_designation; }
    public void setAuthority_designation(String authority_designation) { this.authority_designation = authority_designation; }
    
    public String getAuthority_email() { return authority_email; }
    public void setAuthority_email(String authority_email) { this.authority_email = authority_email; }
}
