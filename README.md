 🎓 College Timetable Generator

A Java desktop application that automatically generates weekly class timetables for multiple sections, built with JavaFX GUI and MySQL database connectivity.

Built for **24CSP012 — Java Programming** | Kumaraguru College of Technology | Slot A4 | KCT01823

---

<img width="2204" height="1422" alt="Screenshot 2026-04-29 003936" src="https://github.com/user-attachments/assets/4e59e494-db2e-491d-9ab8-df6c39690842" />
<img width="2192" height="1420" alt="Screenshot 2026-04-29 003955" src="https://github.com/user-attachments/assets/3fffdccf-5daa-49aa-b10b-b1ae9aa22f8b" />
<img width="2195" height="1398" alt="Screenshot 2026-04-29 004011" src="https://github.com/user-attachments/assets/4dce4456-6ec8-4016-844c-83e6406a2652" />
<img width="2879" height="1713" alt="Screenshot 2026-04-29 004048" src="https://github.com/user-attachments/assets/c0c10a04-21ab-48dd-9ce6-2c40612bdcc0" />


---

✨ Features

- 📚 Add subjects with credits and lab components
- 👨‍🏫 Assign staff members to subjects
- ⚙️ Auto-generate timetables for multiple sections (A, B, C...)
- 🔵 Colour-coded timetable cells — blue for theory, green for LAB-1, yellow for LAB-2
- 💾 All data persisted to MySQL database via JDBC
- 🔄 Data reloads from MySQL on every app launch
- 🖥️ Modern JavaFX GUI with sidebar navigation and dashboard

---

🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| GUI | JavaFX 21 |
| Database | MySQL 8.0 |
| DB Connectivity | JDBC (mysql-connector-j 8.0.33) |
| Build Tool | Apache Maven |
| IDE | VS Code |

---

📁 Project Structure

```
src/main/java/com/timetable/
├── gui/
│   └── MainApp.java           # JavaFX application — all GUI pages
├── db/
│   └── DatabaseManager.java   # JDBC connectivity — all SQL operations
├── generator/
│   └── TimetableGenerator.java # Two-pass scheduling algorithm
├── input/
│   └── InputCollector.java    # Terminal-based input (legacy)
├── model/
│   ├── Subject.java           # Subject data class
│   ├── Staff.java             # Staff data class
│   ├── SemesterConfig.java    # Semester settings
│   └── TimetableEntry.java    # One timetable cell
└── Main.java                  # Entry point
```

---

🗄️ Database Schema

```sql
CREATE DATABASE timetable_db;

CREATE TABLE subjects (
    code     VARCHAR(20) PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    credits  INT NOT NULL,
    has_lab  TINYINT(1) NOT NULL
);

CREATE TABLE staff (
    id           VARCHAR(10) PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);

CREATE TABLE timetable_entries (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    section      VARCHAR(20) NOT NULL,
    day_index    INT NOT NULL,
    period_index INT NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    staff_id     VARCHAR(10),
    is_lab       TINYINT(1) NOT NULL,
    is_lab_part2 TINYINT(1) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);
```

---

⚙️ Prerequisites

Make sure you have these installed:

- Java JDK 17 or higher → [Download](https://www.oracle.com/java/technologies/downloads/)
- Apache Maven → [Download](https://maven.apache.org/download.cgi)
- MySQL Server 8.0 → [Download](https://dev.mysql.com/downloads/installer/)
- MySQL Workbench (optional but recommended)

Check installations:
```bash
java -version
mvn -version
mysql --version
```

---

🚀 Setup and Run

1. Clone the repository
```bash
git clone https://github.com/yourusername/timetable-generator.git
cd timetable-generator
```

2. Set up the MySQL database
Open MySQL Workbench or MySQL Shell and run:
```sql
CREATE DATABASE timetable_db;
USE timetable_db;

CREATE TABLE subjects (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    has_lab TINYINT(1) NOT NULL
);

CREATE TABLE staff (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);

CREATE TABLE timetable_entries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    section VARCHAR(20) NOT NULL,
    day_index INT NOT NULL,
    period_index INT NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    staff_id VARCHAR(10),
    is_lab TINYINT(1) NOT NULL,
    is_lab_part2 TINYINT(1) NOT NULL,
    FOREIGN KEY (subject_code) REFERENCES subjects(code)
);

CREATE USER 'timetable_user'@'localhost' IDENTIFIED BY 'timetable123';
GRANT ALL PRIVILEGES ON timetable_db.* TO 'timetable_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Build the project
```bash
mvn clean compile
```

4. Run the application
```bash
mvn javafx:run
```

---

🖥️ How to Use

1. **Subjects tab** — Enter subject code, name, credits, and check if it has a lab. Click Add Subject. Each subject is saved to MySQL instantly.

2. **Staff tab** — Enter staff name, select their subject from the dropdown, click Add Staff.

3. **Generate tab** — Set periods per day, working weeks, and number of sections. Click Generate. The timetable appears with colour-coded cells and all entries are saved to MySQL.

---

🎨 GUI Concepts Used

| Concept | JavaFX Component |
|---|---|
| Layout | VBox, HBox, StackPane |
| Navigation | Sidebar with styled Buttons |
| Data display | TableView, TableColumn |
| Cell colours | Custom TableCell factory |
| User input | TextField, CheckBox, ComboBox |
| Data binding | SimpleStringProperty, ObservableList |
| Feedback | Label (green/red status messages) |
| Scrolling | ScrollPane |
| Error dialogs | Alert |
| Dashboard cards | Styled VBox containers |

---

🗃️ Database Operations (JDBC)

| Operation | Method | SQL |
|---|---|---|
| INSERT | saveSubject() | INSERT ... ON DUPLICATE KEY UPDATE |
| INSERT | saveStaff() | INSERT INTO staff ... |
| INSERT | saveTimetableEntry() | INSERT INTO timetable_entries ... |
| SELECT | loadAllSubjects() | SELECT * FROM subjects |
| SELECT | loadAllStaff() | SELECT * FROM staff |
| DELETE | clearTimetableForSection() | DELETE FROM timetable_entries WHERE section=? |

---

📊 Scheduling Algorithm

The `TimetableGenerator` uses a **two-pass greedy algorithm**:

**Pass 1 — Labs first:** Scans each day for two consecutive empty slots and places the lab subject there (LAB-1 and LAB-2). Labs are placed first because they are harder to fit.

**Pass 2 — Theory:** Collects all remaining empty slots, shuffles them using a seed derived from the subject code (for reproducibility), and fills them with theory hours.

---

📋 pom.xml Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21</version>
    </dependency>
</dependencies>
```

---

📄 License

This project is submitted as academic coursework for KCT. Not for redistribution.
```

---
