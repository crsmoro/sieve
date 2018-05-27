package com.shuffle.sieve.trackers.torrentleech;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;

public class TorrentLeechTorrentDetail implements TorrentDetailedParser {

	@Override
	public String getImdbLink(String content) {
		return Jsoup.parse(content).select("a[href*=\"http://www.imdb.com/title\"]").attr("href");
	}

	@Override
	public String getYoutubeLink(String htmlContent) {
		String youtubeLink = "";
		String embedLink = Jsoup.parse(htmlContent).select("iframe[src^=\"http://www.youtube.com/embed\"]").attr("src");
		if (embedLink != null && !"".equalsIgnoreCase(embedLink)) {
			String[] elSplit = embedLink.split("/");
			youtubeLink = elSplit[elSplit.length - 1].split("\\?")[0];
			youtubeLink = "https://www.youtube.com/watch?v=" + youtubeLink;
		}
		return youtubeLink;
	}

	@Override
	public String getContent(String htmlContent) {
		return Jsoup.parse(htmlContent).select(".torrent_info_details").html();
	}

}
