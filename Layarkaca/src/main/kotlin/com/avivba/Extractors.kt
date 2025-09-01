package com.avivba

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.extractors.Filesim
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.getQualityFromName

// ---------------- Emturbovid ----------------
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
        val firstPage = app.get(url, referer = referer)

        val embeddedUrl = Regex("""https.*?turboviplay.*?["']""")
            .find(firstPage.text)
            ?.value
            ?.replace("\"", "")
            ?.replace("'", "")

        if (embeddedUrl == null) return

        val secondPage = app.get(embeddedUrl, referer = url)

        val m3u8 = Regex("""https.*?\.m3u8""")
            .find(secondPage.text)
            ?.value
            ?: return

        M3u8Helper.generateM3u8(name, m3u8, mainUrl).forEach(callback)
    }
}

// ---------------- Filemoon ----------------
open class Filemoon : ExtractorApi() {
    override val name = "Filemoon"
    override val mainUrl = "https://filemoon.sx"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val firstPage = app.get(url, referer = referer)

        val embeddedUrl = Regex("""src=["'](.*?filemoon.*?)["']""")
            .find(firstPage.text)
            ?.groupValues?.get(1)

        val targetUrl = embeddedUrl ?: url
        val secondPage = app.get(targetUrl, referer = url)

        val m3u8 = Regex("""https.*?\.m3u8""")
            .find(secondPage.text)
            ?.value
            ?: return

        M3u8Helper.generateM3u8(name, m3u8, mainUrl).forEach(callback)
    }
}

// ---------------- Hydrax ----------------
open class Hydrax : ExtractorApi() {
    override val name = "Hydrax"
    override val mainUrl = "https://short.icu"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val firstPage = app.get(url, referer = referer)

        val embeddedUrl = Regex("""src=["'](.*?hydrax.*?)["']""")
            .find(firstPage.text)
            ?.groupValues?.get(1)

        val targetUrl = embeddedUrl ?: url
        val secondPage = app.get(targetUrl, referer = url)

        val m3u8 = Regex("""https.*?\.m3u8""")
            .find(secondPage.text)
            ?.value
            ?: return

        M3u8Helper.generateM3u8(name, m3u8, mainUrl).forEach(callback)
    }
}

// ---------------- Furher ----------------
open class Furher : ExtractorApi() {
    override val name = "Furher"
    override val mainUrl = "https://furher.in"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val firstPage = app.get(url, referer = referer)

        val embeddedUrl = Regex("""src=["'](.*?furher.*?)["']""")
            .find(firstPage.text)
            ?.groupValues?.get(1)

        val targetUrl = embeddedUrl ?: url
        val secondPage = app.get(targetUrl, referer = url)

        val m3u8 = Regex("""https.*?\.m3u8""")
            .find(secondPage.text)
            ?.value
            ?: return

        M3u8Helper.generateM3u8(name, m3u8, mainUrl).forEach(callback)
    }
}

// ---------------- HowNetwork ----------------
open class HowNetwork : ExtractorApi() {
    override val name = "HowNetwork"
    override val mainUrl = "https://cloud.hownetwork.xyz"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val firstPage = app.get(url, referer = referer)

        val embeddedUrl = Regex("""src=["'](.*?hownetwork.*?)["']""")
            .find(firstPage.text)
            ?.groupValues?.get(1)

        val targetUrl = embeddedUrl ?: url
        val secondPage = app.get(targetUrl, referer = url)

        val m3u8 = Regex("""https.*?\.m3u8""")
            .find(secondPage.text)
            ?.value
            ?: return

        M3u8Helper.generateM3u8(name, m3u8, mainUrl).forEach(callback)
    }
}
