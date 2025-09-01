package com.avivba

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.lagradost.cloudstream3.base64Decode
import com.lagradost.cloudstream3.extractors.*
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.loadExtractor

open class Uplayer : ExtractorApi() {
    override val name = "Uplayer"
    override val mainUrl = "https://uplayer.xyz"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val res = app.get(url,referer=referer).text
        val m3u8 = Regex("file:\\s*\"(.*?m3u8.*?)\"").find(res)?.groupValues?.getOrNull(1)
        M3u8Helper.generateM3u8(
            name,
            m3u8 ?: return,
            mainUrl
        ).forEach(callback)
    }

}

open class Kotakajaib : ExtractorApi() {
    override val name = "Kotakajaib"
    override val mainUrl = "https://kotakajaib.me"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        coroutineScope {
            app.get(url,referer=referer).document.select("ul#dropdown-server li a").forEach {
                launch {
                    loadExtractor(base64Decode(it.attr("data-frame")), "$mainUrl/", subtitleCallback, callback)
                }
            }
        }
    }

}

class OiiaoiiaStream : Chillx() {
    override var name = "Oiiaoiia"
    override var mainUrl = "https://oiiaoiia.stream"
}

class MovEarnPre : VidHidePro() {
    override var name = "MovEarnPre"
    override var mainUrl = "https://movearnpre.com"
}

class DhcPlay : VidHidePro() {
    override var name = "DhcPlay"
    override var mainUrl = "https://dhcplay.com"
}

class Doods : DoodLaExtractor() {
    override var name = "Doods"
    override var mainUrl = "https://doods.pro"
}

class Dutamovie21 : StreamSB() {
    override var name = "Dutamovie21"
    override var mainUrl = "https://dutamovie21.xyz"
}

class FilelionsTo : Filesim() {
    override val name = "Filelions"
    override var mainUrl = "https://filelions.to"
}

class FilelionsOn : Filesim() {
    override val name = "Filelions"
    override var mainUrl = "https://filelions.online"
}

class Lylxan : Filesim() {
    override val name = "Lylxan"
    override var mainUrl = "https://lylxan.com"
}

class Embedwish : Filesim() {
    override val name = "Embedwish"
    override var mainUrl = "https://embedwish.com"
}

class Likessb : StreamSB() {
    override var name = "Likessb"
    override var mainUrl = "https://likessb.com"
}

class DbGdriveplayer : Gdriveplayer() {
    override var mainUrl = "https://database.gdriveplayer.us"
}

///////

class GhBrisk : Filesim() {
    override var mainUrl = "https://ghbrisk.com"
}

class IPlayerHLS : Filesim() {
    override var mainUrl = "https://iplayerhls.com"
}
