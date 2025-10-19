package com.complaintmanagement.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ResponseId implements Serializable {
    private Long complaint_id;
    private Long authority_id;
    private Long response_id;
    public ResponseId() {}
    public ResponseId(Long complaint_id, Long authority_id, Long response_id) {
        this.complaint_id = complaint_id;
        this.authority_id = authority_id;
        this.response_id = response_id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseId that = (ResponseId) o;
        return Objects.equals(complaint_id, that.complaint_id)
            && Objects.equals(authority_id, that.authority_id)
            && Objects.equals(response_id, that.response_id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(complaint_id, authority_id, response_id);
    }
    // getters & setters
    public Long getComplaint_id() { return complaint_id; }
    public void setComplaint_id(Long complaint_id) { this.complaint_id = complaint_id; }
    
    public Long getAuthority_id() { return authority_id; }
    public void setAuthority_id(Long authority_id) { this.authority_id = authority_id; }
    
    public Long getResponse_id() { return response_id; }
    public void setResponse_id(Long response_id) { this.response_id = response_id; }
}
