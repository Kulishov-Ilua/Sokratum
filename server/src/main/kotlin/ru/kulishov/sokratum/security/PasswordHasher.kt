package ru.kulishov.sokratum.security

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Хэширование паролей на стандартном JDK (PBKDF2WithHmacSHA256).
 * Формат хранения: "iterations:saltBase64:hashBase64" — только JDK, без внешних зависимостей.
 */
object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 60_000
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(16).also { random.nextBytes(it) }
        val hash = pbkdf2(password, salt, ITERATIONS)
        return "$ITERATIONS:${Base64.getEncoder().encodeToString(salt)}:${Base64.getEncoder().encodeToString(hash)}"
    }

    fun verify(password: String, stored: String): Boolean = runCatching {
        val (iterations, salt, expected) = stored.split(":")
        val hash = pbkdf2(password, Base64.getDecoder().decode(salt), iterations.toInt())
        hash.contentEquals(Base64.getDecoder().decode(expected))
    }.getOrDefault(false)

    fun isHashed(stored: String): Boolean =
        stored.count { it == ':' } == 2 && stored.substringBefore(':').toIntOrNull() != null

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }
}
