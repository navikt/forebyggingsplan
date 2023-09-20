alter table "aktiviteter"
add column "status" varchar(45);

alter table "aktiviteter"
alter column "fullfort" drop not null;

alter table "aktiviteter"
alter column "fullfort" drop default;

alter table "aktiviteter"
add column "sist_endret" timestamp;

alter table "aktiviteter"
add column "aktivitetstype" varchar(45);

alter table "aktiviteter"
drop column "aktivitetsversjon";