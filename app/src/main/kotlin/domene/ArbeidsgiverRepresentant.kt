package domene

import api.dto.ArbeidsgiverRepresentantDTO

class ArbeidsgiverRepresentant(val fnr: String, val virksomhet: Virksomhet){
    fun tilDto() = ArbeidsgiverRepresentantDTO(
        fnr = fnr,
        orgnr = virksomhet.orgnr
    )
}


val enVirksomhet = Virksomhet("811076732")
val enArbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678912", virksomhet = enVirksomhet)
