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
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'CO1','CAT 1',5,'Part A','Q1',1,'What is Web Technologies?',1,2,1),(2,'CO2','CAT 1',5,'Part A','Q2',1,'What are the technologies used?',1,2,1),(3,'CO1','CAT 1',10,'Part B','Q3',1,'Describe Web Technologies?',1,2,1),(4,'CO2','CAT 1',15,'Part C','Q4',1,'Explain types of web Technologies.',1,2,1),(5,'CO1','CAT 1',5,'Part A','Q1',1,'what is Data Structure?',1,2,2),(6,'CO2','CAT 1',5,'Part A','Q2',1,'What is Data',1,2,2),(7,'CO1','CAT 1',10,'Part B','Q3',1,'describe types of data structure',1,2,2),(8,'CO2','CAT 1',15,'Part C','Q4',1,'All types in DATA',1,2,2),(13,'CO1','CAT 2',5,'Part A','Q1',1,'What is WEB?',1,2,1),(14,'CO1','CAT 2',5,'Part A','Q2',1,'Define the technologies?',1,2,1),(15,'CO2','CAT 2',5,'Part A','Q3',1,'list of technologies used?',1,2,1),(16,'CO2','CAT 2',5,'Part A','Q4',1,'list all',1,2,1),(17,'CO1','CAT 2',10,'Part B','Q5',1,'describe detail about web technologies',1,2,1),(18,'CO2','CAT 2',10,'Part B','Q6',1,'detailed descriptions',1,2,1),(19,'CO1','CAT 2',15,'Part C','Q7',1,'Describe all types in WEB..',1,2,1),(20,'CO2','CAT 2',15,'Part C','Q8',1,'Descibe all types of technologies',1,2,1),(21,'CO3','CAT 1',5,'Part A','Q1',1,'fsdgfsd',1,2,1),(22,'CO3','CAT 1',5,'Part A','Q2',1,'fguf',1,2,1),(23,'CO1','CAT 1 Lab ',100,'Lab','Q1',1,'lab questions',1,2,1),(24,'CO2','CAT 1 Lab ',100,'Lab','Q2',1,'lab ',1,2,1),(25,'CO3','Model Lab',100,'Lab','Q1',1,'model lab',1,2,1),(26,'CO2','CAT 2 Lab',100,'Lab','Q1',1,'CAT 2 lab',1,2,1),(27,'CO4','CAT 1',5,'Part A','Q1',1,'co 4 details',1,2,1),(28,'CO4','CAT 1',5,'Part A','Q2',1,'co 4 details checking',1,2,1),(29,'CO4','CAT 1',10,'Part B','Q3',1,'CO 4 questions',1,2,1),(30,'CO5','CAT 1',5,'Part A','Q1',1,'sCO5 questions',1,2,1),(31,'CO1','CAT 1',6,'Part A','1,2,3',3,'part A',1,2,3),(32,'CO2','CAT 1',4,'Part A','4,5',3,'part A',1,2,3),(33,'CO1','CAT 1',13,'Part B','6',3,'part B',1,2,3),(34,'CO2','CAT 1',13,'Part B','7',3,'part B',1,2,3),(35,'CO1','CAT 1',14,'Part C','8',3,'part C',1,2,3),(36,'CO3','CAT 2',6,'Part A','1,2,3',3,'part A',1,2,3),(37,'CO4','CAT 2',4,'Part A','4,5',3,'part A',1,2,3),(38,'CO3','CAT 2',13,'Part B','6',3,'part B',1,2,3),(39,'CO3','CAT 2',13,'Part B','7',3,'part B',1,2,3),(40,'CO4','CAT 2',14,'Part C','8',3,'part C',1,2,3),(41,'CO1','Model',4,'Part A','1,2',3,'part A',1,2,3),(42,'CO2','Model',4,'Part A','3,4',3,'part A',1,2,3),(43,'CO3','Model',4,'Part A','5,6',3,'part A',1,2,3),(44,'CO4','Model',4,'Part A','7,8',3,'part A',1,2,3),(45,'CO5','Model',4,'Part A','9,10',3,'part A',1,2,3),(46,'CO1','Model',13,'Part B','11',3,'part B',1,2,3),(47,'CO2','Model',13,'Part B','12',3,'part B',1,2,3),(48,'CO3','Model',13,'Part B','13',3,'part B',1,2,3),(49,'CO4','Model',13,'Part B','14',3,'part B',1,2,3),(50,'CO5','Model',13,'Part B','15',3,'part B',1,2,3),(51,'CO5','Model',15,'Part C','16A',3,'part C',1,2,3),(52,'CO2','Model',15,'Part C','16B',3,'part C',1,2,3),(53,'CO1','CAT 1 Lab ',100,'Lab','Q1',3,'LAB',1,2,3),(54,'CO2','CAT 1 Lab ',100,'Lab','Q2',3,'LAB',1,2,3),(55,'CO1','Model Lab',100,'Lab','Q1',3,'LAB',1,2,3),(56,'CO2','Model Lab',100,'Lab','Q2',3,'LAB',1,2,3),(57,'CO3','Model Lab',100,'Lab','Q3',3,'LAB',1,2,3),(58,'CO4','Model Lab',100,'Lab','Q4',3,'LAB',1,2,3),(59,'CO5','Model Lab',100,'Lab','Q5',3,'LAB',1,2,3),(60,'CO1','CAT 1',100,'Lab','Q1',1,'lab questions',1,2,2),(61,'CO1','CAT 1',6,'Part A','1,2,3',4,'part A',1,2,4),(62,'CO2','CAT 1',4,'Part A','4,5',4,'part A',1,2,4),(63,'CO1','CAT 1',13,'Part B','6',4,'part B',1,2,4),(64,'CO2','CAT 1',13,'Part B','7',4,'part B',1,2,4),(65,'CO1','CAT 1',14,'Part C','8',4,'part C',1,2,4),(66,'CO3','CAT 2',6,'Part A','1,2,3',4,'part A',1,2,4),(67,'CO4','CAT 2',4,'Part A','4,5',4,'part A',1,2,4),(68,'CO3','CAT 2',13,'Part B','6',4,'part B',1,2,4),(69,'CO3','CAT 2',13,'Part B','7',4,'part B',1,2,4),(70,'CO4','CAT 2',14,'Part C','8',4,'part C',1,2,4),(71,'CO1','Model',4,'Part A','1,2',4,'part A',1,2,4),(72,'CO2','Model',4,'Part A','3,4',4,'part A',1,2,4),(73,'CO3','Model',4,'Part A','5,6',4,'part A',1,2,4),(74,'CO4','Model',4,'Part A','7,8',4,'part A',1,2,4),(75,'CO5','Model',4,'Part A','9,10',4,'part A',1,2,4),(76,'CO1','Model',13,'Part B','11A',4,'part B',1,2,4),(77,'CO1','Model',13,'Part B','11B',4,'part B',1,2,4),(78,'CO2','Model',13,'Part B','12A',4,'part B',1,2,4),(79,'CO2','Model',13,'Part B','12B',4,'part B',1,2,4),(80,'CO3','Model',13,'Part B','13A',4,'part B',1,2,4),(81,'CO3','Model',13,'Part B','13B',4,'part B',1,2,4),(82,'CO4','Model',13,'Part B','14A',4,'part B',1,2,4),(83,'CO4','Model',13,'Part B','14B',4,'part B',1,2,4),(84,'CO5','Model',13,'Part B','15A',4,'part B',1,2,4),(85,'CO5','Model',13,'Part B','15B',4,'part B',1,2,4),(86,'CO5','Model',15,'Part C','16A',4,'part C',1,2,4),(87,'CO2','Model',13,'Part C','16B',4,'part C',1,2,4),(88,'CO1','CAT 1 Lab ',100,'Lab','Q1',4,'LAB',1,2,4),(89,'CO2','CAT 1 Lab ',100,'Lab','Q2',4,'LAB',1,2,4),(90,'CO1','Model Lab',100,'Lab','Q1',4,'LAB',1,2,4),(91,'CO2','Model Lab',100,'Lab','Q2',4,'LAB',1,2,4),(92,'CO3','Model Lab',102,'Lab','Q3',4,'LAB',1,2,4),(93,'CO4','Model Lab',100,'Lab','Q4',4,'LAB',1,2,4),(94,'CO5','Model Lab',100,'Lab','Q5',4,'LAB',1,2,4);
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

-- Dump completed on 2025-04-24 15:47:32
