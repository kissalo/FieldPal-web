-- USUARIOS (Admin y Jugador)
-- ============================================
-- NOTA: ORG_ID de este admin se completa con el UPDATE de más abajo (después de
-- crear la fila 1 de ORGANIZATIONS), para no romper la referencia de llave foránea.
INSERT INTO USERS (ID, NAME, EMAIL, PHONE, PASSWORD, ROLE, ACTIVE) VALUES (1, 'Admin FieldPal', 'admin@fieldpal.com', '0990000001', 'j1c37tPLfzwORcP3Fzb2Ig==', 'ADMIN', true);
INSERT INTO USERS (ID, NAME, EMAIL, PHONE, PASSWORD, ROLE, ACTIVE) VALUES (2, 'Carlos Mendoza', 'jugador@fieldpal.com', '0991234567', '22uXWwCx4jyGvRCQMdQz4w==', 'PLAYER', true);

-- ORGANIZACIONES (complejos deportivos)
-- ============================================
INSERT INTO ORGANIZATIONS (ID, NAME, ZONE, ADDRESS, PHONE, EMAIL, RATING, DESCRIPTION, COURT_COUNT, LATITUDE, LONGITUDE) VALUES (1, 'Complejo Deportivo Loja Norte', 'NORTE', 'Av. Universitaria y Circunvalacion', '+593987654321', 'contacto@lojanorte.com', 4.5, 'Complejo con canchas de futbol y voley', 2, -3.9928, -79.2033);

-- Vincular al admin predefinido con SU organización (antes este vínculo no existía
-- en BD y quedaba solo en la sesión, por eso los datos de distintos admins se mezclaban).
UPDATE USERS SET ORG_ID = 1 WHERE ID = 1;

-- CANCHAS (dependen de organizations)
-- ============================================
INSERT INTO COURTS (ID, ORG_ID, NAME, TYPE, PRICE_PER_HOUR, HAS_LIGHTING, COVERED, SURFACE, IMAGE_URL) VALUES (1, 1, 'Cancha Futbol 1', 'FUTBOL', 15.00, true, false, 'Sintetica', NULL);
INSERT INTO COURTS (ID, ORG_ID, NAME, TYPE, PRICE_PER_HOUR, HAS_LIGHTING, COVERED, SURFACE, IMAGE_URL) VALUES (2, 1, 'Cancha Voley 1', 'VOLEY', 10.00, false, true, 'Arena', NULL);

-- TIME SLOTS (franjas horarias por cancha)
-- ============================================
INSERT INTO TIME_SLOTS (ID, COURT_ID, SLOT_DATE, SLOT_HOUR, AVAILABLE) VALUES (1, 1, DATE '2026-07-25', TIME '09:00:00', true);
INSERT INTO TIME_SLOTS (ID, COURT_ID, SLOT_DATE, SLOT_HOUR, AVAILABLE) VALUES (2, 1, DATE '2026-07-25', TIME '10:00:00', true);

-- RESERVAS (dependen de users, organizations, courts)
-- ============================================
INSERT INTO RESERVATIONS (ID, USER_ID, ORG_ID, COURT_ID, RESERVATION_DATE, RESERVATION_HOUR, DURATION, PLAYER_COUNT, TOTAL_PRICE, STATUS, CONFIRMED, CONTACT_NAME, CONTACT_PHONE) VALUES (1, 2, 1, 1, DATE '2026-07-25', TIME '09:00:00', 2, 10, 30.00, 'UPCOMING', true, 'Carlos Mendoza', '+593991234567');

-- IMPORTANTE: como los IDs de arriba se insertaron a mano, hay que avisarle
-- a Postgres cual es el siguiente valor libre de cada secuencia de identidad.
-- Sin esto, el primer registro/reserva real chocaria con estos IDs quemados
-- (exactamente el error "duplicate key value violates unique constraint").
-- ============================================
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('organizations', 'id'), (SELECT MAX(id) FROM organizations));
SELECT setval(pg_get_serial_sequence('courts', 'id'), (SELECT MAX(id) FROM courts));
SELECT setval(pg_get_serial_sequence('time_slots', 'id'), (SELECT MAX(id) FROM time_slots));
SELECT setval(pg_get_serial_sequence('reservations', 'id'), (SELECT MAX(id) FROM reservations));
