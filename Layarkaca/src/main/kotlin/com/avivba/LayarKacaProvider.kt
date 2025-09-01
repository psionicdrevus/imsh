package com.avivba

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.utils.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Element

class LayarKacaProvider : MainAPI() {

    override var mainUrl = LayarKacaUrlFetcher.mainUrl
    private val seriesUrl = LayarKacaUrlFetcher.seriesUrl

    override var name = "LayarKaca"
    override val hasMainPage = true
    override var lang = "id"
    override val supportedTypes = setOf(
            TvType.Movie,
            TvType.TvSeries,
            TvType.AsianDrama
    )

    override val mainPage = mainPageOf(
            "$mainUrl/latest/page/" to "Film Terbaru",
            "$mainUrl/populer/page/" to "Film Terplopuler",
            "$mainUrl/rating/page/" to "Film Berdasarkan IMDb Rating",
            "$mainUrl/most-commented/page/" to "Film Dengan Komentar Terbanyak",
            "$seriesUrl/latest-series/" to "Series Terbaru",
            "$seriesUrl/series/asian/page/" to "Film Asian Terbaru"
    )

    override suspend fun getMainPage(
            page: Int,
            request: MainPageRequest
    ): HomePageResponse {
        val url = if (page == 1 && request.data.endsWith("/page/")) {
            request.data.removeSuffix("page/")
        } else if (page > 1 && request.data.endsWith("/page/")) {
            request.data + page
        } else {
            request.data // For series URLs that might not have pagination
        }

        val document = app.get(url).document
        val home = document.select("div.col-film, article[itemscope]").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private suspend fun getProperLink(url: String): String? {
        if(url.startsWith(seriesUrl)) return url
        val res = app.get(url).document
        return if (res.select("title").text().contains("- Nontondrama", true)) {
            res.selectFirst("div#content a")?.attr("href")
        } else {
            url
        }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val href = fixUrl(this.selectFirst("a")!!.attr("href"))
        val title = this.selectFirst("h3.poster-title, h3.figcaption-title > a")?.text()?.trim() ?: return null
        var posterUrl = fixUrlNull(this.selectFirst("div.poster img, figure.figure-film img")?.attr("src"))
        if (posterUrl == null) {
            posterUrl = fixUrlNull(this.selectFirst("div.poster img, figure.figure-film img")?.attr("data-src"))
        }

        // If poster is null, we can't show it, so return null.
        // This also solves the nullability issue.
        val finalPosterUrl = posterUrl ?: return null

        val isMovie = this.selectFirst("span.episode") == null

        return if(isMovie) {
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = finalPosterUrl
                this@toSearchResult.selectFirst("span.label")?.text()?.trim()?.let { quality ->
                    addQuality(quality)
                }
            }
        } else {
            val episode = this.selectFirst("span.episode strong")?.text()?.toIntOrNull()
            newAnimeSearchResponse(title, href, TvType.TvSeries) {
                this.posterUrl = finalPosterUrl
                addSub(episode)
            }
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        // This search logic is likely broken as the selectors are from the old site.
        // It needs to be updated with the HTML from a search results page.
        val document = app.get("$mainUrl/search.php?s=$query").document
        return document.select("div.search-item").mapNotNull {
            val title = it.selectFirst("a")?.attr("title") ?: ""
            val href = fixUrl(it.selectFirst("a")?.attr("href") ?: return@mapNotNull null)
            val posterUrl = fixUrlNull(it.selectFirst("img.img-thumbnail")?.attr("src"))
            newTvSeriesSearchResponse(title, href, TvType.TvSeries) {
                this.posterUrl = posterUrl
            }
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document

        val isMovie = !url.startsWith(seriesUrl)

        return if (isMovie) {
            val title = document.selectFirst("h1")?.text()?.trim() ?: return null
            val poster = fixUrlNull(document.selectFirst("div.movie-info figure img")?.attr("src"))
            val tags = document.select("div.tag-list a").map { it.text() }
            val year = Regex("\\((\\d{4})\\)").find(title)?.groupValues?.get(1)?.toIntOrNull()
            val description = document.selectFirst("div.synopsis")?.text()?.trim()
            val rating = document.selectFirst("div.info-tag > span > strong")?.text()?.toRatingInt()
            val actors = document.select("p:contains(Bintang Film) a, p:contains(Aktor) a").map { it.text() }
            val recommendations = emptyList<SearchResponse>()

            newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = poster
                this.year = year
                this.plot = description
                this.tags = tags
                this.score = rating
                addActors(actors)
                this.recommendations = recommendations
            }
        } else {
            // TV Series load logic
            val title = document.selectFirst("h1")?.text()?.trim() ?: return null
            val poster = fixUrlNull(document.selectFirst("div.movie-info figure img")?.attr("src"))
            val tags = document.select("div.tag-list a").map { it.text() }
            val year = Regex("\\((\\d{4})\\)").find(title)?.groupValues?.get(1)?.toIntOrNull()
            val description = document.selectFirst("div.synopsis")?.text()?.trim()
            val rating = document.selectFirst("div.info-tag > span > strong")?.text()?.toRatingInt()
            val actors = document.select("p:contains(Bintang Film) a, p:contains(Aktor) a").map { it.text() }
            val recommendations = emptyList<SearchResponse>()

            // THIS EPISODE LOGIC IS A PLACEHOLDER AND LIKELY BROKEN.
            // It is copied from the user's original code and needs to be updated
            // with information about the site's AJAX calls for episodes.
            val episodes = document.select("div.episode-list > a:matches(\\d+)").map {
                val href = fixUrl(it.attr("href"))
                val episode = it.text().toIntOrNull()
                val season = it.attr("href").substringAfter("season-").substringBefore("-").toIntOrNull()
                newEpisode(href) {
                    this.name = "Episode $episode"
                    this.season = season
                    this.episode = episode
                }
            }.reversed()

            newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodes) {
                this.posterUrl = poster
                this.year = year
                this.plot = description
                this.tags = tags
                this.score = rating
                addActors(actors)
                this.recommendations = recommendations
            }
        }
    }

    override suspend fun loadLinks(
            data: String,
            isCasting: Boolean,
            subtitleCallback: (SubtitleFile) -> Unit,
            callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        coroutineScope {
            document.select("li > a[data-url]").forEach {
                launch {
                    val url = it.attr("data-url")
                    when {
                        "emturbovid.com" in url -> {
                            Emturbovid().getUrl(url, data, subtitleCallback, callback)
                        }
                        "filemoon.sx" in url -> {
                            FilemoonExtractor().getUrl(url, data, subtitleCallback, callback)
                        }
                        "short.icu" in url -> {
                            HydraxExtractor().getUrl(url, data, subtitleCallback, callback)
                        }
                        "hownetwork.xyz" in url -> {
                            HowNetworkExtractor().getUrl(url, data, subtitleCallback, callback)
                        }
                        else -> {
                            loadExtractor(url, data, subtitleCallback, callback)
                        }
                    }
                }
            }
        }
        return true
    }
}
