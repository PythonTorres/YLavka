CREATE TABLE IF NOT EXISTS couriers(
                                       courier_id BIGSERIAL NOT NULL PRIMARY KEY,
                                       courier_type VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS regions(
    region_id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS couriers_regions(
                                               courier_id BIGINT,
                                               region_id INTEGER,
                                               CONSTRAINT fk_courier
                                               FOREIGN KEY(courier_id)
    REFERENCES couriers(courier_id),
    CONSTRAINT fk_region
    FOREIGN KEY(region_id)
    REFERENCES regions(region_id)
    );

CREATE TABLE IF NOT EXISTS working_hours(
                                            working_hours_id SERIAL NOT NULL PRIMARY KEY,
                                            start_date TIME,
                                            end_date TIME
);

CREATE TABLE IF NOT EXISTS couriers_working_hours(
                                                     courier_id BIGINT,
                                                     working_hours_id INTEGER,
                                                     CONSTRAINT fk_courier
                                                     FOREIGN KEY(courier_id)
    REFERENCES couriers(courier_id),
    CONSTRAINT fk_working_hours
    FOREIGN KEY(working_hours_id)
    REFERENCES working_hours(working_hours_id)
    );

CREATE TABLE IF NOT EXISTS orders(
                                     order_id BIGSERIAL NOT NULL PRIMARY KEY,
                                     weight REAL,
                                     regions INTEGER,
                                     cost INTEGER,
                                     completed_time TIMESTAMP,
                                     courier_id BIGINT
);

CREATE TABLE IF NOT EXISTS delivery_hours(
                                             delivery_hours_id SERIAL NOT NULL PRIMARY KEY,
                                             start_date TIME,
                                             end_date TIME
);

CREATE TABLE IF NOT EXISTS orders_delivery_hours(
                                                    order_id BIGINT,
                                                    delivery_hours_id INTEGER,
                                                    CONSTRAINT fk_order
                                                    FOREIGN KEY(order_id)
    REFERENCES orders(order_id),
    CONSTRAINT fk_delivery_hours
    FOREIGN KEY(delivery_hours_id)
    REFERENCES delivery_hours(delivery_hours_id)
    );