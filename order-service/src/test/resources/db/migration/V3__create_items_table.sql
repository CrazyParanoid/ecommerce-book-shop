create table items
(
    id       bigint primary key auto_increment not null,
    item_id  binary(16) not null,
    price    decimal(14, 6) not null,
    quantity int            not null,
    order_id binary(16) not null,
    foreign key (order_id) references orders (order_id)
)