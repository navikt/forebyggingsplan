package util.hash

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Sha3HasherTest {
    private val hasher = Sha3Hasher()

    @Test
    fun `hasher should always hash to the same value`() {
        val data = "Data to be hashed"
        val result = hasher.hash(data)

        result.toHex() shouldBe "9424158c8272ad6cacd7391d27b05cdc7b09cd51ae8faacda7e11833d4553dec"
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}
