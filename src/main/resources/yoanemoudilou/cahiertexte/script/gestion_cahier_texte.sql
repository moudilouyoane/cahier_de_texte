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
    cours_id INT NOT NULL,
    enseignant_id INT NOT NULL,
    date_seance DATE NOT NULL,
    heure_seance TIME NOT NULL,
    duree INT NOT NULL,
    contenu TEXT NOT NULL,
    observations TEXT NULL,
    statut ENUM('EN_ATTENTE', 'VALIDEE', 'REJETEE') NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire_validation TEXT NULL,
    INDEX idx_seances_cours_id (cours_id),
    INDEX idx_seances_enseignant_id (enseignant_id),
    INDEX idx_seances_statut (statut),
    INDEX idx_seances_date_heure (date_seance, heure_seance),
    CONSTRAINT chk_seances_duree CHECK (duree > 0),
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

INSERT INTO filieres (code, nom)
SELECT * FROM (
    SELECT 'GI' AS code, 'Genie Informatique' AS nom
    UNION ALL
    SELECT 'GC', 'Genie Civil'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM filieres);

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, valide, actif)
SELECT * FROM (
    SELECT 'Traore', 'Ibrahima', 'responsable@ensa.edu',
           '$2a$12$p/UomAb/cPH8g.OfT9MITeNR9ouhqspTG7z2HonITIIY2EtoYfGp6',
           'CHEF_DEPARTEMENT', TRUE, TRUE
    UNION ALL
    SELECT 'Coulibaly', 'Moussa', 'chef.dept@ensa.edu',
           '$2a$12$Wgf6vlNFN5vQIV/R.NPFO.mNFaefhKabGNACCqIpFAzNpjOCTTEzu',
           'CHEF_DEPARTEMENT', TRUE, TRUE
    UNION ALL
    SELECT 'Diallo', 'Fatou', 'enseignant@ensa.edu',
           '$2a$12$7br.TBBxyEndRnezZEVby.324lldWYtiZ3fQ2fPOCVTLIiODZVc1q',
           'ENSEIGNANT', TRUE, TRUE
    UNION ALL
    SELECT 'Barry', 'Aminata', 'responsable.classe@ensa.edu',
           '$2a$12$7br.TBBxyEndRnezZEVby.324lldWYtiZ3fQ2fPOCVTLIiODZVc1q',
           'RESPONSABLE_CLASSE', TRUE, TRUE
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs);

INSERT INTO classes (nom_classe, niveau, filiere_id)
SELECT * FROM (
    SELECT 'L1-GI', 'Licence 1', (SELECT id FROM filieres WHERE code = 'GI')
    UNION ALL
    SELECT 'L2-GI', 'Licence 2', (SELECT id FROM filieres WHERE code = 'GI')
    UNION ALL
    SELECT 'L1-GC', 'Licence 1', (SELECT id FROM filieres WHERE code = 'GC')
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM classes);

INSERT INTO cours (code, intitule, volume_horaire, classe_id)
SELECT * FROM (
    SELECT 'ALG101', 'Algorithmique', 60, (SELECT id FROM classes WHERE nom_classe = 'L1-GI' LIMIT 1)
    UNION ALL
    SELECT 'C101', 'Programmation C', 45, (SELECT id FROM classes WHERE nom_classe = 'L1-GI' LIMIT 1)
    UNION ALL
    SELECT 'SD201', 'Structures de donnees', 60, (SELECT id FROM classes WHERE nom_classe = 'L2-GI' LIMIT 1)
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM cours);

INSERT INTO affectations (enseignant_id, cours_id)
SELECT * FROM (
    SELECT
        (SELECT id FROM utilisateurs WHERE email = 'enseignant@ensa.edu' LIMIT 1),
        (SELECT id FROM cours WHERE code = 'ALG101' LIMIT 1)
    UNION ALL
    SELECT
        (SELECT id FROM utilisateurs WHERE email = 'enseignant@ensa.edu' LIMIT 1),
        (SELECT id FROM cours WHERE code = 'C101' LIMIT 1)
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM affectations);

INSERT INTO seances (cours_id, enseignant_id, date_seance, heure_seance, duree, contenu, observations, statut, commentaire_validation)
SELECT * FROM (
    SELECT
        (SELECT id FROM cours WHERE code = 'ALG101' LIMIT 1),
        (SELECT id FROM utilisateurs WHERE email = 'enseignant@ensa.edu' LIMIT 1),
        DATE '2026-03-10',
        TIME '08:00:00',
        120,
        'Introduction aux algorithmes de tri',
        'Seance initiale',
        'VALIDEE',
        'RAS'
    UNION ALL
    SELECT
        (SELECT id FROM cours WHERE code = 'C101' LIMIT 1),
        (SELECT id FROM utilisateurs WHERE email = 'enseignant@ensa.edu' LIMIT 1),
        DATE '2026-03-12',
        TIME '10:00:00',
        120,
        'Variables, types et structures de controle',
        NULL,
        'EN_ATTENTE',
        NULL
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM seances);
