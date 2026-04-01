CREATE DATABASE IF NOT EXISTS shopping_cart_localization
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE LANGUAGE
(
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_records
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT    NOT NULL,
    total_cost  DOUBLE NOT NULL,
    language    VARCHAR(10),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (language)
        REFERENCES LANGUAGE(code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS cart_items
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number    INT    NOT NULL,
    item_name      VARCHAR(50),
    price          DOUBLE NOT NULL,
    quantity       INT    NOT NULL,
    subtotal       DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    `key`    VARCHAR(100) NOT NULL,
    value    VARCHAR(255) NOT NULL,
    language VARCHAR(10)  NOT NULL,
    FOREIGN KEY (language)
        REFERENCES LANGUAGE(code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

# INSERT

INSERT INTO LANGUAGE (code, name)
VALUES ('en', 'English'),
       ('fi', 'Suomi'),
       ('ja', '日本語'),
       ('lo', 'ລາວ'),
       ('ar', 'العربية'),
       ('se', 'Svenska');


