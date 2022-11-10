-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 10 nov. 2022 à 02:28
-- Version du serveur :  5.7.31
-- Version de PHP : 7.3.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `suivi_financier_auth`
--

-- --------------------------------------------------------

--
-- Structure de la table `password`
--

DROP TABLE IF EXISTS `password`;
CREATE TABLE IF NOT EXISTS `password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `password` varchar(128) NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `secure_question`
--

DROP TABLE IF EXISTS `secure_question`;
CREATE TABLE IF NOT EXISTS `secure_question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(130) NOT NULL,
  `user_id` int(11) NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `secure_response`
--

DROP TABLE IF EXISTS `secure_response`;
CREATE TABLE IF NOT EXISTS `secure_response` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `secure_question_id` int(11) NOT NULL,
  `response` text NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  `question` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `token`
--

DROP TABLE IF EXISTS `token`;
CREATE TABLE IF NOT EXISTS `token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token_context` varchar(128) NOT NULL,
  `token` text NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `type_profile`
--

DROP TABLE IF EXISTS `type_profile`;
CREATE TABLE IF NOT EXISTS `type_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_profile` varchar(32) NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


-- --------------------------------------------------------

--
-- Structure de la table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(32) NOT NULL,
  `prenom` varchar(32) NOT NULL,
  `email` varchar(128) NOT NULL,
  `date_naissance` date NOT NULL,
  `adresse` text NOT NULL,
  `ville` varchar(64) NOT NULL,
  `zip` varchar(16) NOT NULL,
  `type_profil` int(11) NOT NULL,
  `actif` int(11) NOT NULL,
  `password` int(11) NOT NULL,
  `date_creation` datetime NOT NULL,
  `date_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
