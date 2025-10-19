# Requirements Verification Report

## Complaint Management System

**Date:** October 19, 2025  
**Project:** Complaint Management System v1.0  
**Verification Type:** Functional Requirements Analysis

---

## Summary

| Requirement                                        | Status          | Implementation               |
| -------------------------------------------------- | --------------- | ---------------------------- |
| 1. Citizens can lodge new complaints               | ✅ **COMPLETE** | Fully Implemented            |
| 2. Complaints contain department                   | ✅ **COMPLETE** | Fully Implemented            |
| 3. Authorities have unique department              | ✅ **COMPLETE** | Fully Implemented            |
| 4. Authorities view department-specific complaints | ✅ **COMPLETE** | Fully Implemented            |
| 5. Multiple responses per complaint                | ✅ **COMPLETE** | Fully Implemented            |
| 6. Authorities change complaint status             | ⚠️ **PARTIAL**  | Missing "READ" status update |

**Overall Compliance:** 5/6 Fully Complete, 1/6 Partially Complete (83% Complete)

---

## Detailed Analysis

### ✅ Requirement 1: Citizens can lodge a new complaint

**Status:** FULLY IMPLEMENTED

**Evidence:**

- **File:** `RegisterComplaintController.java`
- **Method:** `handleSubmit(ActionEvent event)` (Lines 203-280)

**Implementation Details:**

```java
@FXML
private void handleSubmit(ActionEvent event) {
    // Validates form
    if (!validateForm()) return;

    // Creates new complaint object
    Complaint complaint = new Complaint();
    complaint.setComplaint_title(titleField.getText().trim());
    complaint.setComplaint_type(complaintType);
    complaint.setDepartment(assignedDepartment);
    complaint.setComplaint_descp(descriptionArea.getText().trim());
    complaint.setResolve_status(ComplaintStatus.UNREAD);
    complaint.setComplaint_date(new Date());
    complaint.setCitizen(currentCitizen);

    // Persists to database
    em.getTransaction().begin();
    em.persist(complaint);
    em.getTransaction().commit();
}
```

**Features:**

- ✅ Form validation before submission
- ✅ Auto-generates complaint ID
- ✅ Sets initial status to UNREAD
- ✅ Associates with logged-in citizen
- ✅ Auto-assigns department based on complaint type
- ✅ Displays success confirmation with reference ID

---

### ✅ Requirement 2: Each complaint must contain a department

**Status:** FULLY IMPLEMENTED

**Evidence:**

- **File:** `Complaint.java`
- **Lines:** 16-18

**Implementation Details:**

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "dept_id", nullable = false)
private Department department;
```

**Database Constraints:**

- ✅ Department field is **mandatory** (`nullable = false`)
- ✅ Foreign key relationship enforced
- ✅ Department auto-assigned based on complaint type
- ✅ Cannot create complaint without department

**Validation:**

```java
Department assignedDepartment = getDepartmentForComplaintType(complaintType);
if (assignedDepartment == null) {
    showError("Could not assign department. Please try again.");
    return;
}
```

---

### ✅ Requirement 3: Authorities have their unique department

**Status:** FULLY IMPLEMENTED

**Evidence:**

- **File:** `Authority.java`
- **Lines:** 19-20

**Implementation Details:**

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "dept_id", nullable = false)
private Department department;
```

**Database Constraints:**

- ✅ Department field is **mandatory** (`nullable = false`)
- ✅ Each authority belongs to exactly one department
- ✅ Department relationship loaded eagerly
- ✅ Foreign key constraint enforced

**Sample Data Verification:**
From `database_population.sql`:

- water_officer1 → Water Supply Department
- pwd_officer1 → Public Works Department
- elec_officer1 → Electricity Department
- Each authority has unique department assignment

---

### ✅ Requirement 4: Authorities view only their department complaints

**Status:** FULLY IMPLEMENTED

**Evidence:**

- **File:** `AuthorityDashboardController.java`
- **Method:** `loadComplaintData()` (Lines 268-290)

**Implementation Details:**

```java
private void loadComplaintData() {
    Long deptId = currentAuthority.getDepartment().getDept_id();

    // Filter by department
    TypedQuery<Complaint> query = em.createQuery(
        "SELECT c FROM Complaint c WHERE c.department.dept_id = :deptId " +
        "ORDER BY CASE c.resolve_status " +
        "  WHEN com.complaintmanagement.model.ComplaintStatus.UNREAD THEN 1 " +
        "  WHEN com.complaintmanagement.model.ComplaintStatus.READ THEN 2 " +
        "  WHEN com.complaintmanagement.model.ComplaintStatus.IN_PROGRESS THEN 3 " +
        "  WHEN com.complaintmanagement.model.ComplaintStatus.CLOSED THEN 4 " +
        "END, c.complaint_date DESC",
        Complaint.class
    );
    query.setParameter("deptId", deptId);
}
```

**Features:**

- ✅ Filters complaints by `department.dept_id`
- ✅ Only shows complaints assigned to authority's department
- ✅ Statistics calculated per department
- ✅ Cannot view other departments' complaints
- ✅ Ordered by status priority and date

**Statistics Filtering:**

```java
// All statistics queries filter by department
"SELECT COUNT(c) FROM Complaint c WHERE c.department.dept_id = :deptId"
```

---

### ✅ Requirement 5: Authorities can give multiple responses

**Status:** FULLY IMPLEMENTED

**Evidence:**

- **File:** `AuthorityRespondController.java`
- **Method:** `handleSubmitResponse()` (Lines 225-280)
- **Model:** `ResponseId.java` (Composite key implementation)

**Implementation Details:**

**Composite Key Structure:**

```java
@Embeddable
public class ResponseId implements Serializable {
    private Long complaint_id;
    private Long authority_id;
    private Long response_id;  // Incremental for multiple responses
}
```

**Multiple Response Logic:**

```java
// Get max response_id for this complaint and authority
TypedQuery<Long> maxQuery = em.createQuery(
    "SELECT COALESCE(MAX(r.id.response_id), 0) FROM Response r " +
    "WHERE r.id.complaint_id = :complaintId " +
    "AND r.id.authority_id = :authorityId",
    Long.class
);
Long maxResponseId = maxQuery.getSingleResult();

// Create new response with incremented response_id
ResponseId responseId = new ResponseId(
    complaint.getComplaint_id(),
    authority.getAuthority_id(),
    maxResponseId + 1  // Increment for each new response
);
```

**Features:**

- ✅ Supports unlimited responses per complaint
- ✅ Each response has unique ID (composite key)
- ✅ Responses ordered by date
- ✅ Shows all previous responses
- ✅ Multiple authorities can respond to same complaint
- ✅ Response history preserved

**Model Relationships:**

```java
@OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Response> responses;  // In Complaint.java

@OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Response> responses;  // In Authority.java
```

---

### ⚠️ Requirement 6: Authorities change complaint status to "Read", "In Progress", "Closed"

**Status:** PARTIALLY IMPLEMENTED (67% Complete)

**What's Implemented:**

#### ✅ 1. Status: "CLOSED"

**File:** `AuthorityRespondController.java` (Lines 323-340)

```java
private void closeComplaint() {
    em.getTransaction().begin();
    complaint.setResolve_status(ComplaintStatus.CLOSED);
    em.merge(complaint);
    em.getTransaction().commit();
}
```

- ✅ "Close Ticket" button available
- ✅ Explicitly sets status to CLOSED
- ✅ Persists to database
- ✅ Disables further responses when closed

#### ✅ 2. Status: "IN_PROGRESS"

**File:** `AuthorityRespondController.java` (Lines 268-273)

```java
// Auto-update when submitting response
if (complaint.getResolve_status() == ComplaintStatus.UNREAD ||
    complaint.getResolve_status() == ComplaintStatus.READ) {
    complaint.setResolve_status(ComplaintStatus.IN_PROGRESS);
    em.merge(complaint);
}
```

- ✅ Automatically set when authority submits a response
- ✅ Logical status progression
- ✅ Persisted to database

#### ❌ 3. Status: "READ" - **MISSING**

**Issue Found:**

- ❌ No mechanism to explicitly mark complaint as READ
- ❌ Status never transitions from UNREAD to READ
- ❌ READ status exists in enum but is never set
- ❌ No button or automatic trigger to set READ status

**Search Results:**

```
grep search for "ComplaintStatus.READ":
- Found in queries (checking status)
- Found in conditional logic
- NEVER found as: setResolve_status(ComplaintStatus.READ)
```

**Impact:**

- Status flow: UNREAD → IN_PROGRESS → CLOSED (READ is skipped)
- Statistics may show 0 READ complaints
- Requirement not fully met

---

## Missing Functionality Analysis

### The "READ" Status Gap

**Expected Workflow:**

1. Citizen submits complaint → Status: **UNREAD**
2. Authority views complaint → Status: **READ** ⚠️ MISSING
3. Authority responds → Status: **IN_PROGRESS**
4. Authority closes complaint → Status: **CLOSED**

**Current Workflow:**

1. Citizen submits complaint → Status: **UNREAD**
2. Authority views complaint → Status: **UNREAD** (unchanged)
3. Authority responds → Status: **IN_PROGRESS**
4. Authority closes complaint → Status: **CLOSED**

---

## Recommended Fixes

### Fix 1: Auto-mark as READ when viewing (Recommended)

**Location:** `AuthorityRespondController.java` → `loadComplaintData()` method

**Add after loading complaint:**

```java
private void loadComplaintData(Long complaintId) {
    try {
        complaint = em.find(Complaint.class, complaintId);

        if (complaint != null) {
            // Auto-mark as READ if currently UNREAD
            if (complaint.getResolve_status() == ComplaintStatus.UNREAD) {
                em.getTransaction().begin();
                complaint.setResolve_status(ComplaintStatus.READ);
                em.merge(complaint);
                em.getTransaction().commit();
                System.out.println("Complaint marked as READ");
            }

            // ... rest of existing code ...
        }
    } catch (Exception e) {
        // ... error handling ...
    }
}
```

### Fix 2: Add "Mark as Read" button (Alternative)

Add a button in the UI that allows authorities to manually mark complaints as READ.

**Benefits of Fix 1 (Auto-mark):**

- ✅ Automatic, no user action needed
- ✅ Reflects real-world behavior (viewing = reading)
- ✅ Simpler UX
- ✅ Matches common patterns (email, notifications, etc.)

**Benefits of Fix 2 (Manual button):**

- ✅ Explicit control
- ✅ Authority decides when they've "read" it
- ✅ More flexibility

---

## Test Results from Database

From `CompleteSystemTest.java` execution:

```
[TEST 6] Complaint Status Filtering
-------------------------------------------
   Unread: 10 complaints
   Read: 3 complaints  ⚠️ Likely test data, not set by application
   In Progress: 2 complaints
   Closed: 1 complaint
✓ PASSED
```

**Note:** The 3 READ complaints in database are likely from manual SQL inserts, not from application functionality.

---

## Compliance Matrix

| Requirement              | Must Have | Nice to Have | Implementation Status |
| ------------------------ | --------- | ------------ | --------------------- |
| Lodge complaints         | ✓         |              | ✅ Complete           |
| Complaint has department | ✓         |              | ✅ Complete           |
| Authority has department | ✓         |              | ✅ Complete           |
| Department filtering     | ✓         |              | ✅ Complete           |
| Multiple responses       | ✓         |              | ✅ Complete           |
| Set status: CLOSED       | ✓         |              | ✅ Complete           |
| Set status: IN_PROGRESS  | ✓         |              | ✅ Complete           |
| Set status: READ         | ✓         |              | ❌ **Missing**        |

---

## Recommendations

### Priority 1: Critical (Fix Before Production)

1. **Implement READ status functionality** (Requirement 6 gap)
   - Recommended: Auto-mark as READ when authority views complaint
   - Estimated effort: 30 minutes

### Priority 2: Enhancement

2. Add status change logging/audit trail
3. Add notifications when status changes
4. Add ability to reopen CLOSED complaints if needed

### Priority 3: Nice to Have

5. Add bulk status update capability
6. Add status change history in UI
7. Add filters by status in dashboard

---

## Conclusion

**Overall Assessment:** The system is **83% compliant** with requirements.

**Strengths:**

- ✅ Core complaint management workflow complete
- ✅ Department-based access control working perfectly
- ✅ Multiple responses fully functional
- ✅ Two out of three status transitions implemented
- ✅ Clean, maintainable code structure

**Critical Gap:**

- ❌ Missing READ status transition (Requirement 6)

**Recommendation:**
Implement the READ status functionality (Fix 1 suggested above) to achieve 100% requirements compliance. This is a minor fix that will complete the status workflow and fully satisfy Requirement 6.

**Estimated Time to 100% Compliance:** 30-60 minutes

---

## Files Analyzed

1. `src/main/java/com/complaintmanagement/model/Complaint.java`
2. `src/main/java/com/complaintmanagement/model/Authority.java`
3. `src/main/java/com/complaintmanagement/model/Response.java`
4. `src/main/java/com/complaintmanagement/model/ResponseId.java`
5. `src/main/java/com/complaintmanagement/controller/RegisterComplaintController.java`
6. `src/main/java/com/complaintmanagement/controller/AuthorityDashboardController.java`
7. `src/main/java/com/complaintmanagement/controller/AuthorityRespondController.java`
8. `src/main/java/com/complaintmanagement/controller/DashboardController.java`
9. `sql_scripts/database_population.sql`

---

**Report Generated:** October 19, 2025  
**Verified By:** GitHub Copilot  
**Status:** Ready for review and implementation of recommended fixes
