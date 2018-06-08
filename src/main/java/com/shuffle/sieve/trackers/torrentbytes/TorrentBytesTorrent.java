package com.shuffle.sieve.trackers.torrentbytes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentParser;

public class TorrentBytesTorrent implements TorrentParser {

	private final String baseUrl = "https://www.torrentbytes.net/";

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		Jsoup.parse(content).select("#content table:nth-of-type(2) > tbody > tr").stream().skip(1).forEach(element -> {
			rows.add(element.outerHtml());
		});
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > nobr > a.index")
				.first().attr("href").replace("details.php?id=", "").replace("&hit=1", ""));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > nobr > a.index").first()
				.text();
	}

	@Override
	public String getLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > nobr > a.index").first()
				.attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > nobr > a:not(.index)")
				.first().attr("href");
	}

	private String getFullContentToParse(String row) {
		StringBuilder rowContent = new StringBuilder();
		rowContent.append("<table>");
		rowContent.append(row);
		rowContent.append("</table>");
		return rowContent.toString();
	}

	@Override
	public double getSize(String row) {
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(7)").first().html()
				.replace("<br>", " ").replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(5) > nobr").first().html()
				.replace("<br>", "").replace("\n", "");
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	@Override
	public String getCategory(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(1) > a > img").first().attr("title");
	}

}
