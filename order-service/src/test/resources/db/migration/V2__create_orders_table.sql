create table orders
(
    order_id            binary(16) primary key not null,
    client_id           binary(16) not null,
    delivery_address_id binary(16) not null,
    total_price         decimal(14, 6) not null,
    status              varchar(300)   not null,
    payment_id          varchar(300) null,
    delivered_at        timestamp null,
    created_at          timestamp      not null,
    updated_at          timestamp      not null,
    courier_id          binary(16) null
)