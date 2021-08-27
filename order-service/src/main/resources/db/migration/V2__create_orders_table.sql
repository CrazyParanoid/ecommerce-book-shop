create table orders
(
    order_id            binary(16) primary key not null,
    client_id           binary(16) not null,
    delivery_address_id binary(16) not null,
    total_price         decimal(14, 6) not null,
    status              text           not null,
    payment_id          text null,
    delivered_at        timestamp null,
    courier_id          binary(16) null,
    created_at          timestamp      not null,
    updated_at          timestamp      not null
)