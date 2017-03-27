package com.shuffle.sieve.trackers.torrentleech;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.sieve.core.parser.TorrentParser;

public class TorrentLeechTorrent implements TorrentParser {

	private final String baseUrl = "https://www.torrentleech.org/";

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (Element element : Jsoup.parse(content).select("#torrenttable > tbody > tr[id]")) {
			rows.add(element.outerHtml());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr").attr("id"));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td.name > span.title > a").first().text();
	}

	@Override
	public String getLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.name > span.title > a").first().attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.quickdownload > a").first().attr("href");
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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(5)").first().text().replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String textFull = Jsoup.parse(getFullContentToParse(row)).select("tr > td.name").first().html();
		String afterBr = textFull.split("<br>")[1];
		String dateString = afterBr.split("on ")[afterBr.split("on ").length - 1];
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
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td.name b").first().text();
	}

}
