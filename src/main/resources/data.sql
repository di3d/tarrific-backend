-- Countries
INSERT INTO country (name, tariff_rate) VALUES ('Singapore', 5.00);
INSERT INTO country (name, tariff_rate) VALUES ('Malaysia', 7.00);

-- HS Codes
INSERT INTO hscode (code, description) VALUES ('8471.30', 'Portable automatic data processing machines');
INSERT INTO hscode (code, description) VALUES ('8517.12', 'Telephones for cellular networks');
INSERT INTO hscode (code, description) VALUES ('8528.72', 'Monitors and projectors');
INSERT INTO hscode (code, description) VALUES ('8542.31', 'Electronic integrated circuits');
INSERT INTO hscode (code, description) VALUES ('9504.50', 'Video game consoles and machines');

-- Example Tariff (Singapore → Malaysia, HSCode 8542.31)
INSERT INTO tariff (country_a_id, country_b_id, hscode_id, rate, tariff_type, start_date, end_date)
VALUES (1, 2, 4, 5.00, 'MFN/AHS/BND', '2025-01-01', '2026-01-01');
