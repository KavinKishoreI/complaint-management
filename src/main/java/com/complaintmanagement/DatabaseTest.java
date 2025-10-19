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
        // Create and persist a Citizen
        Citizen citizen = new Citizen(null, "Jane Tester", "Test City", "12345", "5551234567", "jane@test.com");
        em.persist(citizen);
        // Create and persist an Authority
        Authority authority = new Authority("Water Branch", "Officer", "officer@water.com");
        em.persist(authority);
        // Create and persist a Complaint
        Complaint complaint = new Complaint("Type A", "Water", "Leak", "Leaking pipe", "Open", new Date(), citizen);
        em.persist(complaint);
        em.getTransaction().commit();
        // Create and persist a Response
        em.getTransaction().begin();
        ResponseId responseId = new ResponseId(complaint.getComplaint_id(), authority.getAuthority_id(), 1L);
        Response response = new Response(responseId, "Issue noted.", new Date(), complaint, authority);
        em.persist(response);
        em.getTransaction().commit();
        // Retrieve and print
        Citizen foundCitizen = em.find(Citizen.class, citizen.getCitizen_id());
        Authority foundAuthority = em.find(Authority.class, authority.getAuthority_id());
        Complaint foundComplaint = em.find(Complaint.class, complaint.getComplaint_id());
        Response foundResponse = em.find(Response.class, responseId);
        System.out.printf("Citizen: %s, Authority: %s, Complaint: %s, Response: %s\n",
                foundCitizen.getCitizen_name(), foundAuthority.getAuthority_branch(),
                foundComplaint.getComplaint_title(), foundResponse.getResponse());
        em.close();
        DatabaseConnection.close();
    }
}
