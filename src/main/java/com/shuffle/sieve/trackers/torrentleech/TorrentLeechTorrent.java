package com.shuffle.sieve.trackers.torrentleech;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class TorrentLeechTorrent implements TorrentParser {

	private final String baseUrl = "https://www.torrentleech.org/";

	private final JsonParser jsonParser = new JsonParser();

	@Override
	public List<String> getRows(String content) {
		List<String> rows = new ArrayList<>();
		for (JsonElement jsonElement : jsonParser.parse(content).getAsJsonObject().get("torrentList")
				.getAsJsonArray()) {
			rows.add(jsonElement.getAsJsonObject().toString());
		}
		return rows;
	}

	@Override
	public long getId(String row) {
		return Long.valueOf(jsonParser.parse(row).getAsJsonObject().get("fid").getAsLong());

	}

	@Override
	public String getNome(String row) {
		return jsonParser.parse(row).getAsJsonObject().get("name").getAsString();
	}

	@Override
	public String getLink(String row) {
		return baseUrl + "torrent/" + jsonParser.parse(row).getAsJsonObject().get("fid").getAsString();
	}

	@Override
	public String getDownlodLink(String row) {
		JsonObject jsonObject = jsonParser.parse(row).getAsJsonObject();
		return baseUrl + "download/" + jsonObject.get("fid").getAsString() + "/"
				+ jsonObject.get("filename").getAsString();
	}

	@Override
	public double getSize(String row) {
		return jsonParser.parse(row).getAsJsonObject().get("size").getAsDouble();
	}

	@Override
	public Date getAdded(String row) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String textFull = jsonParser.parse(row).getAsJsonObject().get("addedTimestamp").getAsString();
		Date date = null;
		try {
			date = dateFormat.parse(textFull);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	@Override
	public String getCategory(String row) {
		String categoryID = jsonParser.parse(row).getAsJsonObject().get("categoryID").getAsString();
		return Tracker.getInstance(TorrentLeech.class).getCategories().stream()
				.filter(t -> t.getCode().equals(categoryID)).findFirst().orElseGet(TrackerCategory::new).getName();
	}

}
