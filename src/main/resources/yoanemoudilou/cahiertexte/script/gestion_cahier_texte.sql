CREATE TABLE IF NOT EXISTS filieres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    nom VARCHAR(150) NOT NULL,
    CONSTRAINT uq_filieres_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('CHEF_DEPARTEMENT', 'ENSEIGNANT', 'RESPONSABLE_CLASSE') NOT NULL,
    valide BOOLEAN NOT NULL DEFAULT FALSE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_utilisateurs_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_classe VARCHAR(100) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    filiere_id INT NOT NULL,
    CONSTRAINT uq_classes_nom_niveau UNIQUE (nom_classe, niveau),
    INDEX idx_classes_filiere_id (filiere_id),
    CONSTRAINT fk_classes_filieres
        FOREIGN KEY (filiere_id)
        REFERENCES filieres(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS responsables_classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT NOT NULL,
    classe_id INT NOT NULL,
    CONSTRAINT uq_responsables_classes_utilisateur UNIQUE (utilisateur_id),
    CONSTRAINT uq_responsables_classes_classe UNIQUE (classe_id),
    INDEX idx_responsables_classes_utilisateur_id (utilisateur_id),
    INDEX idx_responsables_classes_classe_id (classe_id),
    CONSTRAINT fk_responsables_classes_utilisateurs
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_responsables_classes_classes
        FOREIGN KEY (classe_id)
        REFERENCES classes(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cahiers_texte (
    id INT AUTO_INCREMENT PRIMARY KEY,
    classe_id INT NOT NULL,
    annee_scolaire VARCHAR(20) NOT NULL,
    semestre ENUM('SEMESTRE_1', 'SEMESTRE_2') NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cahiers_texte_classe_periode UNIQUE (classe_id, annee_scolaire, semestre),
    INDEX idx_cahiers_texte_classe_id (classe_id),
    CONSTRAINT fk_cahiers_texte_classes
        FOREIGN KEY (classe_id)
        REFERENCES classes(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    intitule VARCHAR(150) NOT NULL,
    volume_horaire INT NOT NULL,
    classe_id INT NOT NULL,
    CONSTRAINT uq_cours_code UNIQUE (code),
    INDEX idx_cours_classe_id (classe_id),
    CONSTRAINT chk_cours_volume_horaire CHECK (volume_horaire > 0),
    CONSTRAINT fk_cours_classes
        FOREIGN KEY (classe_id)
        REFERENCES classes(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS affectations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enseignant_id INT NOT NULL,
    cours_id INT NOT NULL,
    CONSTRAINT uq_affectations_enseignant_cours UNIQUE (enseignant_id, cours_id),
    INDEX idx_affectations_enseignant_id (enseignant_id),
    INDEX idx_affectations_cours_id (cours_id),
    CONSTRAINT fk_affectations_enseignants
        FOREIGN KEY (enseignant_id)
        REFERENCES utilisateurs(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_affectations_cours
        FOREIGN KEY (cours_id)
        REFERENCES cours(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS seances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cahier_texte_id INT NOT NULL,
    cours_id INT NOT NULL,
    enseignant_id INT NOT NULL,
    date_seance DATE NOT NULL,
    heure_seance TIME NOT NULL,
    duree INT NOT NULL,
    contenu TEXT NOT NULL,
    observations TEXT NULL,
    statut ENUM('EN_ATTENTE', 'VALIDEE', 'REJETEE') NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire_validation TEXT NULL,
    INDEX idx_seances_cahier_texte_id (cahier_texte_id),
    INDEX idx_seances_cours_id (cours_id),
    INDEX idx_seances_enseignant_id (enseignant_id),
    INDEX idx_seances_statut (statut),
    INDEX idx_seances_date_heure (date_seance, heure_seance),
    CONSTRAINT chk_seances_duree CHECK (duree > 0),
    CONSTRAINT fk_seances_cahiers_texte
        FOREIGN KEY (cahier_texte_id)
        REFERENCES cahiers_texte(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_seances_cours
        FOREIGN KEY (cours_id)
        REFERENCES cours(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_seances_enseignants
        FOREIGN KEY (enseignant_id)
        REFERENCES utilisateurs(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    destinataire_id INT NOT NULL,
    titre VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    lue BOOLEAN NOT NULL DEFAULT FALSE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notifications_destinataire_id (destinataire_id),
    INDEX idx_notifications_lue (lue),
    CONSTRAINT fk_notifications_utilisateurs
        FOREIGN KEY (destinataire_id)
        REFERENCES utilisateurs(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
