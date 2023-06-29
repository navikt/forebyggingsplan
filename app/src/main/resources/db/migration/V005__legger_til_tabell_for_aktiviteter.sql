create table "aktiviteter" (
    "hashet_fodselsnummer" bytea not null,
    "orgnr" varchar(9) not null,
    "aktivitetsid" varchar(45) not null,
    "aktivitetsversjon" varchar(45) not null,
    "fullfort" boolean default false not null,
    "fullforingstidspunkt" timestamp default null,
    primary key ("hashet_fodselsnummer", "orgnr", "aktivitetsid")
);