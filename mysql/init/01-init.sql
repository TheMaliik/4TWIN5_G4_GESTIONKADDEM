-- Ensure kaddemdb database exists
CREATE DATABASE IF NOT EXISTS kaddemdb;

-- Grant all privileges to root from any host
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- Grant all privileges to root from localhost (if it exists)
ALTER USER IF EXISTS 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;

-- Create another user for connections if needed
CREATE USER IF NOT EXISTS 'springuser'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON kaddemdb.* TO 'springuser'@'%';

-- Apply changes
FLUSH PRIVILEGES; 