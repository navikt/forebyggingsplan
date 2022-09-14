create table valgtaktivitet
(
    id                  serial primary key,
    aktivitetsmal_id    varchar(40),
    virksomhetsnummer   varchar(20),
    fødselsnummer       varchar(11),
    fullfoert           boolean   default false,
    fullfoert_tidspunkt timestamp default null
);
