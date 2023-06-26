package util.hash

interface Hasher {
    fun hash(data: String, salt: ByteArray): ByteArray
    fun generateRandomSalt(): ByteArray
}
