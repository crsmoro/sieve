package com.shuffle.sieve.trackers.manicomioshare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.sieve.core.parser.TorrentParser;

public class ManicomioShareTorrent implements TorrentParser {

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (Element element : Jsoup.parse(content).select("#tbltorrent > tbody > tr[data-id]")) {
			rows.add(element.outerHtml());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr").attr("data-id"));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().attr("title");
	}

	@Override
	public String getLink(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(8) > a").first().attr("href");
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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(4) > span").first().text().replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		return null;
	}

	@Override
	public String getCategory(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(1) > a > img").first().attr("title");
	}

}
