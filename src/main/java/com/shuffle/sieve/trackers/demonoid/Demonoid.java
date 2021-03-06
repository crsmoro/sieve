package com.shuffle.sieve.trackers.demonoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class Demonoid implements Tracker {

	private final String name = "Demonoid";
	
	public final static String BASE_URL = "http://www.demonoid.pw";

	private final String url = BASE_URL + "/files/";

	private final String authenticationUrl = BASE_URL + "/account_handler.php";

	private final ParameterType parameterType = ParameterType.DEFAULT;

	private final String usernameField = "nickname";

	private final String passwordField = "password";
	
	private final boolean hasCaptcha = false;
	
	private final String captchaField = null;

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "query";

	private final String categoryField = "category";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{
		categories.add(new TrackerCategory("Applications", "", "5"));
		categories.add(new TrackerCategory("Audio Books", "", "17"));
		categories.add(new TrackerCategory("Books", "", "11"));
		categories.add(new TrackerCategory("Comics", "", "10"));
		categories.add(new TrackerCategory("Games", "", "4"));
		categories.add(new TrackerCategory("Japanese Anime", "", "9"));
		categories.add(new TrackerCategory("Miscellaneous", "", "6"));
		categories.add(new TrackerCategory("Movies", "", "1"));
		categories.add(new TrackerCategory("Music", "", "2"));
		categories.add(new TrackerCategory("Music Videos", "", "13"));
		categories.add(new TrackerCategory("Pictures", "", "8"));
		categories.add(new TrackerCategory("TV", "", "3"));

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getAuthenticationUrl() {
		return authenticationUrl;
	}

	@Override
	public ParameterType getParameterType() {
		return parameterType;
	}

	@Override
	public boolean isAuthenticated(String htmlContent) {
		return Jsoup.parse(htmlContent).select("#nickname").size() <= 0;
	}

	@Override
	public String getUsernameField() {
		return usernameField;
	}

	@Override
	public String getPasswordField() {
		return passwordField;
	}

	@Override
	public boolean hasCaptcha() {
		return hasCaptcha;
	}

	@Override
	public String captchaField() {
		return captchaField;
	}

	@Override
	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	@Override
	public Map<String, String> getAuthenticationAdditionalParameters() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("withq", "1");
		map.put("returnpath", "/");
		return map;
	}

	@Override
	public String getPageField() {
		return pageField;
	}

	@Override
	public String getSearchField() {
		return searchField;
	}

	@Override
	public String getCategoryField() {
		return categoryField;
	}

	@Override
	public List<TrackerCategory> getCategories() {
		return categories;
	}

	@Override
	public String getPageValue(long page) {
		return String.valueOf(page);
	}

	@Override
	public TorrentParser getTorrentParser() {
		return new DemonoidTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new DemonoidTorrentDetail();
	}

}
