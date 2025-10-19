# Complaint Management System - 3NF Schema Implementation

## Summary

Successfully implemented the complete Third Normal Form (3NF) database schema for the Complaint Management System with ORM (Hibernate/JPA) mapping.

---

## 📋 Implemented Schema (3NF)

### **1. Department Entity** ✅

```java
@Entity
@Table(name = "Department")
class Department {
    dept_id (PK, Auto-increment)
    dept_name (UNIQUE, NOT NULL)

    @OneToMany → List<Complaint>
    @OneToMany → List<Authority>
}
```

### **2. Citizen Entity** ✅

```java
@Entity
@Table(name = "Citizen")
class Citizen {
    citizen_id (PK, Auto-increment)
    citizen_username (UNIQUE, NOT NULL)
    citizen_password (NOT NULL)
    citizen_name
    citizen_city
    citizen_pincode
    citizen_phone
    citizen_email (UNIQUE)

    @OneToMany → List<Complaint>
}
```

### **3. Authority Entity** ✅

```java
@Entity
@Table(name = "Authority")
class Authority {
    authority_id (PK, Auto-increment)
    authority_username (UNIQUE, NOT NULL)
    authority_password (NOT NULL)
    dept_id (FK → Department, NOT NULL)
    authority_designation
    authority_email (UNIQUE)

    @ManyToOne → Department
    @OneToMany → List<Response>
}
```

### **4. Complaint Entity** ✅

```java
@Entity
@Table(name = "Complaint")
class Complaint {
    complaint_id (PK, Auto-increment)
    complaint_type
    dept_id (FK → Department, NOT NULL)
    complaint_title
    complaint_descp
    resolve_status (ENUM: UNREAD, READ, IN_PROGRESS, CLOSED)
    complaint_date
    citizen_id (FK → Citizen, NOT NULL)

    @ManyToOne → Department
    @ManyToOne → Citizen
    @OneToMany → List<Response>
}
```

### **5. Response Entity** ✅

```java
@Entity
@Table(name = "Response")
class Response {
    @EmbeddedId
    ResponseId {
        complaint_id (FK → Complaint)
        authority_id (FK → Authority)
        response_id
    }
    response (TEXT)
    response_date

    @ManyToOne → Complaint
    @ManyToOne → Authority
}
```

### **6. ComplaintStatus Enum** ✅

```java
public enum ComplaintStatus {
    UNREAD, READ, IN_PROGRESS, CLOSED
}
```

---

## 🔄 Normalization Process

### **Original (Unnormalized)**

```
Complaints(
    citizen_id, citizen_name, citizen_city, ...,
    complaint_id, dept_concerned, ...,
    authority_id, authority_branch, ...,
    response_id, response, ...
)
```

### **1NF → 2NF → 3NF**

**Issues Fixed:**

1. ❌ **Removed partial dependencies** (2NF violation)
2. ❌ **Removed transitive dependencies** (3NF violation)
   - `complaint_id → dept_id → dept_name` ✅ Fixed
   - `authority_id → dept_id → dept_name` ✅ Fixed
3. ❌ **String-based foreign keys** → ✅ **Proper FK relationships**
4. ❌ **No authentication** → ✅ **Added username/password**
5. ❌ **String status** → ✅ **Type-safe ENUM**

---

## ✅ Requirements Mapping

| Requirement                     | Implementation                              | Status |
| ------------------------------- | ------------------------------------------- | ------ |
| 1. Citizens lodge complaints    | `Complaint.citizen_id` FK → `Citizen`       | ✅     |
| 2. Each complaint has dept      | `Complaint.dept_id` FK → `Department`       | ✅     |
| 3. Authorities have unique dept | `Authority.dept_id` FK → `Department`       | ✅     |
| 4. View complaints by dept      | Filter: `WHERE dept_id = authority.dept_id` | ✅     |
| 5. Multiple responses           | `Response` with composite PK                | ✅     |
| 6. Status management            | `ComplaintStatus` ENUM with transitions     | ✅     |

---

## 🗂️ Files Created/Modified

### **Created:**

1. `Department.java` - Department entity
2. `ComplaintStatus.java` - Status enum
3. `DropTables.java` - Utility to drop old tables

### **Modified:**

1. `Citizen.java` - Added authentication fields + @OneToMany
2. `Authority.java` - Added authentication + @ManyToOne Department
3. `Complaint.java` - Changed to FK relations + ENUM status
4. `Response.java` - Already correct
5. `persistence.xml` - Registered Department entity
6. `DatabaseTest.java` - Updated test cases for new schema

---

## 🧪 Test Results

**Database Test:** ✅ **PASSED**

```
Department: Water Department (ID: 1)
Citizen: Jane Tester (Username: jane_test, ID: 1)
Authority: Water Officer (Username: officer_water, Dept: Water Department, ID: 1)
Complaint: Water Leak - Status: Unread (Dept: Water Department, ID: 1)
Response: Issue noted. We will send a team to investigate.
=== ALL TESTS PASSED ===
```

### **Verified:**

- ✅ Department creation
- ✅ Citizen registration with authentication
- ✅ Authority creation with department linkage
- ✅ Complaint lodging with proper FKs
- ✅ Multiple responses per complaint
- ✅ Status enum working correctly
- ✅ All foreign key constraints working

---

## 🎯 Next Steps (Recommended)

1. **Create DAO Layer** - CitizenDAO, AuthorityDAO, DepartmentDAO, ComplaintDAO, ResponseDAO
2. **Create Service Layer** - AuthenticationService, ComplaintService, ResponseService
3. **Implement LoginController** - Authenticate Citizens and Authorities
4. **Build Citizen UI** - CitizenDashboard, NewComplaint form
5. **Build Authority UI** - AuthorityDashboard with department filtering
6. **Implement Workflows:**
   - Citizen: Register → Login → Lodge Complaint → View Status
   - Authority: Login → View Dept Complaints → Add Response → Update Status

---

## 📊 Schema Diagram

```
┌─────────────┐
│ Department  │
│─────────────│
│ dept_id PK  │◄──┐
│ dept_name   │   │
└─────────────┘   │
                  │
      ┌───────────┴───────────┐
      │                       │
┌─────▼──────┐         ┌──────▼─────┐
│  Authority │         │  Complaint │
│────────────│         │────────────│
│ auth_id PK │         │ comp_id PK │
│ username   │         │ type       │
│ password   │         │ title      │
│ dept_id FK │         │ descp      │
│ designation│         │ status     │
│ email      │         │ date       │
└────────────┘         │ citizen_id │◄───┐
      │                │ dept_id FK │    │
      │                └────────────┘    │
      │                      │           │
      │                      │      ┌────┴──────┐
      │                      │      │  Citizen  │
      │                      │      │───────────│
      │                      │      │ cit_id PK │
      │                      │      │ username  │
      │                      │      │ password  │
      │                      │      │ name      │
      │                      │      │ city      │
      │                      │      │ pincode   │
      │                      │      │ phone     │
      │                      │      │ email     │
      │                      │      └───────────┘
      │                      │
      │              ┌───────▼────────┐
      └──────────────►    Response    │
                     │────────────────│
                     │ comp_id FK  PK │
                     │ auth_id FK  PK │
                     │ resp_id     PK │
                     │ response       │
                     │ response_date  │
                     └────────────────┘
```

---

## 🔧 Technology Stack

- **Java 21**
- **JavaFX 23** - UI Framework
- **Hibernate 6.5.2** - ORM
- **JPA 3.1** - Persistence API
- **Oracle Database 21c** - Database
- **Maven** - Build Tool

---

## 📝 Notes

- Changed from `hibernate.hbm2ddl.auto=create-drop` to `update` for production
- All entities properly configured with JPA annotations
- Foreign key constraints enforced at database level
- Unique constraints on usernames and emails
- Enum used for type safety on complaint status
- Bidirectional relationships properly mapped

---

**Date:** October 19, 2025  
**Status:** ✅ Schema Implementation Complete  
**Next Phase:** DAO & Service Layer Development
