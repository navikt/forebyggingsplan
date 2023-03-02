package api

import Miljø
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlient
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlientConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.ProxyConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceCode
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceEdition
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.Subject
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.TokenXToken


fun hentVirksomheterSomBrukerRepresenterer(token: String, subject: String): List<AltinnReportee> {
    return AltinnrettigheterProxyKlient(
        AltinnrettigheterProxyKlientConfig(
            ProxyConfig(
                consumerId = "Forebyggingsplan",
                url = Miljø.altinnRettigheterProxyUrl
            )
        )
    ).hentOrganisasjoner(
        selvbetjeningToken = TokenXToken(value = token),
        subject = Subject(subject),
        filtrerPåAktiveOrganisasjoner = true
    )
}

fun hentVirksomheterSomBrukerHarRiktigEnkelRettighetI(token: String, subject: String): List<AltinnReportee> {
    return AltinnrettigheterProxyKlient(
        AltinnrettigheterProxyKlientConfig(
            ProxyConfig(
                consumerId = "Forebyggingsplan",
                url = Miljø.altinnRettigheterProxyUrl
            )
        )
    ).hentOrganisasjoner(
        selvbetjeningToken = TokenXToken(value = token),
        subject = Subject(subject),
        serviceCode = ServiceCode(Miljø.altinnRettighetServiceCode), // Enkelrettighet i Altinn
        serviceEdition = ServiceEdition(Miljø.altinnRettighetServiceEdition),
        filtrerPåAktiveOrganisasjoner = true
    )
}
