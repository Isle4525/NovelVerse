# NovelVerse Presentation

## Slide 1. Title

**NovelVerse**
Java web application for reading and managing ranobe

- Student project for OOP final assessment
- Stack: Java, Spring Boot, PostgreSQL, HTML/CSS/JavaScript
- Author: `__________`
- Group: `__________`

Speaker notes:
This project is called NovelVerse. It is a Java-based web application for reading ranobe, user authentication, admin management, and importing content.

---

## Slide 2. Project Goal

**Project goal**

- Create a convenient platform for reading ranobe online
- Support user registration and authorization
- Add an admin panel for managing novels
- Store data in a database
- Demonstrate OOP and core course topics in one project

Speaker notes:
The main goal was to build a complete Java project with both user and admin functionality, while also showing the topics studied in the course.

---

## Slide 3. Main Features

**Implemented functionality**

- Registration and login
- Login by email or username
- User profile editing
- Avatar upload and preview
- Admin panel
- Manual novel creation
- TXT import for chapters
- Semi-automatic import from external link
- Real-time notifications

Speaker notes:
The application already supports a real workflow: users can register and read content, while the admin can create and import new ranobe.

---

## Slide 4. Technologies

**Technologies used**

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- HTML, Tailwind CSS, JavaScript
- WebSocket
- Maven

Speaker notes:
The backend is written in Java using Spring Boot. Security uses JWT tokens. Data is stored in PostgreSQL, and the frontend is built with static HTML, Tailwind, and JavaScript.

---

## Slide 5. System Structure

**Architecture**

- Frontend pages: `index.html`, `login.html`, `profile.html`, `admin.html`
- REST API on Spring Boot
- Database for users, novels, and chapters
- File storage for avatars
- WebSocket for live updates

Speaker notes:
The system has a simple client-server architecture. The browser communicates with the backend through REST endpoints, and live notifications are sent through WebSocket.

---

## Slide 6. OOP In The Project

**OOP concepts**

- Classes and objects: `User`, `Novel`, `Chapter`
- Encapsulation: entity fields and service logic
- Abstraction: service layer between controller and database
- Interfaces: `NotificationService`
- Polymorphism: different service implementations can be used through one interface

Speaker notes:
The project demonstrates the basic OOP principles. Entities represent objects, services contain business logic, and interfaces help separate behavior from implementation.

---

## Slide 7. Database

**Database usage**

- Users are stored in the database
- Novels are stored in the database
- Chapters are stored in the database
- User profile data is persistent
- Imported novels are saved permanently

Speaker notes:
Database integration is one of the core parts of the project. All important application data is saved and reused after restart.

---

## Slide 8. File Reading And Writing

**Work with files**

- Avatar images can be uploaded by users
- TXT files can be imported by admin
- TXT content is parsed into chapters automatically
- Uploaded files are processed and stored by the backend

Speaker notes:
This part covers file reading and writing from the syllabus. The project reads uploaded files and uses them to create application content.

---

## Slide 9. Threads And Async Work

**Threads**

- Email sending is asynchronous
- Background processing improves responsiveness
- The main request is not blocked while email is being sent

Speaker notes:
Threads are demonstrated through asynchronous email sending. This shows background execution in a real application scenario.

---

## Slide 10. Socket Programming

**Socket programming**

- WebSocket connection is used
- Clients receive live notifications
- Example: when a new novel is added, connected users see an update immediately

Speaker notes:
This project includes socket programming through WebSocket. It allows real-time communication between the server and the browser.

---

## Slide 11. Admin Panel

**Admin functionality**

- Only admin can create novels
- Manual creation of ranobe
- TXT import for automatic chapter creation
- Semi-automatic import from external URL

Speaker notes:
The admin panel is important because it turns the project from a simple viewer into a manageable content platform.

---

## Slide 12. User Profile

**User profile**

- View personal information
- Edit username and email
- Change password
- Upload custom avatar

Speaker notes:
The profile system makes the application more complete and improves the user experience.

---

## Slide 13. Project Strengths

**Why this project is strong**

- Real full-stack Java application
- Covers multiple syllabus topics
- Has both user and admin roles
- Uses database, files, sockets, and async logic
- Can be extended in the future

Speaker notes:
The project is not just theoretical. It solves a practical problem and includes several important technologies together in one system.

---

## Slide 14. Possible Improvements

**Future improvements**

- Better external import sources
- More accurate metadata parsing
- Chapter editor in admin panel
- Comments and reviews
- Better search and filtering

Speaker notes:
The project already works, but it can still be improved. These ideas show that the architecture allows further development.

---

## Slide 15. Conclusion

**Conclusion**

- NovelVerse is a working Java project
- It demonstrates OOP, GUI, database, files, threads, and sockets
- The application has practical value and can be expanded further

**Thank you for your attention**

Speaker notes:
In conclusion, the project meets the main educational goals and demonstrates both programming concepts and practical implementation.
