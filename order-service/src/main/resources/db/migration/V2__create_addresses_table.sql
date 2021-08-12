create table addresses
(
    id       bigint primary key auto_increment not null,
    street   text not null,
    city     text not null,
    house    text not null,
    flat     int null,
    floor    int null,
    entrance int null
)