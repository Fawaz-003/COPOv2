# Faculty Password Setup Guide - Default Password: 5106

## 🎯 Objective
Set default password "5106" for all faculty members using BCrypt hashing.

## 🔧 Implementation Options

### Option 1: Using Java Application (Recommended)
1. **Start the application**: `.\mvnw.cmd spring-boot:run`
2. **Initialize passwords**: Visit `http://localhost:9091/faculty/init-passwords`
3. **Verify**: Check faculty list to see password status

### Option 2: Direct Database Update
1. **Execute SQL script**: `SOURCE update_faculty_passwords.sql;`
2. **Verify**: Check the faculty table

## 📋 Step-by-Step Instructions

### Step 1: Ensure Application is Running
```bash
cd "D:\Spring Boot - COPO\COPOv2"
.\mvnw.cmd spring-boot:run
```

### Step 2: Initialize Default Passwords
**Method A - Web Interface:**
1. Go to: `http://localhost:9091/faculty/init-passwords`
2. This will set password "5106" for all faculty without passwords

**Method B - Direct Database:**
```sql
-- Connect to your MySQL database
USE copo3;

-- Execute the update script
SOURCE update_faculty_passwords.sql;
```

### Step 3: Verify Password Setup
```sql
-- Check which faculty have passwords
SELECT id, facultycode, name, 
       CASE 
           WHEN password IS NOT NULL AND password != '' THEN 'Password Set'
           ELSE 'No Password'
       END as password_status
FROM faculty;

-- Count faculty with passwords
SELECT COUNT(*) as total_faculty,
       COUNT(CASE WHEN password IS NOT NULL AND password != '' THEN 1 END) as faculty_with_passwords
FROM faculty;
```

### Step 4: Test Faculty Login
1. **Go to**: `http://localhost:9091/login`
2. **Click**: Faculty tab
3. **Enter**:
   - Faculty Code: (any existing faculty code)
   - Password: `5106`
4. **Click**: Login as Faculty

## 🔐 Security Details

### BCrypt Hash for "5106"
The password "5106" will be hashed using BCrypt with:
- **Algorithm**: BCrypt
- **Strength**: 10 rounds (default)
- **Salt**: Automatically generated
- **Example Hash**: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`

### Password Verification
- Passwords are never stored in plain text
- BCrypt automatically handles salt generation
- Secure comparison prevents timing attacks

## 🛠️ Available Endpoints

### Password Management
- `GET /faculty/init-passwords` - Set default password for all faculty
- `GET /faculty/reset-password/{id}` - Reset specific faculty password to default
- `POST /faculty` - Create new faculty with password
- `POST /faculty/edit/{id}` - Update faculty (password optional)

### Authentication
- `POST /login/faculties` - Faculty login with code + password

## 📊 Expected Results

After running the initialization:
- ✅ All faculty will have password "5106" (BCrypt hashed)
- ✅ Faculty can login with: Faculty Code + Password "5106"
- ✅ Passwords are securely stored in database
- ✅ No plain text passwords in database

## 🔍 Troubleshooting

### If Login Still Fails:
1. **Check database**: Verify password column exists
2. **Verify hash**: Check if password field has BCrypt hash
3. **Check application logs**: Look for authentication errors
4. **Test specific faculty**: Try resetting individual faculty password

### Reset Individual Faculty Password:
```
http://localhost:9091/faculty/reset-password/{faculty_id}
```

## 📝 Notes

- Default password is "5106" for all faculty
- Passwords are automatically BCrypt hashed
- Faculty can change passwords through edit form
- System maintains backward compatibility
- All authentication is now password-based only





