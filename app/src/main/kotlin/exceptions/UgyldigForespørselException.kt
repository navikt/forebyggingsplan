package exceptions


class UgyldigForespørselException(override val message: String = "Ugyldig forespørsel"): Exception(message)
