alter table valgtaktivitet
ADD column aktivitetsversjon varchar(40) not null default 'v0',
DROP column fødselsnummer;
