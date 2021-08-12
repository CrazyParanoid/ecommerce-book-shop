create table products
(
    id           binary(16) primary key not null,
    picture_link text           not null,
    price        decimal(14, 6) not null,
    name         text           not null,
    author       text null,
    quantity     int            not null,
    created_at   timestamp      not null,
    updated_at   timestamp      not null
)