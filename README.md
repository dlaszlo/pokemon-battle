# Pokemon Battle Simulator

This is a web application designed to simulate Pokemon battles.

## Build and run containers

To run this application, you must have _Docker_ installed on your system.

Navigate to the project root directory (where the `docker-compose.yml` file is located) and execute the following command:

```bash
docker-compose up --build -d
```

## Access application

Open your web browser at: http://localhost

## Stop the application

To stop and remove the containers:

```bash
docker-compose down
```

## Development Mode

For running the Backend directly from an IDE (while the Frontend runs via ng serve), 
the 'dev' Spring Profile must be explicitly activated to enable CORS headers.

To run the Backend locally in development mode:

```bash
java -jar -Dspring.profiles.active=dev backend/target/backend-0.0.1-SNAPSHOT.jar
```
