# Exam-Management-System-A-Java-Application-for-Automated-Multiple-Choice-Test-Administration
# Exam Management System

This project is a simple **Exam Management System** built with **Java** and **MySQL** for automated multiple-choice test (MCQ) administration. All features are implemented within a single Java file.

## Features

- **User Authentication**: Admin, professor, and student login system.
- **Admin Interface**: Manage users (students, professors) and exams.
- **Professor Interface**: Create and manage multiple-choice questions (MCQs) and exams.
- **Student Interface**: Take exams and view results.
- **Database**: MySQL database for storing user data, questions, and exam results.

## Technologies Used

- **Java**: Application logic and user interfaces.
- **MySQL**: Database management via phpMyAdmin.
- **JDBC**: Java Database Connectivity for connecting Java with MySQL.

## Project Structure

The entire project is contained in a single file:  
`ExamManagementSystem.java`

The project includes the following features:
- Database connection handling
- User authentication (Admin, Professor, Student)
- Admin functionalities (managing users, creating exams)
- Professor functionalities (creating and managing MCQs)
- Student functionalities (taking exams and viewing results)

## Setup Instructions

### Prerequisites

1. **Java**: Ensure Java 8 or later is installed on your machine.
2. **MySQL**: You need a running MySQL instance with phpMyAdmin to manage the database.
3. **JDBC Driver**: Make sure you have the MySQL JDBC driver configured in your project.

### Database Setup

1. Open phpMyAdmin and create a new database (e.g., `exam_management`).
2. Import the SQL script (`qcm.sql`) into phpMyAdmin to create the necessary tables.

   - In phpMyAdmin, go to the database you created.
   - Navigate to the **SQL** tab.
   - Paste the content of `qcm.sql` and execute the query.

### Running the Application

1. Open the `ExamManagementSystem.java` file and run it from your IDE or terminal.
2. Follow the prompts to log in as an admin, professor, or student.
   - Admins can manage users and exams.
   - Professors can create MCQs and exams.
   - Students can take exams and view results.

### Database Configuration

In the `ExamManagementSystem.java` file, configure the connection details for your MySQL database:

```java
// Example of setting up a connection in the Java file
String url = "jdbc:mysql://localhost:3306/exam_management";
String username = "root";
String password = "";
