package com.avivba

import android.util.Log
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper

// The unique tag we will use to find our messages in the logs
private const val DEBUG_TAG = "LayarKacaDebug"

abstract class PackerExtractor : ExtractorApi() {
    override val requiresReferer = true

    private fun unpack(packedJs: String): String? {
        // This function is the same as before
        try {
            val data = Regex("""}\('(.*)',(\d+),(\d+),'([^']*)'\.split\('\|'\)""").find(packedJs)?.groupValues ?: return null
            var payload = data[1]
            val radix = data[2].toIntOrNull() ?: return null
            var count = data[3].toIntOrNull() ?: return null
            val keys = data[4].split("|")
            while (count-- > 0) {
                val key = count.toString(radix)
                payload = payload.replace(Regex("\\b$key\\b"), keys.getOrElse(count) { key })
            }
            return payload
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        Log.e(DEBUG_TAG, "Extractor '$name' started for initial URL: $url")

        val intermediatePage = app.get(url, referer = referer).text
        if (intermediatePage.isBlank()) {
            Log.e(DEBUG_TAG, "Failed to get a response from the intermediate URL. It was empty.")
            return
        }

        val playerUrl = Regex("""<iframe.*?src=["']([^"']+)["']""").find(intermediatePage)?.groupValues?.get(1)
        if (playerUrl == null) {
            Log.e(DEBUG_TAG, "CHECKPOINT 1 FAILED: Could not find the player iframe URL on the intermediate page.")
            return
        }
        Log.e(DEBUG_TAG, "CHECKPOINT 1 PASSED: Found player URL: $playerUrl")

        val playerPage = app.get(playerUrl, referer = url).text
        if (playerPage.isBlank()) {
            Log.e(DEBUG_TAG, "Failed to get a response from the player page URL. It was empty.")
            return
        }

        val packedJS = Regex("""eval\(function\(p,a,c,k,e,d\).*?\)""").find(playerPage)?.value
        if (packedJS == null) {
            Log.e(DEBUG_TAG, "CHECKPOINT 2 FAILED: Could not find the packed JavaScript block on the PLAYER page.")
            return
        }
        Log.e(DEBUG_TAG, "CHECKPOINT 2 PASSED: Found packed JavaScript block.")

        val unpackedText = unpack(packedJS)
        if (unpackedText == null) {
            Log.e(DEBUG_TAG, "CHECKPOINT 3 FAILED: The unpack function failed to decode the JavaScript.")
            return
        }
        Log.e(DEBUG_TAG, "CHECKPOINT 3 PASSED: Successfully unpacked text.")

        val m3u8Url = Regex("""(https?:\/\/[^"']+\.m3u8)""").find(unpackedText)?.groupValues?.get(1)
        if (m3u8Url == null) {
            Log.e(DEBUG_TAG, "CHECKPOINT 4 FAILED: Could not find the .m3u8 link inside the unpacked text.")
            return
        }
        Log.e(DEBUG_TAG, "CHECKPOINT 4 PASSED: Found m3u8 URL: $m3u8Url")

        M3u8Helper.generateM3u8(name, m3u8Url, playerUrl).forEach(callback)
        Log.e(DEBUG_TAG, "SUCCESS: Links generated and sent to player!")
    }
}


// The rest of the file remains the same.
open class Emturbovid : PackerExtractor() {
    override val name = "Emturbovid"
    override val mainUrl = "https://emturbovid.com"
}
open class Filemoon : PackerExtractor() {
    override val name = "Filemoon"
    override val mainUrl = "https://filemoon.sx"
}
open class Hydrax : PackerExtractor() {
    override val name = "Hydrax"
    override val mainUrl = "https://short.icu"
}
open class Furher : PackerExtractor() {
    override val name = "Furher"
    override val mainUrl = "https://furher.in"
}
open class HowNetwork : PackerExtractor() {
    override val name = "HowNetwork"
    override val mainUrl = "https://cloud.hownetwork.xyz"
}
