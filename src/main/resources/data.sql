-- =========================================================================
-- SCRIPT DE PEUPLEMENT AUTOMATIQUE DE LA BASE DE DONNÉES (MODE UPDATE)
-- =========================================================================

-- 1. NETTOYAGE SÉCURISÉ DES TABLES (CONTOURNEMENT DES RESTRICTIONS TRUNCATE)
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM ao7dvw_honeygroup_db.payment;
DELETE FROM ao7dvw_honeygroup_db.booking;
DELETE FROM ao7dvw_honeygroup_db.session;
DELETE FROM ao7dvw_honeygroup_db.prestation;
DELETE FROM ao7dvw_honeygroup_db.photo;
DELETE FROM ao7dvw_honeygroup_db.pole;
DELETE FROM ao7dvw_honeygroup_db.user;

-- Réinitialisation des compteurs d'auto-incrément pour repartir à 1
ALTER TABLE ao7dvw_honeygroup_db.payment AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.booking AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.session AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.prestation AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.photo AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.pole AUTO_INCREMENT = 1;
ALTER TABLE ao7dvw_honeygroup_db.user AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================================
-- 2. INSERTION DE L'UTILISATEUR TECHNIQUE DE TEST (id = 10)
-- =========================================================================
-- Le mot de passe pour ces trois comptes est 'password' (chiffré en BCrypt)
INSERT IGNORE INTO ao7dvw_honeygroup_db.user (id, nom, prenom, email, password, role) VALUES 
(11, 'Client', 'Honey', 'client@honeygroup.fr', '{noop}password', 'CLIENT'),
(12, 'Manager', 'Honey', 'manager@honeygroup.fr', '{noop}password', 'MANAGER'),
(13, 'Admin', 'Honey', 'admin@honeygroup.fr', '{noop}password', 'ADMIN');

-- =========================================================================
-- 3. INSERTION DES PÔLES D'ACTIVITÉ (id_pole de 1 à 4)
-- =========================================================================
INSERT IGNORE INTO ao7dvw_honeygroup_db.pole (id_pole, nom_pole, description) VALUES
(1, 'IT & Digital', 'Solutions technologiques sur mesure. Développement web et cloud pour propulser votre entreprise dans l''ère numérique.'),
(2, 'Ecotourisme', 'Découvrez Madagascar autrement. Des circuits authentiques et durables en immersion culturelle au cœur de la Grande Île.'), 
(3, 'Événementiel', 'Donnez vie à vos projets. Organisation d''événements corporate et privés avec une gestion logistique de pointe.'), 
(4, 'Formation', 'Investissez dans le talent. Des programmes de formation innovants pour booster les compétences de demain.');

-- =========================================================================
-- 4. INSERTION DES PHOTOS (id_photo de 1 à 10)
-- =========================================================================
INSERT IGNORE INTO ao7dvw_honeygroup_db.photo (id_photo, url_fichier, legende) VALUES
-- Photos Pôle IT (1 à 8)
(1, '/media/services/medium_rectanglehebergement_fr1.jpg', 'Hébergement Web (Standard)'),
(2, '/media/services/medium_rectanglehebergement_fr1_OGcCqaI.jpg', 'Cloud Managé (Haute Performance)'),
(3, '/media/services/logo_alwaysdata_1RfTvq2.png', 'Services Cloud'),
(4, '/media/services/images.jpg', 'Solutions Bureautiques (kSuite)'),
(5, '/media/services/kdrive-solo_001.jpg', 'Stockage Sécurisé (kDrive)'),
(6, '/media/services/llmapi-color.png', 'Intelligence Artificielle (API LLM)'),
(7, '/media/services/medium_rectanglecloud_fr1.jpg', 'Public Cloud (Infrastructure)'),
(8, '/media/services/medium_rectanglecloud_fr1_fsQgWhs.jpg', 'Serveurs VPS Cloud'),
-- Photos Pôle Écotourisme (9 à 10)
(9, '/media/services/madagascar_circuit_nord.jpg', 'Circuit Madagascar Nord Sauvage'),
(10, '/media/services/madagascar_baobab.jpg', 'Allée des Baobabs & Trek Sud');

-- =========================================================================
-- 5. INSERTION DES PRESTATIONS (id_prestation de 1 à 10)
-- =========================================================================
INSERT IGNORE INTO ao7dvw_honeygroup_db.prestation 
(id_prestation, id_pole, id_photo, titre_service, description, prix_base, statut, date_creation)
VALUES
-- Prestations du Pôle IT (id_pole = 1)
(1, 1, 1, 'Hébergement Web (Standard)', 'Solution d’hébergement web fiable pour sites vitrines et PME.', 100.0, 'ACTIF', NOW()),
(2, 1, 2, 'Cloud Managé (Haute Performance)', 'Infrastructure cloud optimisée avec haute disponibilité et performance.', 250.0, 'ACTIF', NOW()),
(3, 1, 3, 'Services Cloud', 'Hébergement haute performance avec avantages partenaires (remise 10%).', 50.0, 'ACTIF', NOW()),
(4, 1, 4, 'Solutions Bureautiques (kSuite)', 'Suite collaborative complète pour email, documents et productivité.', 30.0, 'ACTIF', NOW()),
(5, 1, 5, 'Stockage Sécurisé (kDrive)', 'Stockage cloud sécurisé pour fichiers professionnels et personnels.', 10.0, 'ACTIF', NOW()),
(6, 1, 6, 'Intelligence Artificielle (API LLM)', 'API LLM pour intégrer l’intelligence artificielle dans vos applications.', 200.0, 'ACTIF', NOW()),
(7, 1, 7, 'Public Cloud (Infrastructure)', 'Infrastructure cloud publique scalable et performante.', 300.0, 'ACTIF', NOW()),
(8, 1, 8, 'Serveurs VPS Cloud', 'Serveurs VPS cloud flexibles et performants pour projets avancés.', 150.0, 'ACTIF', NOW()),

-- Prestations du Pôle Écotourisme (id_pole = 2)
(9, 2, 9, 'Trek & Découverte : Le Nord Sauvage', 'Une aventure immersive de Diego-Suarez à Nosy Be, découvrez les Tsingy et la faune locale.', 1200.0, 'ACTIF', NOW()),
(10, 2, 10, 'L''Allée des Baobabs et Majestueux Sud', 'Parcours photographique et solidaire à travers Morondava et les parcs nationaux du Sud.', 1450.0, 'ACTIF', NOW());

-- =========================================================================
-- 6. INSERTION DES SESSIONS (id_session de 1 à 3)
-- =========================================================================
INSERT IGNORE INTO ao7dvw_honeygroup_db.session 
(prestation_id, date_debut, date_fin, capacite_max, nb_inscrits, statut_session)
VALUES
-- Sessions pour le Trek Nord Sauvage (id_prestation = 9)
(9, '2026-05-15 08:00:00', '2026-05-30 18:00:00', 12, 2, 'EN_COURS'),
(9, '2026-09-10 08:00:00', '2026-09-24 18:00:00', 15, 0, 'OUVERT'),

-- Sessions pour l'Allée des Baobabs (id_prestation = 10)
(10, '2026-08-05 07:00:00', '2026-08-20 19:00:00', 8, 1, 'OUVERT');

-- =========================================================================
-- 7. INSERTION DES RÉSERVATIONS DE TEST (Booking)
-- =========================================================================
INSERT IGNORE INTO ao7dvw_honeygroup_db.booking 
(user_id, session_id, date_creation_resa, statut, nb_places, montant_total)
VALUES
-- 2 places pour le Trek Nord (id_session = 1) -> 2400.00€
(11, 1, NOW(), 'CONFIRME', 2, 2400.00),

-- 1 place pour l'Allée des Baobabs (id_session = 3) -> 1450.00€
(11, 3, NOW(), 'EN_ATTENTE_PAIEMENT', 1, 1450.00);