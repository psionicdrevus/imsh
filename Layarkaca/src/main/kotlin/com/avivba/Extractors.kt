package com.avivba

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.extractors.Filesim
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.getQualityFromName

// --- Emturbovid extractor ---
open class Emturbovid : ExtractorApi() {
    override val name = "Emturbovid"
    override val mainUrl = "https://emturbovid.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer)
        val m3u8 = Regex("[\"'](.*?master\\.m3u8.*?)[\"']")
            .find(response.text)
            ?.groupValues?.getOrNull(1)
            ?: return

        M3u8Helper.generateM3u8(
            name,
            m3u8,
            mainUrl
        ).forEach(callback)
    }
}

class Furher : Filesim() {
    override val name = "Furher"
    override var mainUrl = "https://furher.in"
}

// --- Filemoon extractor ---
open class FilemoonExtractor : ExtractorApi() {
    override val name = "Filemoon"
    override val mainUrl = "https://filemoon.sx"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer)
        val m3u8 = Regex("[\"'](.*?\\.m3u8.*?)[\"']")
            .find(response.text)
            ?.groupValues?.getOrNull(1)
            ?: return

        M3u8Helper.generateM3u8(
            name,
            m3u8,
            mainUrl
        ).forEach(callback)
    }
}

// --- Hydrax extractor (short.icu) ---
open class HydraxExtractor : ExtractorApi() {
    override val name = "Hydrax"
    override val mainUrl = "https://short.icu"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer)
        val m3u8 = Regex("[\"'](.*?\\.m3u8.*?)[\"']")
            .find(response.text)
            ?.groupValues?.getOrNull(1)
            ?: return

        M3u8Helper.generateM3u8(
            name,
            m3u8,
            mainUrl
        ).forEach(callback)
    }
}

// --- HowNetwork extractor (P2P) ---
open class HowNetworkExtractor : ExtractorApi() {
    override val name = "HowNetwork"
    override val mainUrl = "https://cloud.hownetwork.xyz"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, referer = referer)
        val m3u8 = Regex("[\"'](.*?\\.m3u8.*?)[\"']")
            .find(response.text)
            ?.groupValues?.getOrNull(1)
            ?: return

        M3u8Helper.generateM3u8(
            name,
            m3u8,
            mainUrl
        ).forEach(callback)
    }
}
