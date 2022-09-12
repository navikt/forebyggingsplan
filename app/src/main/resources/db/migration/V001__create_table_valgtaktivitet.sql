create table valgtaktivitet
(
    id                serial primary key,
    uuid              varchar(40) unique,
    virksomhetsnummer varchar(20),
    fødselsnummer     varchar(11),
    fullfoert         boolean default false
);
