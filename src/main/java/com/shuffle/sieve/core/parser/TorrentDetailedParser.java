package com.shuffle.sieve.core.parser;

public interface TorrentDetailedParser {

	String getImdbLink(String htmlContent);

	String getYoutubeLink(String htmlContent);

	long getAno(String htmlContent);

	String getContent(String htmlContent);
}
