-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: localcinemacenter
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `halls`
--

DROP TABLE IF EXISTS `halls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `halls` (
  `hall_id` int NOT NULL AUTO_INCREMENT,
  `hall_name` enum('Hall_A','Hall_B') NOT NULL,
  `seating_capacity` int NOT NULL,
  PRIMARY KEY (`hall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `halls`
--

LOCK TABLES `halls` WRITE;
/*!40000 ALTER TABLE `halls` DISABLE KEYS */;
INSERT INTO `halls` VALUES (1,'Hall_A',16),(2,'Hall_B',48);
/*!40000 ALTER TABLE `halls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` enum('Beverage','Biscuit','Toy') NOT NULL,
  `stock_quantity` int NOT NULL,
  `product_price` decimal(10,2) NOT NULL DEFAULT '5.00',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'Beverage',100,3.50),(2,'Biscuit',50,2.75),(3,'Toy',20,15.00);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthlyschedule`
--

DROP TABLE IF EXISTS `monthlyschedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthlyschedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `hall_id` int NOT NULL,
  `session_time` time NOT NULL,
  `session_date` date NOT NULL,
  `duration` int DEFAULT NULL,
  PRIMARY KEY (`schedule_id`),
  KEY `movie_id` (`movie_id`),
  KEY `hall_id` (`hall_id`),
  CONSTRAINT `monthlyschedule_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  CONSTRAINT `monthlyschedule_ibfk_2` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`hall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthlyschedule`
--

LOCK TABLES `monthlyschedule` WRITE;
/*!40000 ALTER TABLE `monthlyschedule` DISABLE KEYS */;
INSERT INTO `monthlyschedule` VALUES (1,1,'Movie A',1,'18:00:00','2025-01-10',120),(2,2,'Movie B',2,'20:30:00','2025-01-11',90),(3,3,'Harry Potter',1,'00:00:14','2025-01-15',120),(4,4,'Joker',2,'00:00:12','2025-01-20',120),(5,5,'Dune',2,'00:00:22','2025-01-24',125),(6,3,'Harry Potter',1,'00:00:21','2025-01-15',120),(7,7,'ww',2,'00:00:12','2025-01-14',125);
/*!40000 ALTER TABLE `monthlyschedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `movie_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `summary` text,
  `poster_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,'Movie A','Action','An action-packed thriller.','/path/to/posterA.jpg'),(2,'Movie B','Comedy','A hilarious comedy movie.','/path/to/posterB.jpg'),(3,'Harry Potter','urban fantasy','They are mainly dramas, and maintain a fairly serious and dark tone.','C:\\Users\\ACER\\Desktop\\posterhp.jpeg'),(4,'Joker','psychological thriller','t shows the delusions that many white men have about their place in society and the brutality that can result when that place is denied.','C:\\Users\\ACER\\Desktop\\joker.jpeg'),(5,'Dune','science  fic','It tells the story of young Paul Atreides, whose family accepts the stewardship of the planet Arrakis','C:\\Users\\ACER\\Desktop\\dune.jpeg'),(6,'hp','aaa','aaa','C:\\Users\\ACER\\Desktop\\dune.jpeg'),(7,'ww','aaa','aaaa','C:\\Users\\ACER\\Desktop\\posterhp.jpeg');
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pricing`
--

DROP TABLE IF EXISTS `pricing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pricing` (
  `pricing_id` int NOT NULL AUTO_INCREMENT,
  `customer_type` enum('standard','student','senior') NOT NULL,
  `ticket_price` decimal(10,2) NOT NULL,
  `product_price` decimal(10,2) NOT NULL DEFAULT '5.00',
  `discount_rate` decimal(5,2) NOT NULL DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pricing_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pricing`
--

LOCK TABLES `pricing` WRITE;
/*!40000 ALTER TABLE `pricing` DISABLE KEYS */;
INSERT INTO `pricing` VALUES (1,'standard',60.00,20.00,50.00,'2025-01-13 12:20:51'),(2,'student',30.00,20.00,50.00,'2025-01-13 12:20:51'),(3,'senior',30.00,20.00,50.00,'2025-01-13 12:20:51');
/*!40000 ALTER TABLE `pricing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revenue_tax`
--

DROP TABLE IF EXISTS `revenue_tax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revenue_tax` (
  `revenue_id` int NOT NULL AUTO_INCREMENT,
  `total_revenue` decimal(10,2) DEFAULT '0.00',
  `total_tax` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`revenue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revenue_tax`
--

LOCK TABLES `revenue_tax` WRITE;
/*!40000 ALTER TABLE `revenue_tax` DISABLE KEYS */;
INSERT INTO `revenue_tax` VALUES (1,10000.00,5000.00);
/*!40000 ALTER TABLE `revenue_tax` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revenuetaxdetails`
--

DROP TABLE IF EXISTS `revenuetaxdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revenuetaxdetails` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `revenue_id` int NOT NULL,
  `detail_description` text NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `revenue_id` (`revenue_id`),
  CONSTRAINT `revenuetaxdetails_ibfk_1` FOREIGN KEY (`revenue_id`) REFERENCES `revenue_tax` (`revenue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revenuetaxdetails`
--

LOCK TABLES `revenuetaxdetails` WRITE;
/*!40000 ALTER TABLE `revenuetaxdetails` DISABLE KEYS */;
INSERT INTO `revenuetaxdetails` VALUES (1,1,'Movie Ticket Sales',7000.00),(2,1,'Product Sales',3000.00),(3,1,'Tax Collected',2000.00);
/*!40000 ALTER TABLE `revenuetaxdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactiondetails`
--

DROP TABLE IF EXISTS `transactiondetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactiondetails` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `transaction_id` int NOT NULL,
  `item_type` enum('Ticket','Product') NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `transaction_id` (`transaction_id`),
  CONSTRAINT `transactiondetails_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactiondetails`
--

LOCK TABLES `transactiondetails` WRITE;
/*!40000 ALTER TABLE `transactiondetails` DISABLE KEYS */;
INSERT INTO `transactiondetails` VALUES (1,1,'Ticket','Joker',3,50.00),(3,2,'Ticket','Dune',4,50.00),(4,2,'Product','Soda',3,15.00);
/*!40000 ALTER TABLE `transactiondetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `transaction_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(50) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `transactions_ibfk_1` (`username`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,'2025-01-14 10:00:00','cashier1',150.00),(2,'2025-01-14 11:30:00','manager1',200.00);
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `role` enum('admin','manager','cashier') NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin1','admin1','Admin','One','admin'),('berdem','bade123','bade','erdem','admin'),('cashier1','cashier1','Cashier','One','cashier'),('manager1','manager1','Manager','One','manager'),('ruzun','ulaş123','ulaş','uzun','admin'),('zmutlu','zeynep1','zenep','mutlu','admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-13 19:49:19
