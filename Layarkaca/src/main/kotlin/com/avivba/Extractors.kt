package com.avivba

// utils.* might work, but being explicit is better
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper

abstract class PackerExtractor : ExtractorApi() {
    override val requiresReferer = true

    private fun unpack(packedJs: String): String? {
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
        val response = app.get(url, referer = referer).text
        val packedJS = Regex("""eval\(function\(p,a,c,k,e,d\).*?\)""").find(response)?.value ?: return

        val unpackedText = unpack(packedJS) ?: return

        val m3u8Url = Regex("""(https?:\/\/[^"']+\.m3u8)""").find(unpackedText)?.groupValues?.get(1) ?: return

        M3u8Helper.generateM3u8(name, m3u8Url, url).forEach(callback)
    }
}

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
