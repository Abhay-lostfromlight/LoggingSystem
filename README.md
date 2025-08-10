# LoggingSystem

A flexible and extensible Java logging system designed for batch and asynchronous log processing with file-based storage. This project provides a centralized way to collect, store, and manage application logs, allowing for future extension to database storage with minimal code changes.

## Features

- **Core Logger**: Singleton logger that batches logs in memory before persisting for performance.
- **Pluggable Storage**: Abstract `Datastore` interface allows swapping between file storage and other backends (e.g., databases).
- **File Storage**: Out-of-the-box file-based storage implementation (`Filestore`) for persisting logs.
- **Batch Processing**: Logs are collected in sets and processed in batches for efficiency.
- **Asynchronous Handling**: Uses a thread pool to process and write logs without blocking the main application.
- **Log Details**: Captures data, timestamp, thread information, severity, and stack trace for each log.
- **Deep Copy Utility**: Ensures log objects are safely copied before processing.
- **Log Management**: Supports log deletion and backup.

## Usage

1. Instantiate or get the logger instance:
    ```java
    Logger logger = Logger.getInstance();
    ```
2. Add logs:
    ```java
    logger.addLog(new Log(Severity.LOW, "This is a test log entry."));
    ```
3. Batch and persist logs:
    ```java
    logger.appendLog();
    ```

## Design Concepts

- **Polymorphic Storage**: Future-proof design via the `Datastore` interface.
- **Threaded Processing**: Up to 50 worker threads for scalable log handling.
- **Extensible**: Easily add new storage backends or log processing strategies.

## Requirements

- Java 8+
- (Optional) JUnit for testing

## Testing

Unit tests can be found in `src/test/java/logger/AppTest.java`.

---

*This project is a foundation for robust logging in Java applications, easily adaptable to new storage technologies and scalable to high-throughput environments.*
