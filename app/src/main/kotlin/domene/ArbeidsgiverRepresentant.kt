package domene

import api.dto.ArbeidsgiverRepresentantDTO

class ArbeidsgiverRepresentant(val virksomhet: Virksomhet){
    fun tilDto() = ArbeidsgiverRepresentantDTO(
        orgnr = virksomhet.orgnr
    )
}
