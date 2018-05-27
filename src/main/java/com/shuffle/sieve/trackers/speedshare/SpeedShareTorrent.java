package com.shuffle.sieve.trackers.speedshare;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.sieve.core.parser.TorrentParser;

public class SpeedShareTorrent implements TorrentParser {

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (Element element : Jsoup.parse(content).select("#torrentsPortal div[align=\"center\"] > table")) {
			rows.add(element.outerHtml());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(row).select("tr:nth-child(2) > td > a").attr("id"));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(row).select("tr:nth-child(1) > td > strong > a").first().attr("title");
	}

	@Override
	public String getLink(String row) {
		return SpeedShare.BASE_URL + Jsoup.parse(row).select("tr:nth-child(1) > td > strong > a").first().attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return SpeedShare.BASE_URL + "download.php?id=" + Jsoup.parse(row).select("tr:nth-child(2) > td > a").attr("id");
	}

	@Override
	public double getSize(String row) {
		return parseSize(Jsoup.parse(row).select("tr:nth-child(2) > td:nth-child(2) > strong > a[href]").first().parent().html().split("<br>")[0].trim().replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateString = Jsoup.parse(row).select("tr:nth-child(2) > td:nth-child(1) > div.tooltip tr > td:contains(Lançado em:)").first().parent().select("td:nth-child(2)").text().trim();
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
		return Jsoup.parse(row).select("tr:nth-child(2) > td:nth-child(2) > strong > a[href]").first().parent().parent().select("strong").eq(1).text();
	}

}
