import java.util.Base64
import java.util.zip.Inflater

fun decodeEncryptedString(encryptedString: String): String? {
    try {
        // Base64 decode
        val decodedBytes = Base64.getDecoder().decode(encryptedString)

        // Reverse binary and decode characters
        val decodedCharacters = decodedBytes.map { byte ->
            val binaryRepresentation = byte.toUByte().toString(2).padStart(8, '0')
            val reversedBinary = binaryRepresentation.reversed()
            reversedBinary.toInt(2).toByte()
        }

        // Convert to byte array
        val byteArray = ByteArray(decodedCharacters.size) { decodedCharacters[it] }

        // Decompress using ZLIB
        val decompressedData = Inflater().run {
            setInput(byteArray)
            val output = ByteArray(1024 * 4) // Increase buffer size to handle larger outputs
            val decompressedSize = inflate(output)
            output.copyOf(decompressedSize).toString(Charsets.UTF_8)
        }

        // Map special characters to alphabets
        val specialToAlphabetMap = mapOf(
            '!' to 'a', '@' to 'b', '#' to 'c', '$' to 'd', '%' to 'e',
            '^' to 'f', '&' to 'g', '*' to 'h', '(' to 'i', ')' to 'j'
        )
        val processedData = decompressedData.map { char ->
            specialToAlphabetMap[char] ?: char
        }.joinToString("")

        // Base64 decode the final processed data
        val finalDecodedData = Base64.getDecoder().decode(processedData).toString(Charsets.UTF_8)

        return finalDecodedData
    } catch (e: Exception) {
        println("Error decoding string: ${e.message}")
        return null
    }
}
