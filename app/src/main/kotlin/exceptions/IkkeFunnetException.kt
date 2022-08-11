package exceptions

sealed class IkkeFunnetException(id: String, type: String): Exception("$type med id $id ikke funnet")

class AktivitetIkkeFunnetException(aktivitetsId: String): IkkeFunnetException(id = aktivitetsId, type = "Aktivitet")