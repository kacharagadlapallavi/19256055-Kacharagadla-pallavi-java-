-- Task 6: Campus Event Registration Database Schema
CREATE DATABASE IF NOT EXISTS campus_events;
USE campus_events;

CREATE TABLE IF NOT EXISTS event_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    reg_no VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Prevent duplicate registration for the same event
    CONSTRAINT uq_student_event UNIQUE (reg_no, event_name)
);

-- Seed initial test records
INSERT IGNORE INTO event_registrations (reg_no, student_name, department, event_name) VALUES
('192372374', 'Moksha Dude', 'Computer Science', 'Hackathon 2026'),
('192311001', 'Arun Kumar', 'Information Technology', 'RoboWars'),
('192311002', 'Divya R', 'Computer Science', 'Coding Sprint'),
('192311003', 'Karthik S', 'Electronics', 'Hackathon 2026');
