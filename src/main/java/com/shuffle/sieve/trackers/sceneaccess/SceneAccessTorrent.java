package com.shuffle.sieve.trackers.sceneaccess;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.sieve.core.parser.TorrentParser;

public class SceneAccessTorrent implements TorrentParser {

	private final String baseUrl = "https://sceneaccess.eu/";

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (Element element : Jsoup.parse(content).select("#torrents-table > tbody > tr.tt_row")) {
			rows.add(element.outerHtml());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("href").replace("details?id=", ""));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("title");
	}

	@Override
	public String getLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.td_dl > a").first().attr("href");
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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_size").first().html().split("<br>")[0].replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_added").first().html().replaceAll("<br>", " ");
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
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_type > a > img").first().attr("alt");
	}

}
