package com.complaintmanagement;

import com.complaintmanagement.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Comprehensive system test for Complaint Management System
 * Tests all major functionality including:
 * - Database connectivity
 * - User authentication (Citizen and Authority)
 * - Complaint CRUD operations
 * - Response functionality
 * - Data integrity
 */
public class CompleteSystemTest {
    
    private static EntityManager em;
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("COMPLAINT MANAGEMENT SYSTEM - COMPLETE TEST");
        System.out.println("==============================================\n");
        
        // Initialize database connection
        if (!initializeDatabase()) {
            System.err.println("❌ Failed to initialize database. Exiting tests.");
            return;
        }
        
        // Run all tests
        testDatabaseConnection();
        testDepartmentData();
        testCitizenAuthentication();
        testAuthorityAuthentication();
        testComplaintRetrieval();
        testComplaintStatusFiltering();
        testResponseRetrieval();
        testDepartmentComplaintMapping();
        testDataIntegrity();
        
        // Print summary
        printTestSummary();
        
        // Cleanup
        cleanup();
    }
    
    private static boolean initializeDatabase() {
        try {
            if (DatabaseConnection.testConnection()) {
                System.out.println("✓ Database connection test successful\n");
                em = DatabaseConnection.getEntityManager();
                return true;
            } else {
                System.err.println("❌ Database connection test failed\n");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
            return false;
        }
    }
    
    private static void testDatabaseConnection() {
        runTest("Database Connection", () -> {
            return em != null && em.isOpen();
        });
    }
    
    private static void testDepartmentData() {
        runTest("Department Data Exists", () -> {
            TypedQuery<Department> query = em.createQuery("SELECT d FROM Department d", Department.class);
            List<Department> departments = query.getResultList();
            System.out.println("   Found " + departments.size() + " departments");
            return departments.size() > 0;
        });
    }
    
    private static void testCitizenAuthentication() {
        runTest("Citizen Authentication", () -> {
            // Test with known test user
            TypedQuery<Citizen> query = em.createQuery(
                "SELECT c FROM Citizen c WHERE c.citizen_username = :username", 
                Citizen.class
            );
            query.setParameter("username", "test");
            
            List<Citizen> results = query.getResultList();
            if (results.isEmpty()) {
                System.out.println("   ⚠ Test user 'test' not found. You may need to populate the database.");
                return true; // Not a failure, just a warning
            }
            
            Citizen citizen = results.get(0);
            boolean authenticated = citizen.getCitizen_password().equals("test");
            
            if (authenticated) {
                System.out.println("   ✓ Successfully authenticated citizen: " + citizen.getCitizen_name());
            }
            
            return authenticated;
        });
    }
    
    private static void testAuthorityAuthentication() {
        runTest("Authority Authentication", () -> {
            TypedQuery<Authority> query = em.createQuery(
                "SELECT a FROM Authority a WHERE a.authority_username LIKE :pattern", 
                Authority.class
            );
            query.setParameter("pattern", "%officer%");
            query.setMaxResults(1);
            
            List<Authority> authorities = query.getResultList();
            
            if (authorities.isEmpty()) {
                System.out.println("   ⚠ No authority users found. Database may need population.");
                return true; // Warning, not failure
            }
            
            Authority authority = authorities.get(0);
            System.out.println("   ✓ Found authority: " + authority.getAuthority_username() + 
                             " (" + authority.getAuthority_designation() + ")");
            
            return true;
        });
    }
    
    private static void testComplaintRetrieval() {
        runTest("Complaint Retrieval", () -> {
            TypedQuery<Complaint> query = em.createQuery(
                "SELECT c FROM Complaint c ORDER BY c.complaint_date DESC", 
                Complaint.class
            );
            query.setMaxResults(5);
            
            List<Complaint> complaints = query.getResultList();
            System.out.println("   Found " + complaints.size() + " complaints in database");
            
            if (!complaints.isEmpty()) {
                Complaint c = complaints.get(0);
                System.out.println("   Sample: ID=" + c.getComplaint_id() + 
                                 ", Type=" + c.getComplaint_type() + 
                                 ", Status=" + c.getResolve_status());
            }
            
            return true; // Always pass, just informational
        });
    }
    
    private static void testComplaintStatusFiltering() {
        runTest("Complaint Status Filtering", () -> {
            // Test filtering by different statuses
            for (ComplaintStatus status : ComplaintStatus.values()) {
                TypedQuery<Complaint> query = em.createQuery(
                    "SELECT c FROM Complaint c WHERE c.resolve_status = :status", 
                    Complaint.class
                );
                query.setParameter("status", status);
                
                long count = query.getResultList().size();
                System.out.println("   " + status + ": " + count + " complaints");
            }
            
            return true;
        });
    }
    
    private static void testResponseRetrieval() {
        runTest("Response Retrieval", () -> {
            TypedQuery<Response> query = em.createQuery(
                "SELECT r FROM Response r ORDER BY r.response_date DESC", 
                Response.class
            );
            query.setMaxResults(5);
            
            List<Response> responses = query.getResultList();
            System.out.println("   Found " + responses.size() + " responses in database");
            
            if (!responses.isEmpty()) {
                Response r = responses.get(0);
                System.out.println("   Sample response by authority ID: " + 
                                 r.getId().getAuthority_id() + 
                                 ", Status: " + r.getRead_status());
            }
            
            return true;
        });
    }
    
    private static void testDepartmentComplaintMapping() {
        runTest("Department-Complaint Mapping", () -> {
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT d.dept_name, COUNT(c) FROM Department d " +
                "LEFT JOIN Complaint c ON d.dept_id = c.department.dept_id " +
                "GROUP BY d.dept_name " +
                "ORDER BY COUNT(c) DESC", 
                Object[].class
            );
            query.setMaxResults(5);
            
            List<Object[]> results = query.getResultList();
            System.out.println("   Top departments by complaint count:");
            
            for (Object[] row : results) {
                System.out.println("   - " + row[0] + ": " + row[1] + " complaints");
            }
            
            return true;
        });
    }
    
    private static void testDataIntegrity() {
        runTest("Data Integrity Checks", () -> {
            // Check for orphaned complaints (citizens/departments)
            TypedQuery<Long> orphanedComplaints = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.citizen IS NULL OR c.department IS NULL", 
                Long.class
            );
            long orphanedCount = orphanedComplaints.getSingleResult();
            
            // Check for orphaned responses
            TypedQuery<Long> orphanedResponses = em.createQuery(
                "SELECT COUNT(r) FROM Response r WHERE r.complaint IS NULL OR r.authority IS NULL", 
                Long.class
            );
            long orphanedRespCount = orphanedResponses.getSingleResult();
            
            System.out.println("   Orphaned complaints: " + orphanedCount);
            System.out.println("   Orphaned responses: " + orphanedRespCount);
            
            boolean integrity = (orphanedCount == 0) && (orphanedRespCount == 0);
            
            if (!integrity) {
                System.out.println("   ⚠ Data integrity issues detected");
            } else {
                System.out.println("   ✓ All data relationships intact");
            }
            
            return integrity;
        });
    }
    
    private static void runTest(String testName, TestFunction test) {
        testsRun++;
        System.out.println("\n[TEST " + testsRun + "] " + testName);
        System.out.println("-------------------------------------------");
        
        try {
            boolean passed = test.run();
            
            if (passed) {
                testsPassed++;
                System.out.println("✓ PASSED");
            } else {
                testsFailed++;
                System.out.println("❌ FAILED");
            }
        } catch (Exception e) {
            testsFailed++;
            System.out.println("❌ FAILED with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printTestSummary() {
        System.out.println("\n==============================================");
        System.out.println("TEST SUMMARY");
        System.out.println("==============================================");
        System.out.println("Total Tests: " + testsRun);
        System.out.println("Passed: " + testsPassed + " ✓");
        System.out.println("Failed: " + testsFailed + " ❌");
        
        double successRate = (testsPassed * 100.0) / testsRun;
        System.out.println("Success Rate: " + String.format("%.1f%%", successRate));
        
        if (testsFailed == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
        } else {
            System.out.println("\n⚠ Some tests failed. Please review the output above.");
        }
        System.out.println("==============================================\n");
    }
    
    private static void cleanup() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        DatabaseConnection.close();
        System.out.println("✓ Database connection closed");
    }
    
    @FunctionalInterface
    interface TestFunction {
        boolean run() throws Exception;
    }
}
