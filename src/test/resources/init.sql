/*
SQLyog Ultimate v12.5.1 (64 bit)
MySQL - 5.6.42-log : Database - ultimate
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`ultimate` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `ultimate`;

/*Table structure for table `lib` */

DROP TABLE IF EXISTS `lib`;

CREATE TABLE `lib` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message` varchar(100) NOT NULL DEFAULT '',
  `test` varchar(100) NOT NULL DEFAULT '',
  `msd` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

/*Data for the table `lib` */

insert  into `lib`(`id`,`message`,`test`,`msd`) values 
(1,'re','sdf','滚动'),
(2,'','',NULL),
(12,'cweijan','',NULL);

/*Table structure for table `rh_admin` */

DROP TABLE IF EXISTS `rh_admin`;

CREATE TABLE `rh_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message` varchar(100) NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `test` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

/*Data for the table `rh_admin` */

insert  into `rh_admin`(`id`,`message`,`create_date`,`test`) values 
(1,'hello','2018-12-27 16:16:46','test'),
(2,'cweijain','2018-12-28 14:27:21','test'),
(3,'hello','2018-12-28 14:33:51','test'),
(4,'hello','2018-12-28 14:40:14','test'),
(5,'hello','2018-12-31 20:42:13','test'),
(6,'','2019-01-07 14:19:45','test'),
(7,'','2019-01-07 14:20:18','test');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
