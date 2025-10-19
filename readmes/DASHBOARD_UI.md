# Dashboard UI Implementation

## Overview

The citizen dashboard provides a comprehensive view of complaint statistics, recent complaints, and quick access to key actions. This document describes the implementation of the enhanced dashboard UI.

## Features Implemented

### 1. Statistics Cards

The dashboard displays four key statistics in visually distinct cards:

#### **Total Complaints** (Purple gradient)

- Shows the total number of complaints submitted by the citizen
- Icon: 📊
- Database Query: `COUNT(*) WHERE citizen_id = :userId`

#### **Pending Complaints** (Pink gradient)

- Displays complaints awaiting action (UNREAD + READ status)
- Icon: ⏳
- Database Query: `COUNT(*) WHERE status IN ('UNREAD', 'READ')`

#### **In Progress** (Blue gradient)

- Shows complaints currently being worked on
- Icon: 🔄
- Database Query: `COUNT(*) WHERE status = 'IN_PROGRESS'`

#### **Resolved Complaints** (Green gradient)

- Displays successfully closed complaints
- Icon: ✅
- Database Query: `COUNT(*) WHERE status = 'CLOSED'`

### 2. Quick Actions Section

Three primary action buttons for common tasks:

- **📝 Register New Complaint** (Green) - Opens complaint registration form
- **📋 View All Complaints** (Blue) - Opens detailed complaint list (TODO)
- **🔄 Refresh** (White outline) - Refreshes all dashboard data

### 3. Recent Complaints Table

Displays the 10 most recent complaints in a sortable table:

**Columns:**

- **ID** - Complaint reference number
- **Title** - Complaint title/subject
- **Type** - Category of complaint (Road Issues, Water Supply, etc.)
- **Department** - Assigned department
- **Status** - Current status (UNREAD, READ, IN_PROGRESS, CLOSED)
- **Date** - Submission date (formatted as "MMM dd, yyyy")

**Features:**

- Sorted by date descending (newest first)
- Limited to 10 entries for quick overview
- Displays total count below table
- Column resizing with constrained resize policy

### 4. Header Section

- **Welcome Message** - Displays "Welcome, [Name] (Citizen/Authority)"
- **Logout Button** - Red button positioned in top-right corner
- Gradient background (dark blue to darker blue)

### 5. Real-time Updates

- **Last Updated** label shows the time of last refresh
- **Refresh** button reloads all statistics and complaint data
- Automatic data loading on dashboard initialization

## File Structure

### FXML (Dashboard.fxml)

**Location:** `src/main/resources/fxml/Dashboard.fxml`

**Key Components:**

- BorderPane layout (700x1200)
- Top: Header with welcome message and logout
- Center: ScrollPane containing:
  - Statistics GridPane (4 cards)
  - Quick Actions HBox (3 buttons)
  - Recent Complaints TableView

**JavaFX Imports:**

```xml
BorderPane, VBox, HBox, GridPane
Label, Button, ScrollPane
TableView, TableColumn
Insets, Font
```

### Controller (DashboardController.java)

**Location:** `src/main/java/com/complaintmanagement/controller/DashboardController.java`

**Key Methods:**

#### `initializeUser(String userType, Long userId, String userName)`

- Called from LoginController
- Sets user context for dashboard
- Triggers data loading

#### `loadDashboardData()`

- Orchestrates loading of statistics and complaints
- Updates "last updated" timestamp

#### `loadComplaintStatistics()`

- Executes 4 JPA queries for complaint counts
- Updates statistic labels
- Handles errors gracefully (sets to "0")

#### `loadRecentComplaints()`

- Queries 10 most recent complaints
- Converts Complaint entities to ComplaintRow DTOs
- Populates TableView
- Updates table info label

#### `handleRefresh(ActionEvent event)`

- Reloads all dashboard data
- Updates last updated timestamp

#### `handleRegisterComplaint(ActionEvent event)`

- Navigates to complaint registration page
- Passes user context

#### `handleViewComplaints(ActionEvent event)`

- Placeholder for full complaint list view (TODO)

#### `handleLogout(ActionEvent event)`

- Closes EntityManagerFactory
- Returns to login page

**Inner Class - ComplaintRow:**

```java
public static class ComplaintRow {
    private String complaintId;
    private String title;
    private String type;
    private String department;
    private String status;
    private String date;
    // ... getters
}
```

### CSS (dashboard.css)

**Location:** `src/main/resources/css/dashboard.css`

**Style Classes:**

#### Layout

- `.dashboard-root` - Light gray background (#f5f7fa)
- `.dashboard-header` - Dark gradient with shadow
- `.content-scroll` - Transparent scroll pane
- `.dashboard-content` - Main content container

#### Statistics Cards

- `.stat-card` - Base card styling (rounded, shadow, hover effect)
- `.stat-card-primary` - Purple gradient (Total)
- `.stat-card-warning` - Pink gradient (Pending)
- `.stat-card-info` - Blue gradient (In Progress)
- `.stat-card-success` - Green gradient (Resolved)
- `.stat-label`, `.stat-value`, `.stat-sublabel` - Text styling

#### Action Buttons

- `.action-button` - Base button styling
- `.action-button-primary` - Green gradient
- `.action-button-secondary` - Blue gradient
- `.action-button-outline` - White with border

#### Table

- `.complaints-table` - White background, shadow
- `.column-header` - Dark header (#34495e, white text)
- `.table-row-cell` - Alternating row colors
- `.table-row-cell:hover` - Light blue on hover
- `.table-row-cell:selected` - Blue selection highlight

#### Header

- `.header-logout-btn` - Red logout button with hover effects
- `.header-title`, `.header-subtitle` - Welcome text styling

#### Utilities

- `.section-title` - Bold dark section headers
- `.last-updated`, `.table-info` - Small gray text
- Scrollbar styling for smooth appearance

## Database Integration

### JPA Queries Used

**Statistics Queries:**

```java
// Total
SELECT COUNT(c) FROM Complaint c
WHERE c.citizen.citizen_id = :citizenId

// Pending
SELECT COUNT(c) FROM Complaint c
WHERE c.citizen.citizen_id = :citizenId
AND (c.status = :unread OR c.status = :read)

// In Progress
SELECT COUNT(c) FROM Complaint c
WHERE c.citizen.citizen_id = :citizenId
AND c.status = :inProgress

// Resolved
SELECT COUNT(c) FROM Complaint c
WHERE c.citizen.citizen_id = :citizenId
AND c.status = :closed
```

**Recent Complaints Query:**

```java
SELECT c FROM Complaint c
WHERE c.citizen.citizen_id = :citizenId
ORDER BY c.complaint_date DESC
LIMIT 10
```

### Entity Relationships

- **Complaint → Citizen** (ManyToOne) - Filter complaints by logged-in citizen
- **Complaint → Department** (ManyToOne) - Display department name
- **Complaint.resolve_status** - Enum for status filtering

## User Workflow

### Login Flow

1. User logs in with credentials
2. LoginController validates credentials
3. On success, loads Dashboard.fxml
4. Calls `initializeUser()` with userId, userName, userType
5. Dashboard loads statistics and recent complaints

### Navigation Flow

```
Login → Dashboard → Register Complaint → Dashboard (refresh)
         ↓
      Logout → Login
```

### Refresh Flow

1. User clicks Refresh button
2. `handleRefresh()` triggered
3. `loadDashboardData()` re-executes queries
4. Statistics cards update
5. Table refreshes with latest data
6. "Last updated" timestamp updates

## UI Design Principles

### Color Scheme

- **Primary Background:** #f5f7fa (Light gray)
- **Header:** #2c3e50 → #34495e (Dark blue gradient)
- **Cards:** Vibrant gradients (purple, pink, blue, green)
- **Buttons:** Green (primary), Blue (secondary), Red (logout)

### Typography

- **Headers:** Arial Bold, 18-24px
- **Stats:** Arial Bold, 32px
- **Body:** Arial, 13-14px
- **Small text:** 11px

### Spacing

- Card padding: 20px
- Section spacing: 25px vertical
- Button spacing: 15px horizontal
- Grid gaps: 20px

### Hover Effects

- Cards: Scale 1.02, deeper shadow
- Buttons: Scale 1.03-1.05, deeper shadow
- Table rows: Light blue background

### Shadows

- Cards: `dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3)`
- Buttons: `dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3)`
- Header: `dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3)`

## Testing

### Test Scenarios

1. **Login as Test User**

   - Credentials: test/test
   - Verify welcome message displays correctly

2. **Verify Statistics**

   - Check that all 4 cards display counts
   - Submit new complaint
   - Refresh dashboard
   - Verify Total count increments
   - Verify Pending count increments

3. **Check Recent Complaints Table**

   - Verify 10 most recent complaints shown
   - Check columns: ID, Title, Type, Department, Status, Date
   - Verify date formatting (MMM dd, yyyy)
   - Test table scrolling

4. **Test Quick Actions**

   - Click "Register New Complaint" → Verify navigation
   - Return to dashboard → Click "Refresh"
   - Verify data updates and timestamp changes

5. **Test Logout**
   - Click logout button
   - Verify return to login page
   - Verify EntityManagerFactory closed

## Known Limitations

1. **View All Complaints** - Not yet implemented (placeholder)
2. **Pagination** - Recent complaints limited to 10 (no pagination)
3. **Sorting** - Table sorting not implemented
4. **Filtering** - No filter options on dashboard
5. **Search** - No search functionality

## Future Enhancements

### Planned Features

- [ ] Implement View All Complaints page with pagination
- [ ] Add filtering by status, date range, department
- [ ] Implement table column sorting
- [ ] Add search functionality
- [ ] Show complaint details on table row double-click
- [ ] Add charts/graphs for visual statistics
- [ ] Implement real-time notifications for status changes
- [ ] Add export to PDF/Excel functionality

### Performance Optimizations

- [ ] Implement caching for statistics
- [ ] Lazy loading for large complaint lists
- [ ] Connection pooling configuration
- [ ] Query optimization with indexes

## Troubleshooting

### Common Issues

**Issue:** Statistics show 0 even with complaints

- **Cause:** User has no complaints in database
- **Solution:** Use database_population.sql to insert sample data

**Issue:** Table is empty

- **Cause:** Query returning no results
- **Solution:** Check userId is correctly passed from login

**Issue:** Date format error

- **Cause:** complaint_date is null or invalid
- **Solution:** Ensure all complaints have valid dates in DB

**Issue:** CSS not applied

- **Cause:** Stylesheet path incorrect
- **Solution:** Verify `@../css/dashboard.css` path in FXML

**Issue:** EntityManagerFactory error

- **Cause:** Persistence unit not found
- **Solution:** Check persistence.xml has "complaint-management-pu"

## Dependencies

### JavaFX

- javafx.controls (Button, Label, TableView)
- javafx.fxml (FXMLLoader)
- javafx.scene (Scene, Parent)

### JPA/Hibernate

- jakarta.persistence (EntityManager, TypedQuery)
- hibernate-core 6.5.2.Final

### Java Standard Library

- java.util.Date
- java.text.SimpleDateFormat
- javafx.collections.ObservableList

## Conclusion

The enhanced dashboard provides a modern, user-friendly interface for citizens to view their complaint statistics and recent submissions. The implementation follows JavaFX best practices with proper separation of concerns (MVC pattern), efficient database queries, and responsive design principles.
