create table stored_events
(
    id      bigint primary key auto_increment not null,
    payload text         not null,
    type    varchar(100) not null
)