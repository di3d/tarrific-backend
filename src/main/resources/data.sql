-- =======================================
-- COUNTRIES
-- =======================================
INSERT INTO country (name, iso_code, region)
VALUES
    ('Singapore', 'SGP', 'Asia'),
    ('China', 'CHN', 'Asia'),
    ('Japan', 'JPN', 'Asia'),
    ('United States', 'USA', 'North America'),
    ('Germany', 'DEU', 'Europe');

-- =======================================
-- ELECTRONICS HS CODES
-- =======================================
INSERT INTO hs_code (hs_code, description, chapter, heading, subheading)
VALUES
    ('847130', 'Portable automatic data processing machines, laptops and tablets', '84', '71', '30'),
    ('847150', 'Processing units other than those of subheading 8471.30', '84', '71', '50'),
    ('851712', 'Telephones for cellular networks or other wireless networks (smartphones)', '85', '17', '12'),
    ('852580', 'Digital cameras and video camera recorders', '85', '25', '80'),
    ('854231', 'Electronic integrated circuits – processors and controllers', '85', '42', '31'),
    ('854232', 'Electronic integrated circuits – memories', '85', '42', '32'),
    ('854239', 'Electronic integrated circuits – other', '85', '42', '39'),
    ('850440', 'Static converters (power supply units, adapters, chargers)', '85', '04', '40'),
    ('850760', 'Lithium-ion accumulators', '85', '07', '60');

-- =======================================
-- TARIFFS (base rates and validity)
-- =======================================
INSERT INTO tariff (hs_code, base_rate, rate_type, effective_date, expiry_date)
VALUES
    ('847130', 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('847150', 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('851712', 5.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('852580', 2.5, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('854231', 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('854232', 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('854239', 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('850440', 3.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    ('850760', 2.0, 'ad-valorem', '2025-01-01', '2030-12-31');

-- =======================================
-- TRADE AGREEMENTS
-- =======================================
INSERT INTO trade_agreement (name, description, effective_date, expiry_date)
VALUES
    ('ASEAN Free Trade Area (AFTA)', 'Regional tariff reduction among ASEAN nations', '2025-01-01', '2030-12-31'),
    ('US-Singapore FTA', 'Bilateral free trade agreement between USA and Singapore', '2025-01-01', '2030-12-31'),
    ('EU-Japan EPA', 'Economic Partnership Agreement between the EU and Japan', '2025-01-01', '2030-12-31');

-- =======================================
-- PREFERENTIAL TARIFFS
-- =======================================
-- Example: ASEAN trade within Asia – lower tariffs
INSERT INTO preferential_tariff (tariff_id, agreement_id, preferential_rate, rate_type, effective_date, expiry_date)
VALUES
    (1, 1, 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    (2, 1, 0.0, 'ad-valorem', '2025-01-01', '2030-12-31'),
    (3, 2, 2.5, 'ad-valorem', '2025-01-01', '2030-12-31'),
    (4, 3, 1.0, 'ad-valorem', '2025-01-01', '2030-12-31');
