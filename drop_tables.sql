-- Drop all existing tables to start fresh with new 3NF schema
-- Run this script in SQL*Plus or Oracle SQL Developer before running DatabaseTest

DROP TABLE Response CASCADE CONSTRAINTS;
DROP TABLE Complaint CASCADE CONSTRAINTS;
DROP TABLE Authority CASCADE CONSTRAINTS;
DROP TABLE Citizen CASCADE CONSTRAINTS;
DROP TABLE Department CASCADE CONSTRAINTS;

-- Now run your Java application with hibernate.hbm2ddl.auto=create-drop
-- It will create fresh tables with the new schema
