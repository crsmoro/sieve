package com.shuffle.sieve.trackers.manicomioshare;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.service.TrackerManager;

public class ManicomioShareTorrent implements TorrentParser {

	private JsonParser jsonParser = new JsonParser();

	private TrackerManager trackerManager;

	@Override
	public void setTrackerManager(TrackerManager trackerManager) {
		this.trackerManager = trackerManager;
	}

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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(4) > span").first().text()
				.replaceAll(",", ""));
	}

	@Override
	public Date getAdded(String row) {
		int retries = 0, maxRetries = 3;
		String torrentId = String.valueOf(getId(row));
		while (retries < maxRetries)
		{
			String retorno = trackerManager.callURL("https://www.manicomio-share.com/ajax/ajax2.php",
					Collections.singletonMap("torrent", torrentId));
			JsonElement jsonElement = jsonParser.parse(retorno);
			if (jsonElement.getAsJsonObject().get("TorId").getAsString().equals(torrentId))
			{
				String dateString = jsonElement.getAsJsonObject().get("AdicionadoEm").getAsString();
				return new Date(
						LocalDateTime
						.parse(dateString,
								DateTimeFormatter.ofPattern("EEEE', 'dd' de 'MMMM' de 'yyyy' às 'HH:mm",
										new Locale("pt", "BR")))
						.atZone(ZoneId.of("America/Sao_Paulo")).toEpochSecond() * 1000);
			}
			retries++;
		}
		return null;
	}

	@Override
	public String getCategory(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(1) > a > img").first().attr("title");
	}
}
