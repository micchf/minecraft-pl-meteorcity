-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Creato il: Giu 07, 2022 alle 21:57
-- Versione del server: 10.3.34-MariaDB-0+deb10u1
-- Versione PHP: 7.3.31-1~deb10u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `meteorcity`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `cities`
--

CREATE TABLE `cities` (
  `ID` int(11) NOT NULL,
  `PIL` double NOT NULL,
  `cityTemplate` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `locations`
--

CREATE TABLE `locations` (
  `ID` int(11) NOT NULL,
  `minX` int(11) NOT NULL,
  `minY` int(11) NOT NULL,
  `minZ` int(11) NOT NULL,
  `maxX` int(11) NOT NULL,
  `maxY` int(11) NOT NULL,
  `maxZ` int(11) NOT NULL,
  `world` varchar(100) NOT NULL,
  `type` varchar(100) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `IDCity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `members`
--

CREATE TABLE `members` (
  `UUID` varchar(100) NOT NULL,
  `role` varchar(100) NOT NULL,
  `IDCity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `spawns`
--

CREATE TABLE `spawns` (
  `ID` int(11) NOT NULL,
  `X` int(11) NOT NULL,
  `Y` int(11) NOT NULL,
  `Z` int(11) NOT NULL,
  `world` varchar(100) NOT NULL,
  `type` varchar(100) NOT NULL,
  `IDCity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `spawns`
--

INSERT INTO `spawns` (`ID`, `X`, `Y`, `Z`, `world`, `type`, `IDCity`) VALUES
(1, 1000, 50, 1000, 'Cities', 'LAST_POINT', NULL);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `cities`
--
ALTER TABLE `cities`
  ADD PRIMARY KEY (`ID`);

--
-- Indici per le tabelle `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FK_IDCity_locations` (`IDCity`);

--
-- Indici per le tabelle `members`
--
ALTER TABLE `members`
  ADD PRIMARY KEY (`UUID`),
  ADD KEY `FK_IDCity_members` (`IDCity`);

--
-- Indici per le tabelle `spawns`
--
ALTER TABLE `spawns`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FK_IDCity_spawns` (`IDCity`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `cities`
--
ALTER TABLE `cities`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `locations`
--
ALTER TABLE `locations`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `spawns`
--
ALTER TABLE `spawns`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `locations`
--
ALTER TABLE `locations`
  ADD CONSTRAINT `FK_IDCity_locations` FOREIGN KEY (`IDCity`) REFERENCES `cities` (`ID`);

--
-- Limiti per la tabella `members`
--
ALTER TABLE `members`
  ADD CONSTRAINT `FK_IDCity_members` FOREIGN KEY (`IDCity`) REFERENCES `cities` (`ID`);

--
-- Limiti per la tabella `spawns`
--
ALTER TABLE `spawns`
  ADD CONSTRAINT `FK_IDCity_spawns` FOREIGN KEY (`IDCity`) REFERENCES `cities` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
