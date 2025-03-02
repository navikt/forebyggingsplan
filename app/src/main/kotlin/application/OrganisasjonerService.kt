package application

import Systemmiljø
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlient
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlientConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.ProxyConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceCode
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceEdition
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.Subject
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.TokenXToken

fun hentVirksomheterSomBrukerRepresenterer(
    token: String,
    subject: String,
): List<AltinnReportee> =
    AltinnrettigheterProxyKlient(
        AltinnrettigheterProxyKlientConfig(
            ProxyConfig(
                consumerId = "Forebyggingsplan",
                url = Systemmiljø.altinnRettigheterProxyUrl,
            ),
        ),
    ).hentOrganisasjoner(
        selvbetjeningToken = TokenXToken(value = token),
        subject = Subject(subject),
        filtrerPåAktiveOrganisasjoner = true,
    )

fun hentVirksomheterSomBrukerHarRiktigEnkelRettighetI(
    token: String,
    subject: String,
): List<AltinnReportee> =
    AltinnrettigheterProxyKlient(
        AltinnrettigheterProxyKlientConfig(
            ProxyConfig(
                consumerId = "Forebyggingsplan",
                url = Systemmiljø.altinnRettigheterProxyUrl,
            ),
        ),
    ).hentOrganisasjoner(
        selvbetjeningToken = TokenXToken(value = token),
        subject = Subject(subject),
        serviceCode = ServiceCode("5934"), // Enkelrettighet i Altinn
        serviceEdition = ServiceEdition("1"),
        filtrerPåAktiveOrganisasjoner = true,
    )
