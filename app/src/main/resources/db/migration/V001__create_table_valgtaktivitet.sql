create table valgtaktivitet
(
    id                  serial primary key,
    aktivitetsmal_id    varchar(40),
    virksomhetsnummer   varchar(20),
    fødselsnummer       varchar(11),
    fullfort           boolean   default false,
    fullfort_tidspunkt timestamp default null
);
