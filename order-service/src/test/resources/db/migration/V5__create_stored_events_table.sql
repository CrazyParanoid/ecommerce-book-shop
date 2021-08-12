create table stored_events
(
    id      bigint primary key auto_increment not null,
    payload varchar(2000)      not null,
    type    varchar(100)       not null
)