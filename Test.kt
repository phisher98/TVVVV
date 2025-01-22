import java.net.URLDecoder
import java.util.Base64
import java.util.zip.Inflater
import java.util.regex.Pattern

object ColorFormatter {
    const val HEADER = "\u001B[95m"
    const val OKBLUE = "\u001B[94m"
    const val OKCYAN = "\u001B[96m"
    const val OKGREEN = "\u001B[92m"
    const val WARNING = "\u001B[93m"
    const val FAIL = "\u001B[91m"
    const val ENDC = "\u001B[0m"
    const val BOLD = "\u001B[1m"
    const val UNDERLINE = "\u001B[4m"
}

fun main() {
    val baseUrl = "https://vidstreamnew.xyz/v/EDMfWZnXmaYU/"
    val defaultDomain = "https://vidstreamnew.xyz/"
    val requestHeaders = mapOf(
        "Referer" to defaultDomain,
        "User-Agent" to "Mozilla/5.0 (Linux; Android 11; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
    )

    println("\n${ColorFormatter.OKCYAN}TARGET: $defaultDomain${ColorFormatter.ENDC}")

    val initialPageContent = khttp.get(baseUrl, headers = requestHeaders).text

    val encryptedDataPattern = "const\\s+Encrypted\\s*=\\s*'(.*?)'"
    val encryptedDataMatch = Regex(encryptedDataPattern).find(initialPageContent)
    val encryptedData = encryptedDataMatch?.groupValues?.get(1) ?: ""

    if (encryptedData.isEmpty()) {
        println("No encrypted data found.")
        return
    }

    val decodedBytes = Base64.getDecoder().decode(encryptedData)
    val decodedCharacters = decodedBytes.map { byte ->
        val binaryRepresentation = byte.toUByte().toString(2).padStart(8, '0')
        val reversedBinary = binaryRepresentation.reversed()
        reversedBinary.toInt(2).toByte()
    }

    val byteArray = ByteArray(decodedCharacters.size) { decodedCharacters[it] }
    val decompressedData = try {
        Inflater().run {
            setInput(byteArray)
            val output = ByteArray(1024)
            val decompressedSize = inflate(output)
            output.copyOf(decompressedSize).toString(Charsets.UTF_8)
        }
    } catch (e: Exception) {
        println("An error occurred during decompression: ${e.message}")
        ""
    }

    if (decompressedData.isEmpty()) return

    val specialToAlphabetMap = mapOf(
        '!' to 'a', '@' to 'b', '#' to 'c', '$' to 'd', '%' to 'e',
        '^' to 'f', '&' to 'g', '*' to 'h', '(' to 'i', ')' to 'j'
    )

    val processedData = decompressedData.map { char ->
        specialToAlphabetMap[char] ?: char
    }.joinToString("")

    val decodedBase64Data = Base64.getDecoder().decode(processedData)
    val decodedUrl = URLDecoder.decode(String(decodedBase64Data, Charsets.UTF_8), "UTF-8")

    val videoUrlPattern = """file:\s*"([^"]+)""""
    val videoUrlMatch = Regex(videoUrlPattern).find(decodedUrl)
    val videoUrl = videoUrlMatch?.groupValues?.get(1) ?: ""

    if (videoUrl.isEmpty()) {
        println("No video URL found.")
        return
    }

    println("######################")
    println("######################")
    println("Captured URL: ${ColorFormatter.OKGREEN}$videoUrl${ColorFormatter.ENDC}")
    println("######################")
    println("######################")
    println("${ColorFormatter.WARNING}### Please use the header \"Referer: $defaultDomain\" or the CDN host to access the URL, along with a User-Agent.${ColorFormatter.ENDC}")
}
