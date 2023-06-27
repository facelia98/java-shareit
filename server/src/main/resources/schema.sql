drop table if exists users, requests, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY not null,
    email varchar(320),
    name  varchar(100),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY not null,
    requestor_id BIGINT,
    description  VARCHAR(1000),
    created timestamp,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY not null,
    owner_id     bigint,
    request_id   bigint,
    name         varchar(1000),
    description  varchar(1000),
    is_available boolean,
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY not null,
    start_date timestamp,
    end_date   timestamp,
    item_id    bigint,
    booker_id  bigint,
    status     varchar(10),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY not null,
    author_id BIGINT not null,
    item_id   BIGINT not null,
    text      VARCHAR(1000) not null,
    created   timestamp not null,
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);

