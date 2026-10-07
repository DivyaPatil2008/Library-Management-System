CREATE DATABASE IF NOT EXISTS library_db;

USE library_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT INTO users (name, password, role)
VALUES
('admin', 'admin123', 'ADMIN'),
('divya', 'divya123', 'USER'),
('mahi', 'mahi123', 'USER');


SELECT * FROM users;
USE library_db;

CREATE TABLE books (
    book_id INT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO books (book_id, title, author, available)
VALUES
(101, 'Java Programming', 'James Gosling', TRUE),
(102, 'Database Management System', 'Raghu Ramakrishnan', TRUE),
(103, 'Data Structures', 'Seymour Lipschutz', TRUE),
(104, 'Computer Networks', 'Andrew Tanenbaum', TRUE),
(105, 'Operating System', 'Galvin', TRUE);

SELECT * FROM books;
USE library_db;
USE library_db;

CREATE TABLE issue_records (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(10,2) DEFAULT 0.00,

    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
SHOW TABLES;


INSERT INTO issue_records
(book_id, user_id, issue_date, due_date, return_date, fine)
VALUES
(101, 2, '2026-10-04', '2026-10-18', NULL, 0.00);
SELECT * FROM issue_records;

SELECT
    ir.record_id,
    u.name AS user_name,
    b.title AS book_title,
    ir.issue_date,
    ir.due_date,
    ir.return_date,
    ir.fine
FROM issue_records ir
JOIN users u ON ir.user_id = u.user_id
JOIN books b ON ir.book_id = b.book_id;


UPDATE books
SET available = FALSE
WHERE book_id = 101;

SELECT * FROM books;

DESCRIBE issue_records;
USE library_db;

SELECT * FROM users;