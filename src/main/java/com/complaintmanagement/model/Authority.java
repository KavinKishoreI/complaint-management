package com.complaintmanagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authority_id;
    
    @Column(unique = true, nullable = false)
    private String authority_username;
    
    @Column(nullable = false)
    private String authority_password;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
    
    private String authority_designation;
    
    @Column(unique = true)
    private String authority_email;
    
    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Response> responses;

    public Authority() {}

    public Authority(String authority_username, String authority_password, Department department, String authority_designation, String authority_email) {
        this.authority_username = authority_username;
        this.authority_password = authority_password;
        this.department = department;
        this.authority_designation = authority_designation;
        this.authority_email = authority_email;
    }

    // Getters and setters for all fields
    public Long getAuthority_id() { 
        return authority_id; 
    }
    
    public void setAuthority_id(Long authority_id) { 
        this.authority_id = authority_id; 
    }
    
    public String getAuthority_username() { 
        return authority_username; 
    }
    
    public void setAuthority_username(String authority_username) { 
        this.authority_username = authority_username; 
    }
    
    public String getAuthority_password() { 
        return authority_password; 
    }
    
    public void setAuthority_password(String authority_password) { 
        this.authority_password = authority_password; 
    }
    
    public Department getDepartment() { 
        return department; 
    }
    
    public void setDepartment(Department department) { 
        this.department = department; 
    }
    
    public String getAuthority_designation() { 
        return authority_designation; 
    }
    
    public void setAuthority_designation(String authority_designation) { 
        this.authority_designation = authority_designation; 
    }
    
    public String getAuthority_email() { 
        return authority_email; 
    }
    
    public void setAuthority_email(String authority_email) { 
        this.authority_email = authority_email; 
    }
    
    public List<Response> getResponses() { 
        return responses; 
    }
    
    public void setResponses(List<Response> responses) { 
        this.responses = responses; 
    }
}
