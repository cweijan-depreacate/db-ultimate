/*
SQLyog Ultimate
MySQL - 5.5.37 : Database - ultimate
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`ultimate` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `lib` */

DROP TABLE IF EXISTS `lib`;

CREATE TABLE `lib` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message` varchar(100) DEFAULT NULL,
  `test` varchar(100) DEFAULT NULL,
  `msd` varchar(100) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `lib` */

insert  into `lib`(`id`,`message`,`test`,`msd`,`create_date`,`create_time`) values
(1,'cweijan1',NULL,NULL,NULL,NULL),
(2,'cweijan2',NULL,NULL,NULL,NULL),
(3,'cweijan3',NULL,NULL,NULL,NULL),
(4,'cweijan4',NULL,NULL,NULL,NULL),
(5,'cweijan',NULL,NULL,NULL,NULL),
(6,'cweijan',NULL,NULL,'2019-10-30','2019-10-30 22:47:53');

/*Table structure for table `password` */

DROP TABLE IF EXISTS `password`;

CREATE TABLE `password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site` varchar(100) DEFAULT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `strength_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `password` */

insert  into `password`(`id`,`site`,`salt`,`strength_type`) values
(1,'cweijan','cweijan',NULL),
(2,'linode','cweijan',NULL),
(3,'45.32.248.91','cweijan',NULL),
(4,'45.32.248.91msyql','cweijan',NULL),
(5,'123456','WirRx3fkXGe4/eZXBvsi8A==','one'),
(6,'javfree','WirRx3fkXGe4/eZXBvsi8A==','one'),
(7,'javfree','665420',NULL),
(8,'oofh1soNCEKfrNohDK5Kaw==','WirRx3fkXGe4/eZXBvsi8A==','one'),
(9,'admin','WirRx3fkXGe4/eZXBvsi8A==',NULL),
(10,'admin','javfree',NULL),
(11,'45.32.248.91','ssr',NULL);

/*Table structure for table `rh_admin` */

DROP TABLE IF EXISTS `rh_admin`;

CREATE TABLE `rh_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message` varchar(100) DEFAULT NULL,
  `admin_type` varchar(50) DEFAULT NULL,
  `message3` varchar(100) DEFAULT NULL,
  `new_column` varchar(30) DEFAULT NULL,
  `hello_world_test` varchar(100) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `test` varchar(100) DEFAULT NULL,
  `is_delete` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

/*Data for the table `rh_admin` */

insert  into `rh_admin`(`id`,`message`,`admin_type`,`message3`,`new_column`,`hello_world_test`,`create_date`,`test`,`is_delete`) values
(1,'hello','admin','',NULL,NULL,'2019-06-30 22:11:36',NULL,''),
(2,'hello','admin','',NULL,NULL,'2019-06-30 22:14:13',NULL,''),
(3,'hello','admin','',NULL,NULL,'2019-06-30 22:14:34',NULL,''),
(4,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test1','\0'),
(5,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test1','\0'),
(6,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test1','\0'),
(7,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test2','\0'),
(8,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test2','\0'),
(9,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test2','\0'),
(10,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test','\0'),
(11,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test','\0'),
(12,'hello','user','',NULL,NULL,'2019-06-30 22:14:34','test','\0'),
(13,'hello','admin','',NULL,NULL,'2019-06-30 22:15:28',NULL,''),
(14,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test1','\0'),
(15,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test1','\0'),
(16,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test1','\0'),
(17,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test2','\0'),
(18,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test2','\0'),
(19,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test2','\0'),
(20,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test','\0'),
(21,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test','\0'),
(22,'hello','user','',NULL,NULL,'2019-06-30 22:15:28','test','\0'),
(23,'hello','admin','',NULL,NULL,'2019-06-30 22:15:37',NULL,''),
(24,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:16:29',NULL,''),
(25,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test1','\0'),
(26,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test1','\0'),
(27,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test1','\0'),
(28,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test2','\0'),
(29,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test2','\0'),
(30,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test2','\0'),
(31,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test','\0'),
(32,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test','\0'),
(33,'hello','user',NULL,NULL,NULL,'2019-06-30 22:16:29','test','\0'),
(34,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:16:34',NULL,''),
(35,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:26:22',NULL,''),
(36,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:28:38',NULL,''),
(37,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:35:43',NULL,''),
(38,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:36:38',NULL,''),
(39,'hello','admin',NULL,NULL,NULL,'2019-06-30 22:38:24',NULL,'');

/*Table structure for table `t_booklist` */

DROP TABLE IF EXISTS `t_booklist`;

CREATE TABLE `t_booklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_title` varchar(100) DEFAULT NULL,
  `isbn` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `t_booklist` */

insert  into `t_booklist`(`id`,`book_title`,`isbn`) values
(1,'hello','isbn'),
(2,'hello','isbn'),
(3,'hello','isbn'),
(4,'hello','isbn'),
(5,'hello','isbn');

/*Table structure for table `test_init` */

DROP TABLE IF EXISTS `test_init`;

CREATE TABLE `test_init` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `magenet_list` blob,
  `name1` varchar(30) DEFAULT NULL COMMENT '濮撳悕',
  `ag` int(11) NOT NULL DEFAULT '10' COMMENT '骞撮緞',
  `fl` decimal(5,2) DEFAULT NULL,
  `d1` datetime DEFAULT NULL,
  `d2` datetime DEFAULT NULL,
  `t1` date DEFAULT NULL,
  `t2` time DEFAULT NULL,
  `doub` decimal(3,2) DEFAULT NULL,
  `is_c` bit(1) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name1` (`name1`),
  UNIQUE KEY `fl` (`fl`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `test_init` */

insert  into `test_init`(`id`,`magenet_list`,`name1`,`ag`,`fl`,`d1`,`d2`,`t1`,`t2`,`doub`,`is_c`,`lon`) values
(1,'\0sr\0\Zjava.util.Arrays$ArrayList伽<就??\0[\0at\0[Ljava/lang/Object;xpur\0[Ljava.lang.String;V玳{G\0\0xp\0\0\0t\0testt\0sdfxc;l',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(2,'\0sr\0\Zjava.util.Arrays$ArrayList伽<就??\0[\0at\0[Ljava/lang/Object;xpur\0[Ljava.lang.String;V玳{G\0\0xp\0\0\0t\0testt\0sdfxc;l',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(3,'\0sr\0\Zjava.util.Arrays$ArrayList伽<就??\0[\0at\0[Ljava/lang/Object;xpur\0[Ljava.lang.String;V玳{G\0\0xp\0\0\0t\0testt\0sdfxc;l',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(4,'[\"test\",\"sdfxc;l\"]',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(5,'[\"test\",\"sdfxc;l\"]',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(6,'[\"test\",\"sdfxc;l\"]',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(7,'[\"test\",\"sdfxc;l\"]',NULL,12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `ultimate_extra_data` */

DROP TABLE IF EXISTS `ultimate_extra_data`;

CREATE TABLE `ultimate_extra_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `extra_key` varchar(100) DEFAULT NULL,
  `data` blob,
  `type_name` varchar(100) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `exprie_minute` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `ultimate_extra_data` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
