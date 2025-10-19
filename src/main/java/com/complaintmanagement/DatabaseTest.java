package com.complaintmanagement;

import com.complaintmanagement.model.*;
import jakarta.persistence.EntityManager;
import java.util.Date;

public class DatabaseTest {
    public static void main(String[] args) {
        // Test DB connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("DB connection successful. Now testing CRUD for Citizen, Complaint, Authority, and Response...");
        } else {
            System.err.println("DB connection failed. Exiting test.");
            return;
        }
        EntityManager em = DatabaseConnection.getEntityManager();
        em.getTransaction().begin();
        
        // Create and persist a Department
        Department department = new Department("Water Department");
        em.persist(department);
        
        // Create and persist a Citizen
        Citizen citizen = new Citizen("jane_test", "password123", "Jane Tester", "Test City", "12345", "5551234567", "jane@test.com");
        em.persist(citizen);
        
        // Create and persist an Authority
        Authority authority = new Authority("officer_water", "password456", department, "Water Officer", "officer@water.com");
        em.persist(authority);
        
        // Create and persist a Complaint
        Complaint complaint = new Complaint("Water Supply", department, "Water Leak", "Leaking pipe on Main Street", ComplaintStatus.UNREAD, new Date(), citizen);
        em.persist(complaint);
        
        em.getTransaction().commit();
        
        // Create and persist a Response
        em.getTransaction().begin();
        ResponseId responseId = new ResponseId(complaint.getComplaint_id(), authority.getAuthority_id(), 1L);
        Response response = new Response(responseId, "Issue noted. We will send a team to investigate.", new Date(), complaint, authority);
        em.persist(response);
        em.getTransaction().commit();
        
        // Retrieve and print
        Department foundDept = em.find(Department.class, department.getDept_id());
        Citizen foundCitizen = em.find(Citizen.class, citizen.getCitizen_id());
        Authority foundAuthority = em.find(Authority.class, authority.getAuthority_id());
        Complaint foundComplaint = em.find(Complaint.class, complaint.getComplaint_id());
        Response foundResponse = em.find(Response.class, responseId);
        
        System.out.println("\n=== DATABASE TEST RESULTS ===");
        System.out.printf("Department: %s (ID: %d)\n", foundDept.getDept_name(), foundDept.getDept_id());
        System.out.printf("Citizen: %s (Username: %s, ID: %d)\n", foundCitizen.getCitizen_name(), foundCitizen.getCitizen_username(), foundCitizen.getCitizen_id());
        System.out.printf("Authority: %s - %s (Username: %s, Dept: %s, ID: %d)\n", 
                foundAuthority.getAuthority_designation(), foundAuthority.getAuthority_email(),
                foundAuthority.getAuthority_username(), foundAuthority.getDepartment().getDept_name(), foundAuthority.getAuthority_id());
        System.out.printf("Complaint: %s - Status: %s (Dept: %s, ID: %d)\n", 
                foundComplaint.getComplaint_title(), foundComplaint.getResolve_status(),
                foundComplaint.getDepartment().getDept_name(), foundComplaint.getComplaint_id());
        System.out.printf("Response: %s (Date: %s)\n", foundResponse.getResponse(), foundResponse.getResponse_date());
        System.out.println("=== ALL TESTS PASSED ===\n");
        em.close();
        DatabaseConnection.close();
    }
}
