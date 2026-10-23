# 📚 Library Management System

A simple and user-friendly Library Management System built using **Java, MySQL, JDBC, HTML, CSS, and JavaScript**. This project helps manage library books, user accounts, and book issue and return operations through a web-based interface.

The system provides separate access for **Administrators and Users**, making library operations easier to manage and track.

---

## ✨ Features

### 🔐 Authentication & User Management

- Admin and User login.
- Role-based access to library features.
- Create an account and reset-password interface.
- Session-based authentication for protected operations.

### 📖 Book Management

- View all available books.
- Add new books to the library.
- Search for books by relevant details.
- Track book availability.
- Issue and return books.

### 👥 User Dashboard

- View books issued to the logged-in user.
- Track issue dates and due dates.
- View user-wise issued book records.

### 🛠️ Admin Dashboard

- View library statistics.
- Manage library book records.
- View users and their issued books.
- Monitor active issues and returned books.

### 🗄️ Database Management

- MySQL database integration using JDBC.
- Store user details and book information.
- Maintain issue and return records.
- Track book availability in the database.

---

## 🧰 Tech Stack

| Technology   | Purpose                                  |
| ------------ | ---------------------------------------- |
| Java         | Backend logic and server-side operations |
| MySQL        | Database management                      |
| JDBC         | Java–MySQL connectivity                  |
| HTML5        | Web page structure                       |
| CSS3         | Styling and responsive layout            |
| JavaScript   | Frontend interactions and API requests   |
| Git & GitHub | Version control and project hosting      |

---

## 📂 Project Structure

```text
Library-Management-System/
│
├── src/
│   ├── DBConnection.java
│   ├── Book.java
│   ├── LoginServer.java
│   ├── LibraryServer.java
│   ├── LibraryDashboard.java
│   └── LibraryManagementSystem.java
│
├── Frontend/
│   ├── index.html
│   ├── login.html
│   ├── create-account.html
│   ├── reset-password.html
│   ├── style.css
│   └── script.js
│
├── lib/
│   └── mysql-connector-j-9.7.0.jar
│
├── Library management system.sql
└── README.md
```

*Note: The structure above is based on the project files currently identified. Adjust filenames or folder capitalization if your actual repository differs.*

---

## ⚙️ Requirements

Before running the project, install:

- Java Development Kit (JDK)
- MySQL Server
- MySQL Workbench (recommended)
- Visual Studio Code or another Java IDE
- MySQL Connector/J
- Git (optional, for version control)

---

## 🚀 Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/DivyaPatil2008/Library-Management-System.git
cd Library-Management-System
```

### 2. Create the Database

1. Open MySQL Workbench.
2. Open the file `Library management system.sql`.
3. Execute the SQL script to create the database and required tables.
4. Verify that the database and tables were created successfully.

### 3. Configure the Database Connection

Open `src/DBConnection.java` and verify the database URL, username, and password.

Example configuration:

```java
String url = "jdbc:mysql://localhost:3306/library_db";
String username = "root";
String password = "YOUR_MYSQL_PASSWORD";
```

Use your own local MySQL credentials. **Never commit real passwords or other secrets to GitHub.**

### 4. Verify the MySQL Connector

Ensure the MySQL Connector/J `.jar` file is present inside the `lib/` folder.

### 5. Compile the Java Files

Open the terminal in the project root and run:

```powershell
javac -cp ".\lib\*" -d . src\*.java
```

### 6. Start the Library Server

```powershell
java -cp ".;.\lib\*" src.LibraryServer
```

Keep the terminal open while using the application.

### 7. Open the Application

Open your browser and visit:

```text
http://localhost:8080/login.html
```

Log in using an account configured in your local database.

---

## 🔄 Application Workflow

1. The user opens the login page.
2. The server validates the login credentials.
3. The application identifies the user's role.
4. The dashboard displays the relevant library information.
5. Users can browse books and perform permitted issue or return operations.
6. The system stores changes in the MySQL database.
7. Administrators can monitor book records and user-wise issue information.

---

## 🗃️ Database Overview

The database includes tables for the main operations of the library system.

| Table           | Purpose                                         |
| --------------- | ----------------------------------------------- |
| `users`         | Stores user account details and roles           |
| `books`         | Stores book details and availability            |
| `issue_records` | Tracks book issue dates, due dates, and returns |

The exact table definitions and relationships are available in the SQL file included in the repository.

---

## 🔒 Security Notes

- Use secure password-handling practices.
- Keep database credentials outside publicly committed source code.
- Validate user input on the server.
- Verify user roles and permissions for protected operations.
- Use parameterized SQL queries to reduce SQL injection risks.
- Store and validate sessions securely.

---

## 🧪 Testing Checklist

- [ ] Database connection works successfully.
- [ ] Admin and User login work correctly.
- [ ] Books display with the correct availability status.
- [ ] New books can be added.
- [ ] Book search returns relevant results.
- [ ] Issue operations create valid issue records.
- [ ] Return operations update the database correctly.
- [ ] Users see only their own issued books.
- [ ] Admin can view user-wise issued book records.
- [ ] Returned books no longer appear in active issue lists.

---

## 🎯 Project Objective

The main objective of this project is to simplify basic library operations by maintaining book details, user information, and issue and return records in a centralized database. It demonstrates the practical use of Java programming, JDBC, MySQL, and web technologies in an integrated application.

---

## 🔮 Future Enhancements

- Book reservation functionality.
- Automatic overdue notifications.
- Fine calculation and payment tracking.
- Advanced search and filtering.
- Improved password security and account recovery.
- Reports for issued, returned, and overdue books.
- Responsive design for mobile devices.

---

## 👩‍💻 Author

**Divya Patil**

Student Project — Library Management System

## 📄 License

This project is intended for educational and learning purposes. Add a formal license file if you plan to distribute or reuse the project publicly.

---

⭐ If you find this project useful, consider giving the repository a star!
