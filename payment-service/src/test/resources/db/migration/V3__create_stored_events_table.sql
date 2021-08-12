create table stored_events
(
    id      bigint primary key auto_increment not null,
    payload varchar(1000) not null,
    type    varchar(100)  not null
)