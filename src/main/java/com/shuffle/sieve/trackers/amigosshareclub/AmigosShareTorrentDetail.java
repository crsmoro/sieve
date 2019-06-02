package com.shuffle.sieve.trackers.amigosshareclub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;

public class AmigosShareTorrentDetail implements TorrentDetailedParser {

	@Override
	public String getImdbLink(String htmlContent) {
		Document document = Jsoup.parse(htmlContent);
		String imdbLink = "";
		String docBody = document.html();
		Pattern pattern = Pattern.compile("(http(|s):\\/\\/www.imdb.com\\/title\\/tt(\\d+))", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(docBody);
		if (matcher.find() && matcher.groupCount() > 0) {
			imdbLink = matcher.group(0);
		}
		return imdbLink;
	}

	@Override
	public String getYoutubeLink(String htmlContent) {
		String youtubeLink = "";
		String embedLink = Jsoup.parse(htmlContent).select("iframe[src^=\"http://www.youtube.com/embed\"]").attr("src");
		if (StringUtils.isBlank(embedLink)) {
			embedLink = Jsoup.parse(htmlContent).select("iframe[src^=\"https://www.youtube.com/embed\"]").attr("src");
		}
		if (StringUtils.isNotBlank(embedLink)) {
			String[] elSplit = embedLink.split("/");
			youtubeLink = elSplit[elSplit.length - 1].split("\\?")[0];
			youtubeLink = "https://www.youtube.com/watch?v=" + youtubeLink;
		}
		return youtubeLink;
	}

	@Override
	public String getContent(String htmlContent) {
		Document document = Jsoup.parse(htmlContent);
		return document.select("div.row > div:eq(1)").html();
	}

}
