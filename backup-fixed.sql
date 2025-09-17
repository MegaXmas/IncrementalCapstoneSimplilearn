CREATE DATABASE  IF NOT EXISTS `travel_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `travel_db`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: travel_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_users`
--

DROP TABLE IF EXISTS `admin_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `adminUsername` varchar(100) NOT NULL,
  `adminPassword` varchar(100) NOT NULL,
  `enabled` tinyint NOT NULL DEFAULT '1',
  `accountLocked` tinyint DEFAULT '0',
  `lastLogin` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `admin_username_UNIQUE` (`adminUsername`),
  UNIQUE KEY `admin_password_UNIQUE` (`adminPassword`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_users`
--

LOCK TABLES `admin_users` WRITE;
/*!40000 ALTER TABLE `admin_users` DISABLE KEYS */;
INSERT INTO `admin_users` VALUES (1,'testAdmin1','$2a$10$N9qo8uLOickgx2ZMRZoMye/tSs1uxgQVRHr6LFWt3zC1N1V0eAb/O',1,0,NULL);
/*!40000 ALTER TABLE `admin_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `airports`
--

DROP TABLE IF EXISTS `airports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airports` (
  `id` int NOT NULL AUTO_INCREMENT,
  `airportFullName` varchar(45) NOT NULL,
  `airportCode` varchar(45) NOT NULL,
  `airportCityLocation` varchar(45) NOT NULL,
  `airportCountryLocation` varchar(45) NOT NULL,
  `airportTimezone` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `airportCode_UNIQUE` (`airportCode`),
  UNIQUE KEY `airportFullName_UNIQUE` (`airportFullName`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `unique_airport_code` (`airportCode`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airports`
--

LOCK TABLES `airports` WRITE;
/*!40000 ALTER TABLE `airports` DISABLE KEYS */;
INSERT INTO `airports` VALUES (1,'John F. Kennedy International Airport','JFK','New York','United States','America/New_York'),(2,'Los Angeles International Airport','LAX','Los Angeles','United States','America/Los_Angeles'),(3,'Heathrow Airport','LHR','London','United Kingdom','Europe/London'),(4,'Charles de Gaulle Airport','CDG','Paris','France','Europe/Paris'),(5,'Tokyo Haneda Airport','HND','Tokyo','Japan','Asia/Tokyo'),(6,'Singapore Changi Airport','SIN','Singapore','Singapore','Asia/Singapore'),(7,'Dubai International Airport','DXB','Dubai','United Arab Emirates','Asia/Dubai'),(8,'Frankfurt Airport','FRA','Frankfurt','Germany','Europe/Berlin'),(9,'Sydney Kingsford Smith Airport','SYD','Sydney','Australia','Australia/Sydney'),(10,'Toronto Pearson International Airport','YYZ','Toronto','Canada','America/Toronto'),(17,'Amsterdam Airport Schiphol','AMS','Amsterdam','Netherlands','Europe/Amsterdam'),(18,'Hong Kong International Airport','HKG','Hong Kong','Hong Kong','Asia/Hong_Kong'),(19,'Denver International Airport','DEN','Denver','United States','America/Denver');
/*!40000 ALTER TABLE `airports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_details`
--

DROP TABLE IF EXISTS `bus_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `busNumber` varchar(45) NOT NULL,
  `busLine` varchar(45) NOT NULL,
  `busDepartureStation` varchar(45) NOT NULL,
  `busArrivalStation` varchar(45) NOT NULL,
  `busDepartureDate` varchar(45) NOT NULL,
  `busDepartureTime` varchar(45) NOT NULL,
  `busArrivalDate` varchar(45) NOT NULL,
  `busArrivalTime` varchar(45) NOT NULL,
  `busRideDuration` varchar(45) NOT NULL,
  `busRidePrice` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_departure_station` (`busDepartureStation`),
  KEY `fk_arrival_station` (`busArrivalStation`),
  CONSTRAINT `fk_arrival_station` FOREIGN KEY (`busArrivalStation`) REFERENCES `bus_stations` (`busStationCode`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_departure_station` FOREIGN KEY (`busDepartureStation`) REFERENCES `bus_stations` (`busStationCode`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_details`
--

LOCK TABLES `bus_details` WRITE;
/*!40000 ALTER TABLE `bus_details` DISABLE KEYS */;
INSERT INTO `bus_details` VALUES (1,'BUS001','Greyhound','PABT','USBT','2025-08-25','08:00','2025-08-28','20:00','3 days 12 hours','299.99'),(2,'BUS002','Megabus','USBT','LVBT','2025-08-25','09:30','2025-08-25','18:45','9 hours 15 minutes','89.50'),(3,'BUS003','FlixBus','PABT','MCS','2025-08-26','07:15','2025-08-27','19:30','1 day 12 hours 15 minutes','159.75'),(4,'BUS004','Eurolines','VCS','GRPG','2025-08-25','22:00','2025-08-26','06:30','8 hours 30 minutes','45.90'),(5,'BUS005','FlixBus','GRPG','MZOB','2025-08-26','14:20','2025-08-27','03:45','13 hours 25 minutes','67.25'),(6,'BUS006','Greyhound Canada','TCT','PABT','2025-08-27','06:00','2025-08-27','14:30','8 hours 30 minutes','125.00'),(7,'BUS007','BoltBus','PABT','SKSS','2025-08-25','11:45','2025-08-27','23:15','2 days 11 hours 30 minutes','189.99'),(8,'BUS008','JR Highway Bus','TSBT','SCSBT','2025-08-26','16:30','2025-08-28','12:15','1 day 19 hours 45 minutes','245.80'),(9,'BUS009','National Express','VCS','CBS','2025-08-28','05:45','2025-08-30','18:20','2 days 12 hours 35 minutes','198.75'),(10,'BUS010','RegioJet','MZOB','QSBT','2025-08-27','20:15','2025-08-30','16:45','2 days 20 hours 30 minutes','279.50'),(16,'test1','BUSTEST001','USBT','MZOB','2025-01-01','09:50','2025-01-02','10:50','22h 55m','10');
/*!40000 ALTER TABLE `bus_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_stations`
--

DROP TABLE IF EXISTS `bus_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_stations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `busStationFullName` varchar(255) NOT NULL,
  `busStationCode` varchar(45) NOT NULL,
  `busStationCityLocation` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `busStationCode_UNIQUE` (`busStationCode`),
  UNIQUE KEY `busStationFullName_UNIQUE` (`busStationFullName`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `unique_station_code` (`busStationCode`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_stations`
--

LOCK TABLES `bus_stations` WRITE;
/*!40000 ALTER TABLE `bus_stations` DISABLE KEYS */;
INSERT INTO `bus_stations` VALUES (11,'Port Authority Bus Terminal','PABT','New York'),(12,'Union Station Bus Terminal','USBT','Los Angeles'),(13,'Victoria Coach Station','VCS','London'),(14,'Gare Routière Internationale de Paris-Gallieni','GRPG','Paris'),(15,'Tokyo Station Bus Terminal','TSBT','Tokyo'),(16,'Queen Street Bus Terminal','QSBT','Brisbane'),(17,'Central Bus Station','CBS','Tel Aviv'),(18,'München Zentraler Omnibusbahnhof','MZOB','Munich'),(19,'Toronto Coach Terminal','TCT','Toronto'),(20,'Sydney Central Station Bus Terminal','SCSBT','Sydney'),(21,'Las Vegas Bus Terminal','LVBT','Las Vegas'),(22,'Miami Central Station','MCS','Miami'),(23,'Seattle King Street Station','SKSS','Seattle'),(24,'Salesforce Transit Center','SFTC','San Francisco');
/*!40000 ALTER TABLE `bus_stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `credit_card` varchar(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `account_locked` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `credit_card_UNIQUE` (`credit_card`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'john_doe','john.doe@email.com','$2a$10$rQ8L9uO2vK5M6tN7xE1pHe9wZ3yX4uV5tR6sA7bC8dF9gH0iJ1kL2','John','Doe','+1-555-0101','123 Broadway, New York, NY 10001','4532-1234-5678-9012',1,0,'2024-01-15 18:30:00',NULL),(2,'sarah_chen','sarah.chen@techcorp.com','$2a$10$sT9M0pR1wL6N8xF2qI4rGe0zA5yY6vW7uS8cD9eH1jK3lM4nO5pQ6','Sarah','Chen','+1-415-555-0202','456 Market St, San Francisco, CA 94102','5555-4444-3333-2222',1,0,'2024-02-20 22:15:30',NULL),(3,'alex_smith','alex.smith@university.ac.uk','$2a$10$uV0P3sU6yM9O2rK5vJ8tHf1bA7zZ8wX9vT0eR1gI4lN6oP7qS8uW3','Alex','Smith','+44-20-7946-0958','789 Oxford Street, London W1C 1JN, UK','4000-1111-2222-3333',1,0,'2024-03-10 16:45:20',NULL),(4,'maria_rodriguez','maria.r@globalmail.com','$2a$10$wX2Q5vY8zO1P4sL7wK0uIg3cB9eE2hG5wU8fS1kM6pR9vZ4xC7wF8','Maria','Rodriguez','+1-416-555-0404','321 Queen St W, Toronto, ON M5V 2A4, Canada','6011-5555-6666-7777',1,0,'2024-01-26 00:20:10',NULL),(5,'david_wilson','david.wilson@aussiemail.au','$2a$10$yZ4R7wA0bP3Q6vL9yN2xJh5dC1gG4kI7yW0hU3nO8qT1yE6yS9zB2','David','Wilson','+61-2-9876-5432','654 George Street, Sydney NSW 2000, Australia','3782-822468-10005',1,0,'2024-02-05 19:30:45',NULL),(6,'lisa_thompson','lisa.nomad@wanderlust.de','$2a$10$zA6S9yC2eQ5R8wM1zO4yKi7fD3hH6lJ9zA2iV5oP0rU7yF8zT1wC4','Lisa','Thompson','+49-30-12345678','987 Unter den Linden, Berlin 10117, Germany','4111-1111-1111-1111',1,0,'2024-03-01 21:55:25',NULL),(7,'hiroshi_tanaka','h.tanaka@japanesecorp.jp','$2a$10$bB8T1zD4fR7S0wN3zP6yLj9gE5iI8mK1bC4jW7pQ2sV9yG0zA3wE6','Hiroshi','Tanaka','+81-3-1234-5678','147 Shibuya, Tokyo 150-0002, Japan','5454-5454-5454-5454',1,0,'2024-01-30 16:10:35',NULL),(8,'emma_martin','emma.martin@parisfirm.fr','$2a$10$cC0U3zF6gS9T2wO5zQ8yMk1hF7jJ0nL3cD6kX9qR4tW1yH2zB5wF8','Emma','Martin','+33-1-42-86-97-53','258 Champs-Élysées, Paris 75008, France','2222-3333-4444-5555',1,0,'2024-02-15 01:25:50',NULL),(9,'ahmed_hassan','ahmed.hassan@dubaitrade.ae','$2a$10$dD2V5zG8hT1U4wP7zR0yNl3iG9kK2oM5dE8lY1rS6uX3yI4zC7wG0','Ahmed','Hassan','+971-4-123-4567','369 Sheikh Zayed Road, Dubai, UAE','6759-6498-2643-8412',1,0,'2024-03-05 20:40:15',NULL),(10,'jennifer_lee','dr.jennifer.lee@chicagohosp.org','$2a$10$eE4W7zH0iU3V6wQ9zS2yOm5jH1lL4pN7eF0mZ3sT8vY5yJ6zD9wH2','Jennifer','Lee','+1-312-555-0909','741 Michigan Avenue, Chicago, IL 60611','8888-7777-6666-5555',1,0,'2024-02-28 15:15:40',NULL),(11,'testclient1','testclient@email.com','$2a$10$Gi1XZo.aQ99FwDa5bqx3G.6H9P8Tv.jFbHr5wfCSl0LMh175GaZSq','test','client','123456789','',NULL,1,0,'2025-08-29 21:35:52','2025-08-29 22:33:10');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_details`
--

DROP TABLE IF EXISTS `flight_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `flightNumber` varchar(45) NOT NULL,
  `flightAirline` varchar(45) NOT NULL,
  `flightOrigin` varchar(45) NOT NULL,
  `flightDestination` varchar(45) NOT NULL,
  `flightDepartureDate` varchar(45) NOT NULL,
  `flightArrivalDate` varchar(45) NOT NULL,
  `flightDepartureTime` varchar(45) NOT NULL,
  `flightArrivalTime` varchar(45) NOT NULL,
  `flightTravelTime` varchar(45) NOT NULL,
  `flightPrice` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_flight_origin` (`flightOrigin`),
  KEY `fk_flight_destination` (`flightDestination`),
  CONSTRAINT `fk_flight_destination` FOREIGN KEY (`flightDestination`) REFERENCES `airports` (`airportCode`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_flight_origin` FOREIGN KEY (`flightOrigin`) REFERENCES `airports` (`airportCode`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_details`
--

LOCK TABLES `flight_details` WRITE;
/*!40000 ALTER TABLE `flight_details` DISABLE KEYS */;
INSERT INTO `flight_details` VALUES (1,'AA1001','American Airlines','JFK','LAX','2025-08-25','2025-08-25','08:00','11:30','5 hours 30 minutes','299.99'),(2,'UA2002','United Airlines','LAX','JFK','2025-08-25','2025-08-25','14:15','22:45','5 hours 30 minutes','324.50'),(3,'BA3003','British Airways','LHR','JFK','2025-08-26','2025-08-26','10:30','14:15','8 hours 45 minutes','589.75'),(4,'AF4004','Air France','CDG','LAX','2025-08-26','2025-08-26','11:45','15:30','11 hours 45 minutes','749.99'),(5,'JL5005','Japan Airlines','HND','LAX','2025-08-27','2025-08-27','16:20','10:15','9 hours 55 minutes','678.80'),(6,'EK6006','Emirates','DXB','LHR','2025-08-25','2025-08-25','09:15','13:45','7 hours 30 minutes','456.25'),(7,'QR7007','Qatar Airways','DXB','SYD','2025-08-28','2025-08-28','02:30','17:20','14 hours 50 minutes','892.40'),(8,'KL8008','KLM','AMS','CDG','2025-08-25','2025-08-25','07:30','08:45','1 hour 15 minutes','89.50'),(9,'LH9009','Lufthansa','FRA','AMS','2025-08-26','2025-08-26','19:10','20:25','1 hour 15 minutes','95.75'),(10,'CX1010','Cathay Pacific','HKG','SYD','2025-08-27','2025-08-28','23:45','11:30','9 hours 45 minutes','567.90'),(11,'Test001','aa','LAX','JFK','2025-01-01','2025-01-02','02:05','07:40','8h 50m','1000'),(12,'TEST002','aa','LAX','FRA','2025-01-02','2025-02-01','06:45','15:05','8h 40m','1');
/*!40000 ALTER TABLE `flight_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `train_details`
--

DROP TABLE IF EXISTS `train_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trainNumber` varchar(45) NOT NULL,
  `trainLine` varchar(45) NOT NULL,
  `trainDepartureStation` varchar(45) NOT NULL,
  `trainArrivalStation` varchar(45) NOT NULL,
  `trainDepartureDate` varchar(45) NOT NULL,
  `trainDepartureTime` varchar(45) NOT NULL,
  `trainArrivalDate` varchar(45) NOT NULL,
  `trainArrivalTime` varchar(45) NOT NULL,
  `trainRideDuration` varchar(45) NOT NULL,
  `trainRidePrice` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_train_departure` (`trainDepartureStation`),
  KEY `fk_train_arrival` (`trainArrivalStation`),
  CONSTRAINT `fk_train_arrival` FOREIGN KEY (`trainArrivalStation`) REFERENCES `train_stations` (`trainStationCode`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_train_departure` FOREIGN KEY (`trainDepartureStation`) REFERENCES `train_stations` (`trainStationCode`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train_details`
--

LOCK TABLES `train_details` WRITE;
/*!40000 ALTER TABLE `train_details` DISABLE KEYS */;
INSERT INTO `train_details` VALUES (1,'ACELA001','Amtrak Acela','NYP','WAS','2025-08-25','06:00','2025-08-25','08:45','2 hours 45 minutes','189.00'),(2,'ACELA002','Amtrak Acela','WAS','NYP','2025-08-25','18:30','2025-08-25','21:15','2 hours 45 minutes','189.00'),(3,'NER101','Amtrak Northeast Regional','NYP','GCT','2025-08-25','07:15','2025-08-25','07:45','30 minutes','25.50'),(4,'TGV001','SNCF TGV','GDN','PAD','2025-08-26','09:30','2025-08-26','12:45','3 hours 15 minutes','75.80'),(5,'ICE002','Deutsche Bahn ICE','BER','PAD','2025-08-26','08:15','2025-08-26','17:30','9 hours 15 minutes','149.90'),(6,'EST003','Eurostar','STP','GDN','2025-08-25','10:31','2025-08-25','14:47','4 hours 16 minutes','89.50'),(7,'SKR001','JR Shinkansen','TYO','ROM','2025-08-27','14:20','2025-08-29','08:45','1 day 18 hours 25 minutes','445.75'),(8,'RJ101','RegioJet','BER','AMS','2025-08-28','06:45','2025-08-28','13:30','6 hours 45 minutes','67.25'),(9,'TLK201','PKP Intercity','BER','ROM','2025-08-26','22:15','2025-08-28','14:30','1 day 16 hours 15 minutes','189.99'),(10,'VIA001','VIA Rail','CHI','NYP','2025-08-27','19:30','2025-08-28','18:40','23 hours 10 minutes','156.80'),(14,'TEST001','test','LAX','CHI','2032-08-08','09:45','2032-08-10','21:45','24h 50m','1348756');
/*!40000 ALTER TABLE `train_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `train_stations`
--

DROP TABLE IF EXISTS `train_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_stations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trainStationFullName` varchar(45) NOT NULL,
  `trainStationCode` varchar(45) NOT NULL,
  `trainStationCityLocation` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `trainStationCode_UNIQUE` (`trainStationCode`),
  UNIQUE KEY `trainStationFullName_UNIQUE` (`trainStationFullName`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `unique_train_station_code` (`trainStationCode`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train_stations`
--

LOCK TABLES `train_stations` WRITE;
/*!40000 ALTER TABLE `train_stations` DISABLE KEYS */;
INSERT INTO `train_stations` VALUES (11,'Pennsylvania Station','NYP','New York'),(12,'Union Station LA','LAX','Los Angeles'),(13,'London Paddington','PAD','London'),(14,'Gare du Nord','GDN','Paris'),(15,'Tokyo Station','TYO','Tokyo'),(16,'Berlin Hauptbahnhof','BER','Berlin'),(17,'Roma Termini','ROM','Rome'),(18,'Union Station CHI','CHI','Chicago'),(19,'Amsterdam Centraal','AMS','Amsterdam'),(20,'Sydney Central Station','SYD','Sydney'),(21,'Grand Central Terminal','GCT','New York'),(22,'Union Station','WAS','Washington DC'),(23,'St. Pancras International','STP','London'),(24,'30th Street Station','30SPHL','Philadelphia');
/*!40000 ALTER TABLE `train_stations` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-16 16:52:31
