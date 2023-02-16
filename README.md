Forebyggingsplan
================

Backend for forebyggingsplan 

# Komme i gang

Koden ligger i `app`, mens testene ligger i `tests` for å holde testene utenfor modulen, som igjen er for å prøve å
sørge for bedre innkapsling. Bare bruk testmappen `app/src/test` for å skrive enhetstester som ikke lar seg teste utenfra.

Noen tester kjører med [testcontainers](https://www.testcontainers.org/). Da må du ha en container runtime (Docker eller Colima) som kjører på din maskin.

---
# Rettigheter i Altinn
Vi sjekker enkelrettighet 'sykefraværsstatistikk' med følgende konfig: 
 - prod: serviceCode 3403 og serviceEdition 2
 - dev: serviceCode 3403 og serviceEdition 1

Senere skal vi bruke ny enkelrettighet 'forebygge fravær' med konfig: 
 - prod: __ikke klar enda__
 - dev: serviceCode 5934 og serviceEdition 1


# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#team-pia-utvikling](https://nav-it.slack.com/archives/C02T6RG9AE4).
