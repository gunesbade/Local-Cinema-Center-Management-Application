# Local Cinema Center Management Application
This is a Java-based desktop application developed for the CMPE343 course. It is designed to manage the operations of a local cinema center with role-based access. The GUI is created with JavaFX and SceneBuilder. MySQL is used as the database, and JDBC is used for data access.

## Features

### General
- Login system with role-based access (Cashier, Admin, Manager)
- JavaFX interface with FXML files
- MySQL integration via JDBC

### Cashier
- Search movies by genre, partial or full title
- Show movie details (poster, genre, summary)
- Select session, hall, and seats
- Seat layout view with available/occupied indicators
- Age-based discounts for under 18 or over 60
- Add beverages, snacks, toys to shopping cart
- Calculate VAT (20% for tickets, 10% for products)
- Accept cash payment and generate ticket/invoice

### Admin
- Add or update movie info (title, poster, genre, summary)
- Create and edit monthly schedules
- Handle cancellations and refunds

### Manager
- View and update product stock
- Manage personnel (except self)
- Edit ticket/product prices and discount rate
- View income and tax reports

## Technologies Used
- Java
- JavaFX
- SceneBuilder
- MySQL
- JDBC
- GitHub

## Setup
1. Clone this repository.
2. Open the project in your IDE.
3. Import the MySQL database using the SQL script.
4. Configure your JDBC connection.
5. Run the application.
