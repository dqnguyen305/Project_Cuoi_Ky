CREATE TABLE admin (
           id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
           username VARCHAR(50) NOT NULL UNIQUE,
           password VARCHAR(255) NOT NULL
);

CREATE TABLE student (
                         id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         dob DATE NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         sex BOOLEAN NOT NULL,
                         phone VARCHAR(20) NULL,
                         password VARCHAR(255) NOT NULL,
                         create_at DATE DEFAULT CURRENT_DATE
);

CREATE TABLE course (
                        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        duration INT NOT NULL,
                        instructor VARCHAR(100) NOT NULL,
                        create_at DATE DEFAULT CURRENT_DATE
);

CREATE TABLE enrollment (
                            id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            student_id INT NOT NULL,
                            course_id INT NOT NULL,
                            registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            status VARCHAR(20) DEFAULT 'WAITING',

                            CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                            CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                            CONSTRAINT chk_enrollment_status CHECK (status IN ('WAITING', 'DENIED', 'CANCEL', 'CONFIRMED'))
);

INSERT INTO admin (username, password) VALUES ('admin', 'admin123');

INSERT INTO student (name, dob, email, sex, phone, password)
VALUES ('Nguyễn Quốc Văn', '2004-05-30', 'hocvien@gmail.com', true, '0987654321', 'student123');

INSERT INTO course (name, duration, instructor) VALUES ('Lập trình Java Core', 60, 'Thầy Nguyễn Văn A');
INSERT INTO course (name, duration, instructor) VALUES ('Lập trình Web với ReactJS', 45, 'Cô Trần Thị B');