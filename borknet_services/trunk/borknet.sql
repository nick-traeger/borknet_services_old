BACKUP OF:1151285665
-- MySQL dump 10.9
--
-- Host: localhost    Database: borknet
-- ------------------------------------------------------
-- Server version	4.1.11-Debian_4sarge4-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auths`
--

DROP TABLE IF EXISTS `auths`;
CREATE TABLE `auths` (
  `index` int(11) NOT NULL auto_increment,
  `authnick` text NOT NULL,
  `pass` text NOT NULL,
  `mail` text NOT NULL,
  `level` text NOT NULL,
  `suspended` text NOT NULL,
  `last` text NOT NULL,
  `info` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `info` (`info`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `chanfix`
--

DROP TABLE IF EXISTS `chanfix`;
CREATE TABLE `chanfix` (
  `index` int(11) NOT NULL auto_increment,
  `channel` text NOT NULL,
  `host` text NOT NULL,
  `points` text NOT NULL,
  `last` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `g_tickets`
--

DROP TABLE IF EXISTS `g_tickets`;
CREATE TABLE `g_tickets` (
  `index` int(11) NOT NULL auto_increment,
  `user` text NOT NULL,
  `channel` text NOT NULL,
  `time` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `user` (`user`,`channel`,`time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_access`
--

DROP TABLE IF EXISTS `q_access`;
CREATE TABLE `q_access` (
  `index` int(11) NOT NULL auto_increment,
  `user` text NOT NULL,
  `channel` text NOT NULL,
  `flags` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_bans`
--

DROP TABLE IF EXISTS `q_bans`;
CREATE TABLE `q_bans` (
  `index` int(11) NOT NULL auto_increment,
  `name` text NOT NULL,
  `host` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_challenge`
--

DROP TABLE IF EXISTS `q_challenge`;
CREATE TABLE `q_challenge` (
  `index` int(11) NOT NULL auto_increment,
  `user` text NOT NULL,
  `challenge` text NOT NULL,
  `time` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_channels`
--

DROP TABLE IF EXISTS `q_channels`;
CREATE TABLE `q_channels` (
  `index` int(11) NOT NULL auto_increment,
  `name` text NOT NULL,
  `flags` text NOT NULL,
  `modes` text NOT NULL,
  `welcome` text NOT NULL,
  `topic` text NOT NULL,
  `last` text NOT NULL,
  `chanlimit` text NOT NULL,
  `suspended` text NOT NULL,
  `chankey` text NOT NULL,
  `level` text NOT NULL,
  `owner` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `limit` (`chanlimit`),
  FULLTEXT KEY `suspended` (`suspended`),
  FULLTEXT KEY `key` (`chankey`),
  FULLTEXT KEY `level` (`level`),
  FULLTEXT KEY `owner` (`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_fakeusers`
--

DROP TABLE IF EXISTS `q_fakeusers`;
CREATE TABLE `q_fakeusers` (
  `index` int(11) NOT NULL auto_increment,
  `numer` text NOT NULL,
  `nick` text NOT NULL,
  `ident` text NOT NULL,
  `host` text NOT NULL,
  `desc` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_glines`
--

DROP TABLE IF EXISTS `q_glines`;
CREATE TABLE `q_glines` (
  `index` int(11) NOT NULL auto_increment,
  `gline` text NOT NULL,
  `timeset` text NOT NULL,
  `timeexp` text NOT NULL,
  `reason` text NOT NULL,
  `oper` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `reason` (`reason`,`oper`),
  FULLTEXT KEY `timeset` (`timeset`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_jupes`
--

DROP TABLE IF EXISTS `q_jupes`;
CREATE TABLE `q_jupes` (
  `index` int(11) NOT NULL auto_increment,
  `jupe` text NOT NULL,
  `numer` text NOT NULL,
  `timeset` text NOT NULL,
  `timeexp` text NOT NULL,
  `reason` text NOT NULL,
  `oper` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `reason` (`reason`,`oper`),
  FULLTEXT KEY `timeset` (`timeset`),
  FULLTEXT KEY `numer` (`numer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_mails`
--

DROP TABLE IF EXISTS `q_mails`;
CREATE TABLE `q_mails` (
  `index` int(11) NOT NULL auto_increment,
  `mail` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_pwrequest`
--

DROP TABLE IF EXISTS `q_pwrequest`;
CREATE TABLE `q_pwrequest` (
  `index` int(11) NOT NULL auto_increment,
  `user` text NOT NULL,
  `pass` text NOT NULL,
  `code` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `code` (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `q_trusts`
--

DROP TABLE IF EXISTS `q_trusts`;
CREATE TABLE `q_trusts` (
  `index` int(11) NOT NULL auto_increment,
  `host` text NOT NULL,
  `users` text NOT NULL,
  `auth` text NOT NULL,
  `time` text NOT NULL,
  `need-ident` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `reason` (`time`,`need-ident`),
  FULLTEXT KEY `timeset` (`users`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `s_channels`
--

DROP TABLE IF EXISTS `s_channels`;
CREATE TABLE `s_channels` (
  `index` int(11) NOT NULL auto_increment,
  `name` text NOT NULL,
  `flags` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `s_kills`
--

DROP TABLE IF EXISTS `s_kills`;
CREATE TABLE `s_kills` (
  `index` int(11) NOT NULL auto_increment,
  `kills` text NOT NULL,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `s_users`
--

DROP TABLE IF EXISTS `s_users`;
CREATE TABLE `s_users` (
  `index` int(11) NOT NULL auto_increment,
  `username` text NOT NULL,
  `points` text NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `message` (`message`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `servers`
--

DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `index` int(11) NOT NULL auto_increment,
  `numer` text NOT NULL,
  `host` text NOT NULL,
  `hub` text NOT NULL,
  `service` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `services` (`service`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `u_connect4`
--

DROP TABLE IF EXISTS `u_connect4`;
CREATE TABLE `u_connect4` (
  `id` text NOT NULL,
  `user1` text NOT NULL,
  `user2` text NOT NULL,
  `field` text NOT NULL,
  `turn` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `userchans`
--

DROP TABLE IF EXISTS `userchans`;
CREATE TABLE `userchans` (
  `index` int(11) NOT NULL auto_increment,
  `channel` text NOT NULL,
  `user` text NOT NULL,
  `modes` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `modes` (`modes`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `index` int(11) NOT NULL auto_increment,
  `numer` text NOT NULL,
  `nick` text NOT NULL,
  `host` text NOT NULL,
  `modes` text NOT NULL,
  `authnick` text NOT NULL,
  `isop` text NOT NULL,
  `server` text NOT NULL,
  `ip` text NOT NULL,
  `fake` text NOT NULL,
  PRIMARY KEY  (`index`),
  FULLTEXT KEY `server` (`server`),
  FULLTEXT KEY `ip` (`ip`),
  FULLTEXT KEY `fake` (`fake`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

