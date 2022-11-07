package domene

import api.dto.ArbeidsgiverRepresentantDTO

class ArbeidsgiverRepresentant(val fnr: String, val virksomhet: Virksomhet){
    fun tilDto() = ArbeidsgiverRepresentantDTO(
        fnr = fnr,
        orgnr = virksomhet.orgnr
    )
}
