
# DoctortSab

DoctortSab is a web application designed to provide users with medical assistance through features such as symptom checking, first aid guidance, and video consultations with doctors. The application allows users to book appointments and access various health-related services.

## Features

- **Symptom Check**: Users can input their symptoms and receive potential diagnoses and recommendations.
- **First Aid**: Access to first aid information for common medical emergencies.
- **Video Consultation**: Users can book appointments for video consultations with doctors.
- **User Authentication**: Secure login and signup functionality using Google Authentication.
- **Admin Dashboard**: Admins can manage users, view analytics, and monitor system usage.
- **Doctor Dashboard**: Doctors can view their appointments and join video calls with patients.

## Technologies Used

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (Spring Boot)
- **Database**: Firebase Firestore
- **Authentication**: Google Authentication
- **Real-time Communication**: WebRTC

## Project Structure

```
DoctortSab
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── doctortsab
│   │   │           ├── controller
│   │   │           ├── model
│   │   │           ├── service
│   │   │           └── util
│   │   └── resources
│   │       ├── application.properties
│   │       └── static
│   │           ├── css
│   │           │   └── style.txt
│   │           └── index.txt
│   │       └── templates
│   │           ├── home.html
│   │           ├── login.html
│   │           ├── dashboard
│   │           │   ├── user.html
│   │           │   ├── doctor.html
│   │           │   └── admin.html
│   │           └── appointment.html
├── pom.xml
└── README.md
```

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd DoctortSab
   ```

3. Install dependencies using Maven:
   ```
   mvn install
   ```

4. Configure Firebase settings in `src/main/resources/application.properties`.

5. Run the application:
   ```
   mvn spring-boot:run
   ```

6. Access the application at `http://localhost:8080`.

## Usage

- Users can log in or sign up on the home page.
- After logging in, users can access the symptom check, first aid information, and book appointments.
- Doctors can manage their appointments and join video consultations.
- Admins can view user analytics and manage the system.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.#
