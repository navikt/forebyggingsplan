Forebyggingsplan
================

Backend for forebyggingsplan

# Komme i gang

Koden ligger i `app`, mens testene ligger i `tests` for å holde testene utenfor modulen, som igjen
er for å prøve å
sørge for bedre innkapsling. Bare bruk testmappen `app/src/test` for å skrive enhetstester som ikke
lar seg teste utenfra.

Noen tester kjører med [testcontainers](https://www.testcontainers.org/). Da må du ha en container
runtime (Docker eller Colima) som kjører på din maskin.

### Colima

dersom du bruker colima, må du opprette en symlink til `docker.sock` i colima-mappen for at
testcontainers skal fungere:

```
sudo rm -rf /var/run/docker.sock && sudo ln -s /Users/$(whoami)/.colima/docker.sock /var/run/docker.sock
```

husk også å starte colima:

```
colima start
```

testene kan kjøres fra intellij, eller med gradle:

```
./gradlew test
```

---

# Rettigheter i Altinn

Vi sjekker enkelrettighet 'Forebygge fravær' med følgende konfig (i dev og prod):

- serviceCode 5934
- serviceEdition 1

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i
kanalen [#team-pia](https://nav-it.slack.com/archives/C02DL347ZT2).
