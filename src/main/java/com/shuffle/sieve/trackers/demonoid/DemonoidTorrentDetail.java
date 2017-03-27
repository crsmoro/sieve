package com.shuffle.sieve.trackers.demonoid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;

public class DemonoidTorrentDetail implements TorrentDetailedParser {

	@Override
	public String getImdbLink(String content) {
		String imdbLink = "";
		Document document = Jsoup.parse(content);
		String docBody = document.select("#fslispc > table > tbody > tr > td > table:nth-child(3)").text();
		Pattern pattern = Pattern.compile("(http:\\/\\/www.imdb.com\\/title\\/tt(\\d+))", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(docBody);
		if (matcher.find() && matcher.groupCount() > 0) {
			imdbLink = matcher.group(0);
		}
		return imdbLink;
	}

	@Override
	public String getYoutubeLink(String content) {
		return null;
	}

	@Override
	public long getAno(String content) {
		return 0;
	}

	@Override
	public String getContent(String content) {
		return Jsoup.parse(content).select("#fslispc > table > tbody > tr > td > table:nth-child(3)").outerHtml();
	}

}
