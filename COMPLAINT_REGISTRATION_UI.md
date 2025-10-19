# Complaint Registration UI - Documentation

## Overview

A comprehensive user interface for citizens to register new complaints in the Complaint Management System. The UI is designed with modern JavaFX components and provides a smooth, intuitive experience for complaint submission.

## Files Created

### 1. RegisterComplaint.fxml

**Location:** `src/main/resources/fxml/RegisterComplaint.fxml`

**Features:**

- **Header Section:**
  - Back button for navigation
  - Page title: "Register New Complaint"
  - User information display (shows logged-in citizen name)
- **Form Fields:**
  - **Complaint Title** - TextField (required, max 200 chars)
  - **Complaint Type** - ComboBox with 15 predefined types:
    - Infrastructure Issue, Public Safety, Sanitation & Hygiene
    - Water Supply, Electricity, Road Maintenance, Drainage Problem
    - Noise/Air Pollution, Illegal Construction, Street Lighting
    - Garbage Collection, Public Transport, Traffic Management, Other
  - **Department** - ComboBox (dynamically loaded from database)
  - **Description** - TextArea (required, min 20 chars, max 1000 chars)
    - Real-time character counter
- **Action Buttons:**

  - Submit Complaint - Saves complaint to database
  - Clear Form - Resets all fields
  - Cancel - Returns to dashboard with confirmation

- **Validation & Feedback:**
  - Individual error labels for each field
  - Status message area for success/error notifications
  - Help section with usage tips

### 2. register-complaint.css

**Location:** `src/main/resources/css/register-complaint.css`

**Styling Features:**

- Gradient purple header matching the login page theme
- Clean white form container with subtle shadows
- Modern input fields with focus effects
- Color-coded buttons:
  - Green for Submit (primary action)
  - Gray for Clear (secondary action)
  - Red outline for Cancel
- Responsive hover and pressed states
- Status messages with color coding (success=green, error=red, warning=orange)
- Help section with light blue background

### 3. RegisterComplaintController.java

**Location:** `src/main/java/com/complaintmanagement/controller/RegisterComplaintController.java`

**Key Features:**

#### Initialization

- Connects to database using JPA EntityManager
- Loads departments dynamically from database
- Sets up 15 predefined complaint types
- Configures character counter for description field
- Adds validation listeners to all form fields

#### Form Validation

- **Title:** Required, max 200 characters
- **Type:** Required selection from dropdown
- **Department:** Required selection from dropdown
- **Description:** Required, min 20 characters, max 1000 characters
- Real-time error messages below each field
- Visual feedback with color-coded labels

#### Data Submission (No Service Layer Yet)

```java
// Creates new Complaint entity
Complaint complaint = new Complaint();
complaint.setComplaint_title(titleField.getText().trim());
complaint.setComplaint_type(complaintTypeCombo.getValue());
complaint.setDepartment(departmentCombo.getValue());
complaint.setComplaint_descp(descriptionArea.getText().trim());
complaint.setResolve_status(ComplaintStatus.UNREAD);
complaint.setComplaint_date(new Date());
complaint.setCitizen(currentCitizen);

// Saves directly to database using EntityManager
em.getTransaction().begin();
em.persist(complaint);
em.getTransaction().commit();
```

#### User Experience Features

- Disables submit button during processing to prevent duplicate submissions
- Shows success dialog with complaint reference ID after submission
- Automatically clears form after successful submission (with 2-second delay)
- Confirmation dialog before canceling if form has data
- Navigation back to dashboard

#### Methods

- `initialize()` - Sets up UI components and loads data
- `initializeUser()` - Receives citizen info from previous page
- `handleSubmit()` - Validates and saves complaint
- `handleClear()` - Resets all form fields
- `handleCancel()` - Navigates back with confirmation
- `validateForm()` - Performs comprehensive form validation
- `loadDepartments()` - Fetches departments from database
- `setupCharacterCounter()` - Configures real-time char count
- `showSuccess/Error/Warning()` - Display status messages

### 4. Updated Dashboard.fxml

**Location:** `src/main/resources/fxml/Dashboard.fxml`

**Changes:**

- Replaced placeholder with functional dashboard
- Added "Register New Complaint" button (primary action - green)
- Added placeholder buttons for future features:
  - View My Complaints
  - Track Complaint
  - My Profile
- Added Logout button at bottom
- Header shows welcome message with user name

### 5. Updated DashboardController.java

**Location:** `src/main/java/com/complaintmanagement/controller/DashboardController.java`

**Features:**

- `initializeUser()` - Receives and stores user info from login
- `handleRegisterComplaint()` - Navigates to complaint registration page
  - Loads RegisterComplaint.fxml
  - Applies register-complaint.css
  - Passes user information to RegisterComplaintController
- `handleLogout()` - Returns to login page

### 6. Updated LoginController.java

**Location:** `src/main/java/com/complaintmanagement/controller/LoginController.java`

**Changes:**

- Now properly passes user information to Dashboard
- Calls `dashboardController.initializeUser()` after successful login
- Enables resizable window for dashboard

## Data Flow

1. **Login → Dashboard:**

   ```
   User logs in → LoginController authenticates
   → Opens Dashboard with user info (userId, userName, userType)
   → Dashboard displays personalized welcome message
   ```

2. **Dashboard → Register Complaint:**

   ```
   User clicks "Register New Complaint"
   → DashboardController navigates to RegisterComplaint page
   → Passes citizen ID and name to RegisterComplaintController
   → Controller loads citizen entity and displays form
   ```

3. **Submit Complaint:**

   ```
   User fills form → Clicks Submit
   → Controller validates all fields
   → Creates Complaint entity with status UNREAD
   → Persists to database using JPA
   → Shows success dialog with reference ID
   → Clears form
   ```

4. **Cancel/Back:**
   ```
   User clicks Cancel/Back
   → Checks if form has unsaved data
   → Shows confirmation dialog if needed
   → Navigates back to Dashboard
   ```

## Database Interactions

### Entities Used

- **Citizen** - Current logged-in user
- **Department** - Loaded for dropdown selection
- **Complaint** - Created and persisted on submission
- **ComplaintStatus** - Set to UNREAD for new complaints

### Queries

```java
// Load departments
"SELECT d FROM Department d ORDER BY d.dept_name"

// Load citizen (by ID from login)
em.find(Citizen.class, citizenId)

// Persist complaint (JPA)
em.persist(complaint)
```

## UI/UX Highlights

### Visual Design

- Consistent color scheme (purple gradient theme)
- Professional gradient backgrounds
- Rounded corners and subtle shadows
- Clear typography hierarchy
- Emoji icons for visual appeal

### User Feedback

- ✅ Green success messages
- ❌ Red error messages
- ⚠️ Orange warning messages
- Real-time character counter
- Field-specific validation errors
- Confirmation dialogs for important actions

### Accessibility

- Clear labels for all fields
- Mandatory fields marked with \*
- Help text and tips provided
- Descriptive button labels
- Visual focus indicators

## Testing the UI

### To Test Complaint Registration:

1. **Start the application:**

   ```bash
   mvn javafx:run
   ```

2. **Login as a citizen:**

   - Use valid citizen credentials from database
   - Example: username from Citizen table

3. **Navigate to Register Complaint:**

   - Click "📝 Register New Complaint" button on dashboard

4. **Fill the form:**

   - Enter a title (e.g., "Broken Street Light")
   - Select complaint type (e.g., "Street Lighting")
   - Select department (e.g., "Electricity Department")
   - Enter detailed description (min 20 characters)

5. **Submit:**

   - Click "Submit Complaint"
   - Check for success message with reference ID
   - Verify complaint is saved in database

6. **Test validations:**
   - Try submitting empty form
   - Try submitting with description < 20 chars
   - Try submitting with description > 1000 chars
   - Check error messages appear correctly

## Future Enhancements (Service Layer)

When implementing the service layer, the controller should be refactored to:

1. **Create ComplaintService:**

   ```java
   public interface ComplaintService {
       Complaint registerComplaint(ComplaintDTO dto);
       List<Department> getAllDepartments();
       Citizen getCitizenById(Long id);
   }
   ```

2. **Update Controller:**

   - Inject ComplaintService
   - Replace direct EntityManager calls with service methods
   - Add DTOs for data transfer
   - Implement proper transaction management
   - Add logging and error handling

3. **Additional Features:**
   - File attachment support
   - Image upload for proof
   - Location/address fields
   - Priority selection
   - Category-based department suggestion
   - Email notification after submission
   - SMS confirmation

## Notes

- **No Service Layer Yet:** The controller directly uses EntityManager for database operations
- **Status:** All new complaints are set to UNREAD status
- **Date:** System automatically sets complaint_date to current timestamp
- **Validation:** Client-side validation only (server-side validation recommended for production)
- **Error Handling:** Basic try-catch blocks (should be enhanced with specific exception handling)

## Dependencies

The UI relies on:

- JavaFX 23
- Jakarta Persistence API (JPA)
- Hibernate 6.5.2
- Oracle JDBC Driver
- Existing entity models (Complaint, Citizen, Department, ComplaintStatus)

## Conclusion

The Complaint Registration UI is fully functional and ready for testing. It provides a complete user experience from login to complaint submission with proper validation, feedback, and navigation. The service layer can be added later without major changes to the UI components.
