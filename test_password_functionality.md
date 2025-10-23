# Faculty Password Functionality Test Guide

## Implementation Summary

✅ **Completed Features:**

1. **Database Schema Update:**
   - Added `password` column to `faculty` table
   - Column type: VARCHAR(255) for BCrypt hashed passwords

2. **Backend Implementation:**
   - Added `PasswordService` with BCrypt hashing
   - Updated `Faculty` model with password field
   - Enhanced `FacultyService` with password hashing in save/update operations
   - Added credential verification methods
   - Updated `LoginService` with password-based authentication

3. **Frontend Updates:**
   - Added password field to faculty create form
   - Added password field to faculty edit form (optional for updates)
   - Updated login form with password field for faculty

4. **Security Features:**
   - BCrypt password hashing (industry standard)
   - Secure password storage
   - Backward compatibility with name-based authentication

## Testing Steps

### 1. Database Setup
```sql
-- Run the SQL script to add password column
SOURCE add_password_column_faculty.sql;

-- Verify the column was added
SELECT * FROM faculty LIMIT 5;
```

### 2. Create Faculty with Password
1. Go to: `http://localhost:9091/faculty/new`
2. Fill in:
   - Faculty Code: TEST001
   - Name: Test Faculty
   - Designation: Professor
   - Password: test123
   - Department: Select any department
3. Click "Save"
4. Password will be automatically hashed using BCrypt

### 3. Test Login with Password
1. Go to: `http://localhost:9091/login`
2. Click "Faculty" tab
3. Enter:
   - Faculty Name: Test Faculty (optional)
   - Faculty Code: TEST001
   - Password: test123
4. Click "Login as Faculty"
5. Should redirect to faculty dashboard

### 4. Test Login without Password (Legacy)
1. Go to: `http://localhost:9091/login`
2. Click "Faculty" tab
3. Enter:
   - Faculty Name: Test Faculty
   - Faculty Code: TEST001
   - Password: (leave blank)
4. Click "Login as Faculty"
5. Should work with name-based authentication

### 5. Update Faculty Password
1. Go to: `http://localhost:9091/faculty/edit/{id}`
2. Enter new password in password field
3. Click "Update"
4. Password will be hashed and updated

## Security Features

- ✅ BCrypt hashing with salt
- ✅ Secure password storage
- ✅ Backward compatibility
- ✅ Password verification
- ✅ Optional password updates

## API Endpoints

- `POST /faculty` - Create faculty with password
- `POST /faculty/edit/{id}` - Update faculty (password optional)
- `POST /login/faculties` - Login with password or name
- `GET /faculty` - List all faculty
- `GET /faculty/new` - Create faculty form
- `GET /faculty/edit/{id}` - Edit faculty form

## Password Requirements

- Passwords are automatically hashed using BCrypt
- Minimum security with salt and rounds
- Passwords are never stored in plain text
- Verification uses secure comparison

