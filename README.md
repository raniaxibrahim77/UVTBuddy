UVTBuddy

UVTBuddy is a full-stack integration application designed for university students.
It centralizes essential UVT-related information and academic events, while also helping students connect and collaborate through study posts and study sessions.

The application aims to simplify student life by providing quick access to useful university content and by encouraging peer-to-peer learning and interaction.

Tech Stack
- Backend: Java, Spring Boot, Spring Security, Gradle
- Frontend: Angular
- Database: MySQL (production), H2 (testing)

Main Features
- User authentication and role-based authorization
- UVT-related events and academic information
- Study posts (questions, advice, notes)
- Study session creation and participation
- File uploads for posts
- RESTful API with CRUD operations
- Secure backend endpoints using Spring Security

How to Run the Backend
1. Make sure Java 17+ is installed
2. In the project root, run:
   ./gradlew bootRun
3. Backend runs at:
   http://localhost:8080

How to Run the Frontend
1. Navigate to the frontend folder:
   cd frontend
2. Install dependencies:
   npm install
3. Start the Angular application:
   ng serve --proxy-config proxy.conf.json
4. Frontend runs at:
   http://localhost:4200

Database
- MySQL is used for the main application data
- H2 in-memory database is used only for testing purposes

Security
- Authentication and authorization are handled using Spring Security
- Access to backend endpoints is restricted based on user roles

Author
Ibrahim Rania


