-- DE-Store Database Initialization

CREATE SCHEMA IF NOT EXISTS price_service;
CREATE SCHEMA IF NOT EXISTS inventory_service;
CREATE SCHEMA IF NOT EXISTS loyalty_service;
CREATE SCHEMA IF NOT EXISTS finance_service;
CREATE SCHEMA IF NOT EXISTS notification_service;
CREATE SCHEMA IF NOT EXISTS reporting_service;

GRANT ALL ON SCHEMA price_service TO destore;
GRANT ALL ON SCHEMA inventory_service TO destore;
GRANT ALL ON SCHEMA loyalty_service TO destore;
GRANT ALL ON SCHEMA finance_service TO destore;
GRANT ALL ON SCHEMA notification_service TO destore;
GRANT ALL ON SCHEMA reporting_service TO destore;

CREATE TABLE IF NOT EXISTS public.stores (
    id BIGSERIAL PRIMARY KEY,
    store_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    postcode VARCHAR(20),
    region VARCHAR(50),
    manager_name VARCHAR(100),
    manager_email VARCHAR(100),
    manager_phone VARCHAR(20),
    warehouse_id BIGINT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO public.stores (store_code, name, address, city, postcode, region, manager_name, manager_email)
VALUES
    ('STORE001', 'Edinburgh Central', '100 Princes Street', 'Edinburgh', 'EH2 3AA', 'Scotland', 'John Smith', 'john.smith@destore.com'),
    ('STORE002', 'Glasgow West', '50 Argyle Street', 'Glasgow', 'G2 8AH', 'Scotland', 'Jane Doe', 'jane.doe@destore.com'),
    ('STORE003', 'London Southbank', '200 Waterloo Road', 'London', 'SE1 8XJ', 'England', 'Bob Wilson', 'bob.wilson@destore.com')
ON CONFLICT (store_code) DO NOTHING;
