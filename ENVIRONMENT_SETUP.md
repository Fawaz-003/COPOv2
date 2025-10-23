# Environment Variables Configuration for Vercel

## Required Environment Variables
Set these in your Vercel dashboard under Project Settings > Environment Variables:

### Database Configuration
```
DATABASE_URL=jdbc:mysql://your-database-host:3306/copo3
DB_USERNAME=your-database-username
DB_PASSWORD=your-database-password
```

### Application Configuration
```
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your-secure-admin-password
PORT=9091
SPRING_PROFILES_ACTIVE=vercel
```

## Database Setup Instructions

### Option 1: PlanetScale (Recommended - Free Tier)
1. Go to https://planetscale.com
2. Create a free account
3. Create a new database named `copo3`
4. Get the connection string from the dashboard
5. Update DATABASE_URL with your PlanetScale connection string

### Option 2: Railway (Free Tier)
1. Go to https://railway.app
2. Create a new project
3. Add MySQL database service
4. Get connection details from the service dashboard
5. Update environment variables accordingly

### Option 3: AWS RDS
1. Create MySQL RDS instance
2. Configure security groups
3. Get endpoint and credentials
4. Update environment variables

## Import Database Schema
After setting up your database, import the SQL files from:
- `SQL Dump/copo3/` directory
- Or use the individual table files in `SQL Dump/alltables/`

## Testing Database Connection
You can test your database connection by running the application locally with the same environment variables.

