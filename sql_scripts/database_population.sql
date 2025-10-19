-- =====================================================
-- COMPLAINT MANAGEMENT SYSTEM - DATABASE POPULATION
-- =====================================================
-- This file contains SQL queries to populate the database
-- with departments, sample citizens, authorities, and complaints
-- 
-- DO NOT RUN THESE QUERIES DIRECTLY IN THIS FILE
-- Execute them individually in your Oracle SQL environment
-- =====================================================

-- =====================================================
-- 1. DEPARTMENT TABLE POPULATION
-- =====================================================
-- Creating departments to handle all 15 complaint types
-- Each department is mapped to specific complaint categories

-- Essential Municipal Departments
INSERT INTO Department (dept_name) VALUES ('Public Works Department');
INSERT INTO Department (dept_name) VALUES ('Water Supply Department');
INSERT INTO Department (dept_name) VALUES ('Electricity Department');
INSERT INTO Department (dept_name) VALUES ('Roads and Highways Department');
INSERT INTO Department (dept_name) VALUES ('Sanitation and Waste Management');
INSERT INTO Department (dept_name) VALUES ('Drainage and Sewerage Department');
INSERT INTO Department (dept_name) VALUES ('Public Safety Department');
INSERT INTO Department (dept_name) VALUES ('Traffic Management Department');
INSERT INTO Department (dept_name) VALUES ('Environmental Protection Department');
INSERT INTO Department (dept_name) VALUES ('Urban Planning Department');
INSERT INTO Department (dept_name) VALUES ('Public Transport Department');
INSERT INTO Department (dept_name) VALUES ('Street Lighting Department');
INSERT INTO Department (dept_name) VALUES ('Building and Construction Department');
INSERT INTO Department (dept_name) VALUES ('Health and Hygiene Department');
INSERT INTO Department (dept_name) VALUES ('General Administration');

-- Verify departments inserted
-- SELECT * FROM Department ORDER BY dept_name;

COMMIT;

-- =====================================================
-- 2. SAMPLE CITIZENS POPULATION
-- =====================================================
-- Creating diverse citizen profiles for testing
-- Username format: firstname.lastname or firstname_lastname
-- Password: Simple for testing (in production, use hashed passwords)

-- Citizen 1: John Doe
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('john.doe', 'password123', 'John Doe', 'Mumbai', '400001', '9876543210', 'john.doe@email.com');

-- Citizen 2: Sarah Smith
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('sarah.smith', 'pass1234', 'Sarah Smith', 'Mumbai', '400002', '9876543211', 'sarah.smith@email.com');

-- Citizen 3: Raj Kumar
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('raj.kumar', 'raj12345', 'Raj Kumar', 'Mumbai', '400003', '9876543212', 'raj.kumar@email.com');

-- Citizen 4: Priya Sharma
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('priya.sharma', 'priya123', 'Priya Sharma', 'Mumbai', '400004', '9876543213', 'priya.sharma@email.com');

-- Citizen 5: Mohammed Ali
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('mohammed.ali', 'ali12345', 'Mohammed Ali', 'Mumbai', '400005', '9876543214', 'mohammed.ali@email.com');

-- Citizen 6: Anjali Patel
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('anjali.patel', 'anjali123', 'Anjali Patel', 'Mumbai', '400006', '9876543215', 'anjali.patel@email.com');

-- Citizen 7: Vikram Singh
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('vikram.singh', 'vikram123', 'Vikram Singh', 'Mumbai', '400007', '9876543216', 'vikram.singh@email.com');

-- Citizen 8: Meera Reddy
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('meera.reddy', 'meera123', 'Meera Reddy', 'Mumbai', '400008', '9876543217', 'meera.reddy@email.com');

-- Citizen 9: Amit Verma
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('amit.verma', 'amit1234', 'Amit Verma', 'Mumbai', '400009', '9876543218', 'amit.verma@email.com');

-- Citizen 10: Deepa Menon
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('deepa.menon', 'deepa123', 'Deepa Menon', 'Mumbai', '400010', '9876543219', 'deepa.menon@email.com');

-- Simple test user
INSERT INTO Citizen (citizen_username, citizen_password, citizen_name, citizen_city, citizen_pincode, citizen_phone, citizen_email)
VALUES ('test', 'test', 'Test User', 'Mumbai', '400000', '9999999999', 'test@email.com');

-- Verify citizens inserted
-- SELECT * FROM Citizen ORDER BY citizen_name;

COMMIT;

-- =====================================================
-- 3. SAMPLE AUTHORITIES POPULATION
-- =====================================================
-- Creating authority users for each department
-- These are officials who will handle and respond to complaints
-- Username: dept abbreviation + official number
-- Password: Simple for testing

-- Get department IDs first (use these in the INSERT statements below)
-- SELECT dept_id, dept_name FROM Department ORDER BY dept_name;

-- Note: Replace <dept_id> with actual dept_id from your Department table
-- You can find dept_id by running: SELECT dept_id, dept_name FROM Department;

-- Public Works Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('pwd_officer1', 'pwd123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Public Works Department'), 
    'Senior Engineer', 'pwd.officer1@municipal.gov');

-- Water Supply Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('water_officer1', 'water123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Water Supply Department'), 
    'Water Supply Officer', 'water.officer1@municipal.gov');

-- Electricity Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('elec_officer1', 'elec123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Electricity Department'), 
    'Electrical Engineer', 'elec.officer1@municipal.gov');

-- Roads and Highways Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('road_officer1', 'road123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Roads and Highways Department'), 
    'Highway Engineer', 'road.officer1@municipal.gov');

-- Sanitation and Waste Management Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('sanit_officer1', 'sanit123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Sanitation and Waste Management'), 
    'Sanitation Inspector', 'sanit.officer1@municipal.gov');

-- Drainage and Sewerage Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('drain_officer1', 'drain123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Drainage and Sewerage Department'), 
    'Drainage Engineer', 'drain.officer1@municipal.gov');

-- Public Safety Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('safety_officer1', 'safety123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Public Safety Department'), 
    'Safety Officer', 'safety.officer1@municipal.gov');

-- Traffic Management Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('traffic_officer1', 'traffic123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Traffic Management Department'), 
    'Traffic Controller', 'traffic.officer1@municipal.gov');

-- Environmental Protection Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('env_officer1', 'env123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Environmental Protection Department'), 
    'Environmental Officer', 'env.officer1@municipal.gov');

-- Urban Planning Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('urban_officer1', 'urban123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Urban Planning Department'), 
    'Urban Planner', 'urban.officer1@municipal.gov');

-- Public Transport Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('trans_officer1', 'trans123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Public Transport Department'), 
    'Transport Manager', 'trans.officer1@municipal.gov');

-- Street Lighting Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('light_officer1', 'light123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Street Lighting Department'), 
    'Lighting Technician', 'light.officer1@municipal.gov');

-- Building and Construction Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('build_officer1', 'build123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Building and Construction Department'), 
    'Building Inspector', 'build.officer1@municipal.gov');

-- Health and Hygiene Department Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('health_officer1', 'health123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Health and Hygiene Department'), 
    'Health Inspector', 'health.officer1@municipal.gov');

-- General Administration Authority
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('admin_officer1', 'admin123', 
    (SELECT dept_id FROM Department WHERE dept_name = 'General Administration'), 
    'Administrative Officer', 'admin.officer1@municipal.gov');

-- Simple test authority user
INSERT INTO Authority (authority_username, authority_password, dept_id, authority_designation, authority_email)
VALUES ('testauth', 'test', 
    (SELECT dept_id FROM Department WHERE dept_name = 'General Administration'), 
    'Test Officer', 'testauth@municipal.gov');

-- Verify authorities inserted
-- SELECT a.authority_id, a.authority_username, a.authority_designation, d.dept_name 
-- FROM Authority a JOIN Department d ON a.dept_id = d.dept_id 
-- ORDER BY d.dept_name;

COMMIT;

-- =====================================================
-- 4. SAMPLE COMPLAINTS POPULATION (Optional)
-- =====================================================
-- Creating diverse sample complaints for testing
-- This helps test the complaint viewing and tracking features

-- Note: Use actual citizen_id and dept_id from your tables
-- Find IDs: SELECT citizen_id FROM Citizen WHERE citizen_username = 'john.doe';
--           SELECT dept_id FROM Department WHERE dept_name = 'Roads and Highways Department';

-- Complaint 1: Pothole on Main Road
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Road Maintenance', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Roads and Highways Department'),
    'Large Pothole on MG Road',
    'There is a large and dangerous pothole on MG Road near the traffic signal. It has been causing accidents and needs immediate attention. The pothole is approximately 2 feet wide and 6 inches deep.',
    'UNREAD',
    SYSDATE,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'john.doe'));

-- Complaint 2: Street Light Not Working
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Street Lighting', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Street Lighting Department'),
    'Street Light Not Working on Park Avenue',
    'The street light pole number 45 on Park Avenue has not been working for the past week. This area becomes very dark at night, creating safety concerns for residents and pedestrians.',
    'UNREAD',
    SYSDATE - 1,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'sarah.smith'));

-- Complaint 3: Garbage Not Collected
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Garbage Collection', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Sanitation and Waste Management'),
    'Garbage Not Collected for 3 Days',
    'Garbage has not been collected from our area (Sector 15) for the past three days. The accumulating waste is creating health hazards and attracting stray animals. Immediate action is required.',
    'READ',
    SYSDATE - 2,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'raj.kumar'));

-- Complaint 4: Water Supply Issue
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Water Supply', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Water Supply Department'),
    'Low Water Pressure in Morning Hours',
    'For the past week, we have been experiencing very low water pressure during morning hours (6 AM to 9 AM) in our building. This is affecting daily routines of all residents. Please investigate and resolve.',
    'IN_PROGRESS',
    SYSDATE - 3,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'priya.sharma'));

-- Complaint 5: Drainage Problem
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Drainage Problem', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Drainage and Sewerage Department'),
    'Blocked Drainage Causing Waterlogging',
    'The drainage system on Station Road is completely blocked, causing severe waterlogging during rain. This has been ongoing for two weeks and is making the road impassable. Multiple complaints have been raised but no action taken.',
    'READ',
    SYSDATE - 4,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'mohammed.ali'));

-- Complaint 6: Illegal Construction
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Illegal Construction', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Building and Construction Department'),
    'Unauthorized Construction in Residential Area',
    'There is unauthorized construction happening at plot number 234, Green Valley. The construction violates building regulations and is being done without proper permits. Request immediate inspection and action.',
    'UNREAD',
    SYSDATE - 5,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'anjali.patel'));

-- Complaint 7: Noise Pollution
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Noise Pollution', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Environmental Protection Department'),
    'Loud Music from Restaurant After Midnight',
    'The restaurant "Downtown Cafe" on Church Street plays extremely loud music after midnight, violating noise pollution norms. This has been disturbing the sleep of residents in nearby buildings for the past month.',
    'CLOSED',
    SYSDATE - 10,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'vikram.singh'));

-- Complaint 8: Power Outage
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Electricity', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Electricity Department'),
    'Frequent Power Cuts in Residential Area',
    'Our area (Laxmi Nagar, Block A) has been experiencing frequent power cuts for the past week. Power goes off 3-4 times daily without any notice. This is affecting work-from-home professionals and students. Please resolve urgently.',
    'IN_PROGRESS',
    SYSDATE - 1,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'meera.reddy'));

-- Complaint 9: Traffic Congestion
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Traffic Management', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Traffic Management Department'),
    'Traffic Signal Not Functioning at Major Junction',
    'The traffic signal at the junction of Ring Road and Main Street has not been functioning for two days. This is causing major traffic congestion during peak hours and increasing the risk of accidents.',
    'READ',
    SYSDATE - 2,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'amit.verma'));

-- Complaint 10: Public Transport Delay
INSERT INTO Complaint (complaint_type, dept_id, complaint_title, complaint_descp, resolve_status, complaint_date, citizen_id)
VALUES ('Public Transport', 
    (SELECT dept_id FROM Department WHERE dept_name = 'Public Transport Department'),
    'Bus Route 45 Frequently Cancelled',
    'Bus route number 45 which connects Railway Station to Tech Park is being frequently cancelled without prior notice. This is causing major inconvenience to daily commuters who depend on this service for their office commute.',
    'UNREAD',
    SYSDATE,
    (SELECT citizen_id FROM Citizen WHERE citizen_username = 'deepa.menon'));

-- Verify complaints inserted
-- SELECT c.complaint_id, c.complaint_title, c.complaint_type, d.dept_name, c.resolve_status, cz.citizen_name
-- FROM Complaint c 
-- JOIN Department d ON c.dept_id = d.dept_id 
-- JOIN Citizen cz ON c.citizen_id = cz.citizen_id
-- ORDER BY c.complaint_date DESC;

COMMIT;

-- =====================================================
-- 5. DEPARTMENT-COMPLAINT TYPE MAPPING REFERENCE
-- =====================================================
-- This is a reference guide showing which department handles which complaint types
-- Use this when registering complaints through the application

/*
COMPLAINT TYPE MAPPING:
=======================

1. Infrastructure Issue → Public Works Department
2. Public Safety → Public Safety Department
3. Sanitation & Hygiene → Health and Hygiene Department / Sanitation and Waste Management
4. Water Supply → Water Supply Department
5. Electricity → Electricity Department
6. Road Maintenance → Roads and Highways Department
7. Drainage Problem → Drainage and Sewerage Department
8. Noise Pollution → Environmental Protection Department
9. Air Pollution → Environmental Protection Department
10. Illegal Construction → Building and Construction Department
11. Street Lighting → Street Lighting Department
12. Garbage Collection → Sanitation and Waste Management
13. Public Transport → Public Transport Department
14. Traffic Management → Traffic Management Department
15. Other → General Administration

*/

-- =====================================================
-- 6. USEFUL VERIFICATION QUERIES
-- =====================================================
-- Run these queries to verify your data after insertion

-- Count records in each table
-- SELECT 'Departments' as Table_Name, COUNT(*) as Record_Count FROM Department
-- UNION ALL
-- SELECT 'Citizens', COUNT(*) FROM Citizen
-- UNION ALL
-- SELECT 'Authorities', COUNT(*) FROM Authority
-- UNION ALL
-- SELECT 'Complaints', COUNT(*) FROM Complaint;

-- List all departments with their authority count
-- SELECT d.dept_name, COUNT(a.authority_id) as authority_count
-- FROM Department d
-- LEFT JOIN Authority a ON d.dept_id = a.dept_id
-- GROUP BY d.dept_name
-- ORDER BY d.dept_name;

-- List complaints by status
-- SELECT resolve_status, COUNT(*) as count
-- FROM Complaint
-- GROUP BY resolve_status
-- ORDER BY resolve_status;

-- Recent complaints with full details
-- SELECT 
--     c.complaint_id,
--     c.complaint_title,
--     c.complaint_type,
--     d.dept_name,
--     cz.citizen_name,
--     c.resolve_status,
--     TO_CHAR(c.complaint_date, 'YYYY-MM-DD HH24:MI:SS') as submitted_date
-- FROM Complaint c
-- JOIN Department d ON c.dept_id = d.dept_id
-- JOIN Citizen cz ON c.citizen_id = cz.citizen_id
-- ORDER BY c.complaint_date DESC;

-- =====================================================
-- 7. TEST USER CREDENTIALS FOR QUICK LOGIN
-- =====================================================
/*
TEST CITIZEN ACCOUNTS:
=====================
Username: test          | Password: test
Username: john.doe      | Password: password123
Username: sarah.smith   | Password: pass1234
Username: raj.kumar     | Password: raj12345

TEST AUTHORITY ACCOUNTS:
=======================
Username: testauth          | Password: test
Username: pwd_officer1      | Password: pwd123
Username: water_officer1    | Password: water123
Username: elec_officer1     | Password: elec123
Username: road_officer1     | Password: road123
*/

-- =====================================================
-- NOTES:
-- =====================================================
-- 1. These queries use subqueries to automatically fetch dept_id and citizen_id
-- 2. If you get errors, verify table names and column names match your schema
-- 3. Passwords are plain text for testing - use hashing in production
-- 4. Dates use SYSDATE which gives current timestamp
-- 5. Remember to COMMIT after each section
-- 6. You can modify data (names, descriptions, etc.) as needed
-- =====================================================
