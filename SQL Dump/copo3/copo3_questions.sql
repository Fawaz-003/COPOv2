-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: copo3
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_outcome` varchar(255) DEFAULT NULL,
  `exam_type` varchar(255) DEFAULT NULL,
  `max_marks` int NOT NULL,
  `part` varchar(255) DEFAULT NULL,
  `question_number` varchar(255) DEFAULT NULL,
  `semester` int NOT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `batch_id` bigint NOT NULL,
  `department_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn1avt7pgok3023fuycq41eja1` (`batch_id`),
  KEY `FK124be202h7u31e4s1pl9cdqnd` (`department_id`),
  KEY `FKo0h0rn8bxifrxmq1d8ipiyqv5` (`subject_id`),
  CONSTRAINT `FK124be202h7u31e4s1pl9cdqnd` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FKn1avt7pgok3023fuycq41eja1` FOREIGN KEY (`batch_id`) REFERENCES `batches` (`id`),
  CONSTRAINT `FKo0h0rn8bxifrxmq1d8ipiyqv5` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'CO1','CAT 1',5,'Part A','Q1',1,'What is Web Technologies?',1,2,1),(2,'CO2','CAT 1',5,'Part A','Q2',1,'What are the technologies used?',1,2,1),(3,'CO1','CAT 1',10,'Part B','Q3',1,'Describe Web Technologies?',1,2,1),(4,'CO2','CAT 1',15,'Part C','Q4',1,'Explain types of web Technologies.',1,2,1),(5,'CO1','CAT 1',5,'Part A','Q1',1,'what is Data Structure?',1,2,2),(6,'CO2','CAT 1',5,'Part A','Q2',1,'What is Data',1,2,2),(7,'CO1','CAT 1',10,'Part B','Q3',1,'describe types of data structure',1,2,2),(8,'CO2','CAT 1',15,'Part C','Q4',1,'All types in DATA',1,2,2);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-31 14:37:55
