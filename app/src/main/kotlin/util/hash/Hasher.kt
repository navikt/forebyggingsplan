package util.hash

interface Hasher {
    fun hash(data: String): ByteArray
    fun hashWithSalt(data: String, salt: ByteArray): ByteArray
    fun generateRandomSalt(): ByteArray
}
