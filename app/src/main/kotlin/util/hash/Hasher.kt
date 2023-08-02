package util.hash

interface Hasher {
    fun hash(data: String): ByteArray
    fun generateRandomSalt(): ByteArray
}
