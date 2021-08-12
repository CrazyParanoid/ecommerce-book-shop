create table clients
(
    client_id  binary(16) primary key not null,
    address_id bigint not null,
    foreign key (address_id) references addresses (id)
)