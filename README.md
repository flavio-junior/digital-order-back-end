# Documentation of Project | Digital Order

# Resources

- **Specifications:**
  - **Programming language used: ` Kotlin `**
  - **Kotlin Version: ` 1.9.25 `**
  - **JDK: ` 21 LTS `**
  - **Maven: ` 3.9.7 `**

- **Tools:**
  - **` Docker `**
  - **` IntelliJ IDEA Community Edition `**
  - **` Postman `**
  - **` Git `**
  - **` GitHub `**
  - **` PostgreSQL `**

- **Executing Project:**
  - **Clone this project to your machine using an IDE, by downloading the project, or via Git clone.**
  - **Open the IntelliJ IDEA and select the folder back-end to navigate in project.**
  - **Open Docker Desktop**
  - **Run the command: ` docker compose up -d ` to start the development environment on the machine.**
  - **Execute the ` fun main ` Class: ` Application ` to run the project.**
 
- **Environments:**
  - **Test**
  - **Dev**
  - **Prod**

- **Contact Me:**
  - **Name: ` Flávio Júnior `**
  - **Description: ` Author of Project `**
  - **Contact: ` flaviojunior.work@gmail.com `**

# RoadMap
![Image](https://github.com/user-attachments/assets/4944d1b0-5a5c-4891-a189-f0533aaefaaa)

# Commands
### Commands of Docker:
- **Create and run service with definitions of docker-compose.yml: ` docker compose up -d --build `**
- **Change environment:**
  - **Test: ` docker-compose --env-file .env.test up --build `**
  - **Dev: ` docker-compose --env-file .env.dev up --build `**
  - **Prod: ` docker-compose --env-file .env.prod up --build -d `** 

### Commands to generete file .jar:
- **Generate jar: ` mvn clean package `**
- **Generate jar and skip tests: ` mvn clean package -DskipTests `**
- **Run project: ` java -jar target/digital-order-0.0.1-SNAPSHOT.jar.jar `**
- **Stop project: ` Ctrl + C `**

### Commands of flyway:
- **Migrate: ` mvn flyway:migrate `**
- **Clean: ` mvn flyway:clean (Execute this command only in development or testing environments). `**
- **Info: ` mvn flyway:info `**
- **Validade: ` mvn flyway:validate `**

# External Links:
<a href="https://www.youtube.com/watch?v=JkSMTDOCGoQ&list=PLoWHBWVWTfi2hgIY59_eCsQyBsVkiXs-J"> <img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white"></a>
<a href="https://medium.com/p/abda8172ec1f"> <img src="https://img.shields.io/badge/Medium-12100E?style=for-the-badge&logo=medium&logoColor=white"></a>
