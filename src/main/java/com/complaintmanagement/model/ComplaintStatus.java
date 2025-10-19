package com.complaintmanagement.model;

public enum ComplaintStatus {
    UNREAD("Unread"),
    READ("Read"),
    IN_PROGRESS("In Progress"),
    CLOSED("Closed");
    
    private final String displayName;
    
    ComplaintStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
