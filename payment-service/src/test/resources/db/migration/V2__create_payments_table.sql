create table payments
(
    id         varchar(50) primary key not null,
    order_id   binary(16) not null,
    client_id  binary(16) not null,
    amount     bigint                  not null,
    created_at timestamp               not null,
    updated_at timestamp               not null
)