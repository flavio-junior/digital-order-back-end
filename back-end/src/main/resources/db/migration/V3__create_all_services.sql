CREATE TABLE IF NOT EXISTS tb_company(
    id SERIAL PRIMARY KEY,
    identifier BIGINT NOT NULL,
    date DATE NOT NULL,
    hour TIME NOT NULL,
    name VARCHAR(60) NOT NULL,
    main_image VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS tb_user_company (
    fk_user INT REFERENCES tb_user(id),
    fk_company INT REFERENCES tb_company(id),
    PRIMARY KEY (fk_user, fk_company)
);

CREATE TABLE IF NOT EXISTS tb_category(
    id SERIAL PRIMARY KEY,
    name VARCHAR(60)
);

CREATE TABLE IF NOT EXISTS tb_company_category (
    fk_company INT REFERENCES tb_company(id),
    fk_category INT REFERENCES tb_category(id),
    PRIMARY KEY (fk_company, fk_category)
);

CREATE TABLE IF NOT EXISTS tb_reservation(
    id SERIAL PRIMARY KEY,
    name VARCHAR(60),
    status varchar(30) check (status in ('AVAILABLE', 'RESERVED'))
);

CREATE TABLE IF NOT EXISTS tb_company_reservation (
    fk_company INT REFERENCES tb_company(id),
    fk_reservation INT REFERENCES tb_reservation(id),
    PRIMARY KEY (fk_company, fk_reservation)
);

CREATE TABLE IF NOT EXISTS tb_item(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    price NUMERIC(10, 2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS tb_company_item (
    fk_company INT REFERENCES tb_company(id),
    fk_item INT REFERENCES tb_item(id),
    PRIMARY KEY (fk_company, fk_item)
);

CREATE TABLE IF NOT EXISTS tb_food (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS tb_company_food (
    fk_company INT REFERENCES tb_company(id),
    fk_food INT REFERENCES tb_food(id),
    PRIMARY KEY (fk_company, fk_food)
);

CREATE TABLE IF NOT EXISTS tb_product (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) DEFAULT 0.0,
    stock_quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_company_product (
    fk_company INT REFERENCES tb_company(id),
    fk_product INT REFERENCES tb_product(id),
    PRIMARY KEY (fk_company, fk_product)
);

CREATE TABLE IF NOT EXISTS tb_food_category (
    fk_food INT REFERENCES tb_food(id),
    fk_category INT REFERENCES tb_category(id),
    PRIMARY KEY (fk_food, fk_category)
);

CREATE TABLE IF NOT EXISTS tb_product_category (
    fk_product INT REFERENCES tb_product(id),
    fk_category INT REFERENCES tb_category(id),
    PRIMARY KEY (fk_product, fk_category)
);

CREATE TABLE IF NOT EXISTS tb_order (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    type varchar(30) check (type in ('DELIVERY', 'ORDER', 'RESERVATION', 'SHOPPING_CART')),
    status varchar(30) check (status in ('OPEN', 'CLOSED')),
    quantity INT NOT NULL,
    observation VARCHAR(300) NULL,
    total NUMERIC(10, 2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS tb_company_order (
    fk_order INT REFERENCES tb_order(id),
    fk_company INT REFERENCES tb_company(id),
    PRIMARY KEY (fk_order, fk_company)
);

CREATE TABLE IF NOT EXISTS tb_order_reservation (
    fk_order INT REFERENCES tb_order(id),
    fk_reservation INT REFERENCES tb_reservation(id),
    PRIMARY KEY (fk_order, fk_reservation)
);

CREATE TABLE IF NOT EXISTS tb_address (
    id SERIAL PRIMARY KEY,
    status varchar(30) check (status in ('PENDING_DELIVERY', 'SENDING', 'DELIVERED')),
    street VARCHAR(40) NOT NULL,
    number INT NULL,
    district VARCHAR(30) NOT NULL,
    complement VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_order_address (
    fk_order INT REFERENCES tb_order(id),
    fk_address INT REFERENCES tb_address(id),
    PRIMARY KEY (fk_order, fk_address)
);

CREATE TABLE IF NOT EXISTS tb_object (
    id SERIAL PRIMARY KEY,
    identifier INT NULL,
    type varchar(30) check (type in ('FOOD', 'PRODUCT', 'ITEM')),
    name VARCHAR(60) NOT NULL,
    price NUMERIC(10, 2) DEFAULT 0.0,
    quantity INT NOT NULL,
    total NUMERIC(10, 2) DEFAULT 0.0,
    status varchar(30) check (status in ('PENDING', 'DELIVERED'))
);

CREATE TABLE IF NOT EXISTS tb_order_object (
    fk_order INT REFERENCES tb_order(id),
    fk_object INT REFERENCES tb_object(id),
    PRIMARY KEY (fk_order, fk_object)
);

CREATE TABLE IF NOT EXISTS tb_payment (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    hour TIME NOT NULL,
    code BIGINT NOT NULL,
    author VARCHAR(30) NOT NULL,
    assigned VARCHAR(30) NOT NULL,
    type_order varchar(30) check (type_order in ('DELIVERY', 'ORDER', 'RESERVATION', 'SHOPPING_CART')),
    type_payment varchar(30) check (type_payment in ('CREDIT_CAD', 'DEBIT_CAD', 'MONEY', 'PIX')),
    discount BOOLEAN DEFAULT FALSE,
    value_discount NUMERIC(10, 2) DEFAULT 0.0,
    fee BOOLEAN DEFAULT FALSE,
    value_fee NUMERIC(10, 2) DEFAULT 0.0,
    total NUMERIC(10, 2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS tb_order_payment (
    fk_order INT REFERENCES tb_order(id),
    fk_payment INT REFERENCES tb_payment(id),
    PRIMARY KEY (fk_order, fk_payment)
);

CREATE TABLE IF NOT EXISTS tb_company_payment (
    fk_company INT REFERENCES tb_company(id),
    fk_payment INT REFERENCES tb_payment(id),
    PRIMARY KEY (fk_company, fk_payment)
);

CREATE TABLE IF NOT EXISTS tb_report (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    hour TIME NOT NULL,
    resume VARCHAR(1000) NOT NULL,
    author VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_company_report (
    fk_company INT REFERENCES tb_company(id),
    fk_report INT REFERENCES tb_report(id),
    PRIMARY KEY (fk_company, fk_report)
);

CREATE TABLE IF NOT EXISTS tb_overview (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    status varchar(30) check (status in ('PENDING', 'DELIVERED')),
    quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_object_overview (
    fk_overview INT REFERENCES tb_overview(id),
    fk_object INT REFERENCES tb_object(id),
    PRIMARY KEY (fk_overview, fk_object)
);

CREATE TABLE IF NOT EXISTS tb_employee (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    name VARCHAR(30) NOT NULL,
    function varchar(30) check (function in ('ATTENDANT', 'BOX', 'WAITER')),
    status varchar(30) check (status in ('ENABLED', 'DISABLED'))
);

CREATE TABLE IF NOT EXISTS tb_company_employee (
    fk_employee INT REFERENCES tb_employee(id),
    fk_company INT REFERENCES tb_company(id),
    PRIMARY KEY (fk_employee, fk_company)
);

CREATE TABLE IF NOT EXISTS tb_fee (
    id SERIAL PRIMARY KEY,
    percentage INT NOT NULL,
    assigned varchar(30) check (assigned in ('WAITER'))
);

CREATE TABLE IF NOT EXISTS tb_company_fee (
    fk_fee INT REFERENCES tb_fee(id),
    fk_company INT REFERENCES tb_company(id),
    PRIMARY KEY (fk_fee, fk_company)
);

CREATE TABLE IF NOT EXISTS tb_order_fee (
    fk_fee INT REFERENCES tb_fee(id),
    fk_order INT REFERENCES tb_order(id),
    PRIMARY KEY (fk_fee, fk_order)
);

CREATE TABLE IF NOT EXISTS tb_author (
    id SERIAL PRIMARY KEY,
    author VARCHAR(30) NOT NULL,
    assigned VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_fee_author (
    fk_fee INT REFERENCES tb_fee(id),
    fk_author INT REFERENCES tb_author(id),
    PRIMARY KEY (fk_fee, fk_author)
);

CREATE TABLE IF NOT EXISTS tb_day (
    id SERIAL PRIMARY KEY,
    day varchar(30) check (day in ('ALL', 'SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'))
);

CREATE TABLE IF NOT EXISTS tb_fee_day (
    fk_fee INT REFERENCES tb_fee(id),
    fk_day INT REFERENCES tb_day(id),
    PRIMARY KEY (fk_fee, fk_day)
);
