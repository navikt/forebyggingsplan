alter table "aktiviteter"
add column "status" varchar(45);

alter table "aktiviteter"
alter column "fullfort" drop not null;
alter table "aktiviteter"
alter column "fullfort" drop default;
