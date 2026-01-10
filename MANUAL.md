# Smart Vehicle Procurement System using Blockchain Technology

This project is a Smart Vehicle Procurement System built with Django. It allows for the management of vehicle procurement processes, involving Buyers and Sellers.

## Prerequisites

Before running this project, ensure you have the following installed:

- **Python:** version 3.8 or higher.
- **pip:** Python package installer.
- **Git:** Version control system.

## Installation

1.  **Clone the Repository**
    If you haven't already, clone the repository to your local machine:

    ```bash
    git clone https://github.com/YOUR_USERNAME/smart-vehicle-procurement-blockchain.git
    cd smart-vehicle-procurement-blockchain
    ```

2.  **Create a Virtual Environment (Recommended)**
    It's best practice to run Python projects in a virtual environment.

    ```bash
    # Windows
    python -m venv venv
    venv\Scripts\activate

    # macOS/Linux
    python3 -m venv venv
    source venv/bin/activate
    ```

3.  **Install Dependencies**
    Install the required Python packages using `requirements.txt`.
    ```bash
    pip install -r requirements.txt
    ```

## Database Setup

1.  **Migrations**
    Run the following commands to create the database tables.

    ```bash
    python manage.py makemigrations
    python manage.py migrate
    ```

2.  **Create Superuser (Admin)**
    Create an administrator account to access the Django admin panel.
    ```bash
    python manage.py createsuperuser
    ```
    Follow the prompts to set a username, email, and password.

## Running the Application

1.  **Start the Development Server**

    ```bash
    python manage.py runserver
    ```

2.  **Access the Application**
    Open your web browser and navigate to:
    - **Home:** `http://127.0.0.1:8000/`
    - **Admin Panel:** `http://127.0.0.1:8000/admin/`

## Project Structure

- **Smart_Vehicle_Procurement_System_using_Blockchain_Technology/**: Main project directory containing settings and configuration.
- **Buyers/**: App handling buyer-related functionality.
- **seller/**: App handling seller-related functionality.
- **Templates/**: HTML templates for the application.
- **media/**: Directory for user-uploaded media files.
- **db.sqlite3**: Default SQLite database file.
- **manage.py**: Django's command-line utility for administrative tasks.

## Troubleshooting

- **Image Issues:** If you encounter errors related to images, ensure `Pillow` is installed correctly (`pip install Pillow`).
- **Database Errors:** If you have issues with the database, you can try deleting `db.sqlite3` and running the migration commands again (warning: this deletes all data).
