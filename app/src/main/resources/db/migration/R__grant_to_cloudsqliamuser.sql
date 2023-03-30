-- Script som nullstiller rettigheter til cloudsqliamuser til read-only
-- Vi gjør dette fordi det er mulig å tilegne delete/write/update rettigheter med 'nais-cli', men det er ikke mulig å fjerne disse igjen
DO
$do$
    DECLARE count int;
    BEGIN
        SELECT count(*) INTO count FROM pg_roles WHERE rolname = 'cloudsqliamuser';
        IF count > 0 THEN
            REVOKE ALL ON ALL TABLES IN SCHEMA public FROM cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
        END IF;
    END
$do$
