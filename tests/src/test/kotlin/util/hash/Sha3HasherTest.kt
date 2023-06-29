package util.hash

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class Sha3HasherTest {
    private val hasher = Sha3Hasher()

    @Test
    fun `hasher should always hash to the same value given the same salt`() {
        val data = "Data to be hashed"
        val salt = "Salt".toByteArray()
        val result = hasher.hash(data)

        result shouldBe "4ce65935eb85af6941f49702d764b516ce12284b113f48871cfb76b763e1634a".hexStringToByteArray()
    }

    @Test
    fun `hasher should always hash to a different value given different salts`() {
        val data = "Data to be hashed"
        val firstSalt = hasher.generateRandomSalt()
        val secondSalt = hasher.generateRandomSalt()
        val firstResult = hasher.hash(data)
        val secondResult = hasher.hash(data)

        firstResult shouldNotBe secondResult
    }

    private fun String.hexStringToByteArray(): ByteArray {
        return chunked(2)
            .map { it.toIntOrNull(16) ?: 0 }
            .map { it.toByte() }
            .toByteArray()
    }
}