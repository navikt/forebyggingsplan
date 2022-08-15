package exceptions

sealed class IkkeFunnetException(id: String, type: String): Exception("$type med id $id ikke funnet")

class AktivitetIkkeFunnetException(aktivitetsmalId: String): IkkeFunnetException(id = aktivitetsmalId, type = "Aktivitet")