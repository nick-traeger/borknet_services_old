-- phpMyAdmin SQL Dump
-- version 2.11.8.1deb5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 01, 2009 at 11:58 PM
-- Server version: 5.0.51
-- PHP Version: 5.2.6-1+lenny2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `borknet`
--

-- --------------------------------------------------------

--
-- Table structure for table `auths`
--

CREATE TABLE IF NOT EXISTS `auths` (
  `authnick` varchar(15) NOT NULL,
  `pass` varchar(32) NOT NULL,
  `mail` varchar(100) NOT NULL,
  `level` int(11) NOT NULL,
  `suspended` tinyint(1) NOT NULL,
  `last` bigint(20) NOT NULL,
  `info` varchar(250) NOT NULL,
  `userflags` varchar(10) NOT NULL,
  `vhost` varchar(150) NOT NULL,
  PRIMARY KEY  (`authnick`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `chanfix`
--

CREATE TABLE IF NOT EXISTS `chanfix` (
  `channel` varchar(100) NOT NULL,
  `host` varchar(100) NOT NULL,
  `points` int(11) NOT NULL,
  `last` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `h_tickets`
--

CREATE TABLE IF NOT EXISTS `h_tickets` (
  `user` varchar(15) NOT NULL,
  `channel` varchar(100) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY  (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `m_messages`
--

CREATE TABLE IF NOT EXISTS `m_messages` (
  `index` int(11) NOT NULL auto_increment,
  `authname` varchar(15) NOT NULL,
  `from` varchar(15) NOT NULL,
  `message` text NOT NULL,
  `senttime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`index`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Table structure for table `q_access`
--

CREATE TABLE IF NOT EXISTS `q_access` (
  `user` varchar(15) NOT NULL,
  `channel` varchar(100) NOT NULL,
  `flags` varchar(10) NOT NULL,
  PRIMARY KEY  (`user`,`channel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_bans`
--

CREATE TABLE IF NOT EXISTS `q_bans` (
  `name` varchar(100) NOT NULL,
  `host` varchar(100) NOT NULL,
  PRIMARY KEY  (`name`,`host`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_challenge`
--

CREATE TABLE IF NOT EXISTS `q_challenge` (
  `user` varchar(5) NOT NULL,
  `challenge` varchar(32) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY  (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_channels`
--

CREATE TABLE IF NOT EXISTS `q_channels` (
  `name` varchar(100) NOT NULL,
  `flags` varchar(30) NOT NULL,
  `modes` varchar(20) NOT NULL,
  `welcome` varchar(250) NOT NULL,
  `topic` varchar(250) NOT NULL,
  `last` bigint(20) NOT NULL,
  `chanlimit` int(11) NOT NULL,
  `suspended` tinyint(1) NOT NULL,
  `chankey` varchar(50) NOT NULL,
  `level` int(11) NOT NULL,
  `owner` varchar(15) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_fakeusers`
--

CREATE TABLE IF NOT EXISTS `q_fakeusers` (
  `numer` varchar(3) NOT NULL,
  `nick` varchar(15) NOT NULL,
  `ident` varchar(50) NOT NULL,
  `host` varchar(250) NOT NULL,
  `desc` varchar(250) NOT NULL,
  PRIMARY KEY  (`numer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_glines`
--

CREATE TABLE IF NOT EXISTS `q_glines` (
  `gline` varchar(100) NOT NULL,
  `timeset` bigint(20) NOT NULL,
  `timeexp` bigint(20) NOT NULL,
  `reason` varchar(250) NOT NULL,
  `oper` varchar(15) NOT NULL,
  PRIMARY KEY  (`gline`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_jupes`
--

CREATE TABLE IF NOT EXISTS `q_jupes` (
  `jupe` varchar(100) NOT NULL,
  `numer` varchar(5) NOT NULL,
  `timeset` int(11) NOT NULL,
  `timeexp` int(11) NOT NULL,
  `reason` varchar(250) NOT NULL,
  `oper` varchar(15) NOT NULL,
  PRIMARY KEY  (`jupe`),
  KEY `numer` (`numer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_mails`
--

CREATE TABLE IF NOT EXISTS `q_mails` (
  `mail` varchar(100) NOT NULL,
  PRIMARY KEY  (`mail`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_pwrequest`
--

CREATE TABLE IF NOT EXISTS `q_pwrequest` (
  `user` varchar(15) NOT NULL,
  `pass` varchar(7) NOT NULL,
  `code` varchar(100) NOT NULL,
  PRIMARY KEY  (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_trusts`
--

CREATE TABLE IF NOT EXISTS `q_trusts` (
  `host` varchar(15) NOT NULL,
  `users` int(11) NOT NULL,
  `auth` varchar(15) NOT NULL,
  `time` int(11) NOT NULL,
  `need-ident` tinyint(1) NOT NULL,
  PRIMARY KEY  (`host`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `q_variables`
--

CREATE TABLE IF NOT EXISTS `q_variables` (
  `info` varchar(500) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `s_channels`
--

CREATE TABLE IF NOT EXISTS `s_channels` (
  `name` varchar(100) NOT NULL,
  `flags` varchar(5) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `userchans`
--

CREATE TABLE IF NOT EXISTS `userchans` (
  `channel` varchar(100) NOT NULL,
  `user` varchar(5) NOT NULL,
  `modes` varchar(3) NOT NULL,
  KEY `chanuser` (`channel`,`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `x_stats`
--

CREATE TABLE IF NOT EXISTS `x_stats` (
  `maxusers` int(11) NOT NULL,
  `maxopers` int(11) NOT NULL,
  `maxservers` int(11) NOT NULL,
  `maxchannels` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
