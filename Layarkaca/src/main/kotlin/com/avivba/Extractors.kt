package com.avivba

// utils.* might work, but being explicit is better
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper
// =======================================================
// THIS IS THE FIX: We must import the 'eval' function
// =======================================================
import com.lagradost.cloudstream3.utils.eval


/**
 * This is a base class for extractors that use a common JavaScript packing method.
 * The logic is to find a script block that looks like `eval(function(p,a,c,k,e,d){...})`,
 * execute it to get the deobfuscated code, and then find the m3u8 link within that code.
 */
abstract class PackerExtractor : ExtractorApi() {
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer).text

        val packedJS = Regex("""eval\(function\(p,a,c,k,e,d\).*?\)""").find(response)?.value
        if (packedJS == null) {
            return
        }

        // Now that 'eval' is imported, this will work correctly
        val unpackedText = eval(packedJS)
        if (unpackedText == null) {
            return
        }

        // The second error will also be fixed because 'unpackedText' is now correctly identified as a String
        val m3u8Url = Regex("""(https?:\/\/[^"']+\.m3u8)""").find(unpackedText)?.groupValues?.get(1)
        if (m3u8Url == null) {
            return
        }

        M3u8Helper.generateM3u8(name, m3u8Url, url).forEach(callback)
    }
}

// The rest of the file remains the same
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
