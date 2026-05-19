-- ============================================================
--  UIR Handicap — Script SQL — Exécuter dans MySQL Workbench
-- ============================================================

CREATE DATABASE IF NOT EXISTS uir_handicap
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE uir_handicap;

CREATE TABLE IF NOT EXISTS utilisateur (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe   VARCHAR(255) NOT NULL,
    role           ENUM('ADMIN','HANDICAPEE') NOT NULL,
    est_actif      BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS demande (
    id_demande       INT AUTO_INCREMENT PRIMARY KEY,
    date_creation    DATE NOT NULL DEFAULT (CURRENT_DATE()),
    type_demande     VARCHAR(150) NOT NULL,
    description      TEXT NOT NULL,
    statut           ENUM('En cours','Acceptée','Refusée','Archivée') NOT NULL DEFAULT 'En cours',
    id_personne      INT NOT NULL,
    id_admin         INT NULL,
    est_supprime     BOOLEAN DEFAULT FALSE,
    date_suppression DATE NULL,
    FOREIGN KEY (id_personne) REFERENCES utilisateur(id_utilisateur),
    FOREIGN KEY (id_admin)    REFERENCES utilisateur(id_utilisateur)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reclamation (
    id_reclamation   INT AUTO_INCREMENT PRIMARY KEY,
    date_reclamation DATE NOT NULL DEFAULT (CURRENT_DATE()),
    objet            VARCHAR(200) NOT NULL,
    description      TEXT NOT NULL,
    statut           ENUM('En attente','En cours','Résolue','Archivée') NOT NULL DEFAULT 'En attente',
    id_personne      INT NOT NULL,
    id_admin         INT NULL,
    est_supprime     BOOLEAN DEFAULT FALSE,
    date_suppression DATE NULL,
    FOREIGN KEY (id_personne) REFERENCES utilisateur(id_utilisateur),
    FOREIGN KEY (id_admin)    REFERENCES utilisateur(id_utilisateur)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS archive (
    id_archive      INT AUTO_INCREMENT PRIMARY KEY,
    date_archivage  DATE NOT NULL DEFAULT (CURRENT_DATE()),
    type_element    ENUM('Demande','Réclamation') NOT NULL,
    id_demande      INT NULL,
    id_reclamation  INT NULL,
    FOREIGN KEY (id_demande)     REFERENCES demande(id_demande),
    FOREIGN KEY (id_reclamation) REFERENCES reclamation(id_reclamation)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS historique_action (
    id_action      INT AUTO_INCREMENT PRIMARY KEY,
    date_action    DATETIME NOT NULL DEFAULT NOW(),
    action         VARCHAR(100) NOT NULL,
    details        TEXT NULL,
    id_demande     INT NULL,
    id_reclamation INT NULL,
    FOREIGN KEY (id_demande)     REFERENCES demande(id_demande),
    FOREIGN KEY (id_reclamation) REFERENCES reclamation(id_reclamation)
) ENGINE=InnoDB;

-- ============================================================
--  DONNÉES DE TEST
--  IMPORTANT : Générez de vrais hash BCrypt avec :
--  PasswordUtil.hashPassword("admin123")
--  Remplacez les hash ci-dessous par les vrais hash générés
-- ============================================================
-- INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
-- ('Admin', 'Super', 'admin@uir.ac.ma', '<hash_bcrypt_ici>', 'ADMIN'),
-- ('Etudiant', 'Ilias', 'ilias@uir.ac.ma', '<hash_bcrypt_ici>', 'HANDICAPEE');
