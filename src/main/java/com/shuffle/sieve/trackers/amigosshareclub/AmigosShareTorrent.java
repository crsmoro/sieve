package com.shuffle.sieve.trackers.amigosshareclub;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.service.TrackerManager;

public class AmigosShareTorrent implements TorrentParser {

	@Override
	public void setTrackerManager(TrackerManager trackerManager) {

	}

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (Element element : Jsoup.parse(content).select("#fancy-list-group > ul.list-group > li.list-group-item")) {
			rows.add(element.outerHtml());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-content > div.tooltips > p > a").attr("href").replace("torrents-details.php?id=", ""));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-content > div.tooltips > p > a").text();
	}

	@Override
	public String getLink(String row) {
		return "https://amigos-share.club/" + Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-content > div.tooltips > p > a").attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return "https://amigos-share.club/" + Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-controls > a.badge.badge-light").attr("href");
	}

	private String getFullContentToParse(String row) {
		StringBuilder rowContent = new StringBuilder();
		rowContent.append("<div>");
		rowContent.append(row);
		rowContent.append("</div>");
		return rowContent.toString();
	}

	@Override
	public double getSize(String row) {
		return parseSize(Optional.ofNullable(Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-content > p.list-group-item-text > span.badge.badge-pill.badge-info")).map(Elements::first).map(Element::text).orElse("0.0")
				.replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		String dateString = Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-content div.tooltips span.info.shadow.p-2.bg-secondary.rounded.position-absolute div.itens_details > p").stream()
				.filter(e -> e.text().contains("Lançado:")).findFirst().orElseThrow(() -> new SieveException("Error parsing date added")).text().replace("Lançado:", "").trim();
		return new Date(LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss", new Locale("pt", "BR"))).atZone(ZoneId.of("America/Sao_Paulo")).toEpochSecond() * 1000);
	}

	@Override
	public String getCategory(String row) {
		return Optional.ofNullable(Jsoup.parse(getFullContentToParse(row)).select("li > div.list-group-item-addon img")).map(Elements::first).orElseThrow(() -> new SieveException("Error parsing category")).attr("src")
				.replace("https://amigos-share.club/images/categories/", "").replace(".png", "").trim();
	}

}
