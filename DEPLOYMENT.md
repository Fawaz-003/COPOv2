# COPO Spring Boot Application - Vercel Deployment

## Overview
This is a Spring Boot application for Course Outcome Process Outcome (COPO) management, configured for deployment on Vercel.

## Prerequisites
- Java 17
- Maven 3.6+
- MySQL Database (external service like PlanetScale, Railway, or AWS RDS)

## Environment Variables Required
Set these in your Vercel dashboard:

```
DATABASE_URL=jdbc:mysql://your-database-host:3306/copo3
DB_USERNAME=your-database-username
DB_PASSWORD=your-database-password
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your-admin-password
PORT=9091
```

## Database Setup
1. Create a MySQL database named `copo3`
2. Import the SQL files from `SQL Dump/` directory
3. Update the `DATABASE_URL` environment variable with your database connection string

## Local Development
```bash
# Run with Docker Compose
docker compose up -d

# Or run directly with Maven
mvn spring-boot:run
```

## Deployment to Vercel
1. Install Vercel CLI: `npm i -g vercel`
2. Login to Vercel: `vercel login`
3. Deploy: `vercel --prod`

## Important Notes
- Vercel has limitations for Spring Boot applications (10-second timeout for serverless functions)
- Consider using Railway, Heroku, or AWS for better Spring Boot support
- Database must be external (not local MySQL)

## Alternative Deployment Options
For better Spring Boot support, consider:
- **Railway**: `railway deploy`
- **Heroku**: `git push heroku main`
- **AWS Elastic Beanstalk**: Upload JAR file
- **Google Cloud Run**: Container deployment

