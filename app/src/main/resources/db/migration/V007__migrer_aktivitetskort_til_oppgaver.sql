INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('9D9E8ACC-CB8E-4763-8B04-B345B9F3DE68'),
             ('B7881929-4104-44F7-B409-2FB0AB3F16BF'),
             ('81282F61-8A8B-4027-9AA6-3E04A5D30B4B'),
             ('57F04A11-2D32-4F6F-90DE-A0043D64F242')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = '1ef7291f-b498-4fc9-98b5-63464432fab4'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;

INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('D7067365-8971-4D08-A6DE-915AE1CDCE5E'),
             ('43384261-71DF-4AC5-ACAE-C9FC062A337A')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = '3a0f3c60-ac1e-4d4e-9693-e850b5bef984'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;

INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('A6907370-1C82-47DD-B726-63E750E28E78')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = '61170ac8-0a8c-424f-bfc3-e05140d0171a'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;

INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('1649577A-6BFB-49A7-80F1-392EFC9F02E9'),
             ('98504561-25F3-46D5-9F92-927ECC1C8CD5')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = '67fe672b-8def-4e71-8fe8-1fd6d5226dc9'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;

INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('C283BCCC-E6E0-49B3-8169-DF1E4F6D2F36'),
             ('5BA3101B-1D6D-4EBB-BE81-21AB862C8AD5')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = 'ba2cd821-21ac-4142-97d6-1f75a8ca9764'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;

INSERT INTO aktiviteter (aktivitetsid, hashet_fodselsnummer, orgnr, sist_endret, status, aktivitetstype)
SELECT ny.aktivitetsid, hashet_fodselsnummer, orgnr, fullforingstidspunkt, 'FULLFØRT', 'OPPGAVE'
FROM (
         VALUES
             ('EFA22A63-D8E0-4A40-928D-C62933A80475'),
             ('51363EDD-5102-4473-8367-E5304935B273')
     ) AS ny (aktivitetsid),
     aktiviteter
WHERE aktiviteter.aktivitetsid = 'cc3402f3-9765-4f31-a1fc-d3b1a67f1052'
ON CONFLICT (hashet_fodselsnummer, orgnr, aktivitetsid) DO NOTHING
RETURNING *;
