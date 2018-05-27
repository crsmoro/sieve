package com.shuffle.sieve.trackers.iptorrents;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentParser;

public class IPTorrentsTorrent implements TorrentParser {

	private final String baseUrl = "https://www.iptorrents.com";

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		Jsoup.parse(content).select("#torrents > tbody > tr").stream().skip(1).forEach(element -> {
			rows.add(element.outerHtml());
		});
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first()
				.attr("href").replace("/details.php?id=", ""));
	}

	@Override
	public String getNome(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().text();
	}

	@Override
	public String getLink(String row) {
		return baseUrl
				+ Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().attr("href");
	}

	@Override
	public String getDownlodLink(String row) {
		return baseUrl
				+ Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(4) > a").first().attr("href");
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
		return parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(6)").first().html()
				.replaceAll(",", ""));
	}
	
	private final Map<ChronoUnit, Long> units = new HashMap<>();
	
	{
		units.put(ChronoUnit.SECONDS, 1L);
		units.put(ChronoUnit.MINUTES, 60L);
		units.put(ChronoUnit.HOURS, 60L);
		units.put(ChronoUnit.HALF_DAYS, 12L);
		units.put(ChronoUnit.DAYS, 24L);
		units.put(ChronoUnit.WEEKS, 4L);
		units.put(ChronoUnit.MONTHS, 12L);
		units.put(ChronoUnit.YEARS, 365L);
	}

	@Override
	public Date getAdded(String row) {
		String timeAgoString = Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > div.t_ctime").first().text();
		Pattern pattern = Pattern.compile("((\\d+)(\\.\\d)) (seconds|hours|minutes|days|months|years)( ago by (.*)|)");
		Matcher matcher = pattern.matcher(timeAgoString);
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
		if (matcher.find() && matcher.groupCount() > 0) {
			
			ChronoUnit unit = ChronoUnit.valueOf(matcher.group(4).toUpperCase());
			
			String stringValueInteger = matcher.group(2);
			String stringValueDecimal = matcher.group(3);
			dateTime = dateTime.minus(Long.valueOf(stringValueInteger), unit);
			BigDecimal decimal = new BigDecimal("0" + stringValueDecimal).multiply(new BigDecimal(units.get(ChronoUnit.values()[unit.ordinal() - 1 - (unit.equals(ChronoUnit.DAYS) ? 1 : 0)])));
			dateTime = dateTime.minus(decimal.longValue(), ChronoUnit.values()[unit.ordinal() - 1 - (unit.equals(ChronoUnit.DAYS) ? 1 : 0)]);
			return new Date(dateTime.atZone(ZoneId.of("America/Sao_Paulo")).toEpochSecond() * 1000);
		}
		return null;
	}

	@Override
	public String getCategory(String row) {
		return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(1)").first().text();
	}

}
