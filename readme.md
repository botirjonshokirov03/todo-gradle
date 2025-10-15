# Lab2 - Gradle Project
# Student name: Simbarashe Vhirozho
# Student ID: 47147

## Installation

1. **Clone the repository**
   ```powershell
   git clone <repository-url>
   cd lab2
   ```

2. **Build the project (optional)**
   ```powershell
   ./gradlew build
   ```
   Or on Windows:
   ```powershell
   gradlew.bat build
   ```

## Running the Project

Start the server using:
```powershell
./gradlew bootRun
```
Or on Windows:
```powershell
gradlew.bat bootRun
```

The server will start at [http://localhost:8080](http://localhost:8080).

## Features
- All CRUD operations for Todo items:
  - Create a todo
  - Delete a todo
  - Update a todo
  - List all todos
- Check completed todos

## Usage

Open your browser and navigate to `http://localhost:8080` to use the application. The server generates HTML content for all features.
