package com.avivba

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.eval

/**
 * This is a base class for extractors that use a common JavaScript packing method.
 * The logic is to find a script block that looks like `eval(function(p,a,c,k,e,d){...})`,
 * execute it to get the deobfuscated code, and then find the m3u8 link within that code.
 */
abstract class PackerExtractor : ExtractorApi() {
    override val requiresReferer = true

    // The core logic is now in this single function
    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        // 1. Get the page content
        val response = app.get(url, referer = referer).text

        // 2. Find the packed JavaScript block. This regex is specific to this type of obfuscation.
        val packedJS = Regex("""eval\(function\(p,a,c,k,e,d\).*?\)""").find(response)?.value
        if (packedJS == null) {
            // Optional: Log an error if the script isn't found
            // log.e(name, "Could not find packed JS for url: $url")
            return
        }

        // 3. Use CloudStream's 'eval' helper to deobfuscate the script
        val unpackedText = eval(packedJS)
        if (unpackedText == null) {
            // log.e(name, "Failed to unpack JS for url: $url")
            return
        }

        // 4. Find the .m3u8 link within the now-unpacked text
        // This regex looks for a full URL ending in .m3u8
        val m3u8Url = Regex("""(https?:\/\/[^"']+\.m3u8)""").find(unpackedText)?.groupValues?.get(1)
        if (m3u8Url == null) {
            // log.e(name, "Could not find m3u8 link in unpacked text for url: $url")
            return
        }

        // 5. Generate the final links and pass them to the player
        M3u8Helper.generateM3u8(name, m3u8Url, url).forEach(callback)
    }
}

// Now, all your original classes become very simple. They just inherit the logic.
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

// HowNetwork might use a different method, but we can try this first.
// If it fails, it will need its own specific logic.
open class HowNetwork : PackerExtractor() {
    override val name = "HowNetwork"
    override val mainUrl = "https://cloud.hownetwork.xyz"
}
