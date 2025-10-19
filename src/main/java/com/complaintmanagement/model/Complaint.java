package com.complaintmanagement.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Complaint")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaint_id;
    
    private String complaint_type;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
    
    private String complaint_title;
    private String complaint_descp;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus resolve_status;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date complaint_date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;
    
    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Response> responses;

    public Complaint() {}

    public Complaint(String complaint_type, Department department, String complaint_title, String complaint_descp, ComplaintStatus resolve_status, Date complaint_date, Citizen citizen) {
        this.complaint_type = complaint_type;
        this.department = department;
        this.complaint_title = complaint_title;
        this.complaint_descp = complaint_descp;
        this.resolve_status = resolve_status;
        this.complaint_date = complaint_date;
        this.citizen = citizen;
    }

    // Getters and setters for all fields
    public Long getComplaint_id() { 
        return complaint_id; 
    }
    
    public void setComplaint_id(Long complaint_id) { 
        this.complaint_id = complaint_id; 
    }
    
    public String getComplaint_title() { 
        return complaint_title; 
    }
    
    public void setComplaint_title(String complaint_title) { 
        this.complaint_title = complaint_title; 
    }
    
    public String getComplaint_type() { 
        return complaint_type; 
    }
    
    public void setComplaint_type(String complaint_type) { 
        this.complaint_type = complaint_type; 
    }
    
    public Department getDepartment() { 
        return department; 
    }
    
    public void setDepartment(Department department) { 
        this.department = department; 
    }
    
    public String getComplaint_descp() { 
        return complaint_descp; 
    }
    
    public void setComplaint_descp(String complaint_descp) { 
        this.complaint_descp = complaint_descp; 
    }
    
    public ComplaintStatus getResolve_status() { 
        return resolve_status; 
    }
    
    public void setResolve_status(ComplaintStatus resolve_status) { 
        this.resolve_status = resolve_status; 
    }
    
    public Date getComplaint_date() { 
        return complaint_date; 
    }
    
    public void setComplaint_date(Date complaint_date) { 
        this.complaint_date = complaint_date; 
    }
    
    public Citizen getCitizen() { 
        return citizen; 
    }
    
    public void setCitizen(Citizen citizen) { 
        this.citizen = citizen; 
    }
    
    public List<Response> getResponses() { 
        return responses; 
    }
    
    public void setResponses(List<Response> responses) { 
        this.responses = responses; 
    }
}
