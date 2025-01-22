import java.util.Base64
import java.util.zip.Inflater

fun decodeEncryptedData(encryptedString: String): String? {
    // Step 1: Decode Base64 string
    val decodedBytes = try {
        Base64.getDecoder().decode(encryptedString)
    } catch (e: IllegalArgumentException) {
        println("Invalid Base64 input: ${e.message}")
        return null
    }

    // Step 2: Reverse binary representation of each byte
    val decodedCharacters = decodedBytes.map { byte ->
        val binaryRepresentation = byte.toUByte().toString(2).padStart(8, '0')
        val reversedBinary = binaryRepresentation.reversed()
        reversedBinary.toInt(2).toByte()
    }

    // Step 3: Decompress the byte array
    val byteArray = ByteArray(decodedCharacters.size) { decodedCharacters[it] }
    val decompressedData = try {
        Inflater().run {
            setInput(byteArray)
            val output = ByteArray(1024)
            val decompressedSize = inflate(output)
            output.copyOf(decompressedSize).toString(Charsets.UTF_8)
        }
    } catch (e: Exception) {
        println("Error during decompression: ${e.message}")
        return null
    }

    // Step 4: Replace special characters with alphabets
    val specialToAlphabetMap = mapOf(
        '!' to 'a', '@' to 'b', '#' to 'c', '$' to 'd', '%' to 'e',
        '^' to 'f', '&' to 'g', '*' to 'h', '(' to 'i', ')' to 'j'
    )
    val processedData = decompressedData.map { char ->
        specialToAlphabetMap[char] ?: char
    }.joinToString("")

    return processedData
}
