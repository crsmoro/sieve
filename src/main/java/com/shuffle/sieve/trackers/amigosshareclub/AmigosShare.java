package com.shuffle.sieve.trackers.amigosshareclub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class AmigosShare implements Tracker {

	private final String name = "Amigos-Share";

	private final String url = "https://amigos-share.club/torrents-search.php?order=desc&sort=id";

	private final String authenticationUrl = "https://amigos-share.club/account-login.php";

	private final ParameterType parameterType = ParameterType.DEFAULT;

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final boolean hasCaptcha = false;

	private final String captchaField = null;

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "search";

	private final String categoryField = "cat";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{
		categories.add(new TrackerCategory("Filmes", "parent_cat", "Filmes"));
	}

	private final TorrentParser torrentParser = new AmigosShareTorrent();

	private final TorrentDetailedParser torrentDetailedParser = new AmigosShareTorrentDetail();

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
		return Jsoup.parse(htmlContent).select("a[href=\"account-recover.php\"]").size() <= 0;
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
		map.put("returnto", "/index.php");
		map.put("autologout", "yes");
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
		return torrentParser;
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return torrentDetailedParser;
	}

}
