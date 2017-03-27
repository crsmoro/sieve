package com.shuffle.sieve.trackers.demonoid;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.sieve.core.parser.TorrentParser;

public class DemonoidTorrent implements TorrentParser {

	private final String baseUrl = Demonoid.BASE_URL;

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		Iterator<Element> iElement = Jsoup.parse(content).select("td.ctable_content_no_pad > table.font_12px > tbody > tr:not([align])").iterator();
		while (iElement.hasNext()) {
			Element element = iElement.next();
			if (element.select("td").size() > 1) {
				content = element.outerHtml();
				element = iElement.next();
				rows.add(content + element.outerHtml());
			}

		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").attr("href").split("/")[3]);
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").text();
	}

	@Override
	public String getLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr").last().select("td").get(2).select("a").last().attr("href");
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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr").last().select("td[align=\"right\"").text().replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		return null;
	}

	@Override
	public String getCategory(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[rowspan=\"2\"] > a > img").attr("alt");
	}

}
