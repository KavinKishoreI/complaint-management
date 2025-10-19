package com.complaintmanagement.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Response")
public class Response {
    @EmbeddedId
    private ResponseId id;
    private String response;
    private Date response_date;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("complaint_id")
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authority_id")
    @JoinColumn(name = "authority_id")
    private Authority authority;
    public Response() {}
    public Response(ResponseId id, String response, Date response_date, Complaint complaint, Authority authority) {
        this.id = id;
        this.response = response;
        this.response_date = response_date;
        this.complaint = complaint;
        this.authority = authority;
    }
    // Getters and setters
    public ResponseId getId() { return id; }
    public void setId(ResponseId id) { this.id = id; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public Date getResponse_date() { return response_date; }
    public void setResponse_date(Date response_date) { this.response_date = response_date; }
    
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
    
    public Authority getAuthority() { return authority; }
    public void setAuthority(Authority authority) { this.authority = authority; }
}
