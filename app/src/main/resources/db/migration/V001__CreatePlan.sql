create table plan
(
    id                serial primary key,
    uuid              varchar(40) unique,
    virksomhetsnummer varchar(20)
);
