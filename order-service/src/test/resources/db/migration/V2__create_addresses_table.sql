create table addresses
(
    id       bigint primary key auto_increment not null,
    street   varchar(300) not null,
    city     varchar(300) not null,
    house    varchar(300) not null,
    flat     int null,
    floor    int null,
    entrance int null
)