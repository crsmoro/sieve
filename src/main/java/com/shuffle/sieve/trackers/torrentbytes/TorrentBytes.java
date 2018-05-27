package com.shuffle.sieve.trackers.torrentbytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class TorrentBytes implements Tracker {

	private final String name = "TorrentBytes";

	private final String url = "https://www.torrentbytes.net/browse.php";

	private final String authenticationUrl = "https://www.torrentbytes.net/takelogin.php";

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
		categories.add(new TrackerCategory("Movies/Pack", "", "40"));
		categories.add(new TrackerCategory("Movies/SD", "", "19"));
		categories.add(new TrackerCategory("Movies/HD", "", "5"));
		categories.add(new TrackerCategory("Movies/UHD", "", "49"));
		categories.add(new TrackerCategory("Movies/DVD-R", "", "20"));
		categories.add(new TrackerCategory("Movies/Full Blu-ray", "", "12"));

		categories.add(new TrackerCategory("TV/Pack", "", "41"));
		categories.add(new TrackerCategory("TV/SD", "", "33"));
		categories.add(new TrackerCategory("TV/HD", "", "38"));
		categories.add(new TrackerCategory("TV/BRrip", "", "37"));

		categories.add(new TrackerCategory("Games/Pack", "", "42"));
		categories.add(new TrackerCategory("Games/PC", "", "4"));
		categories.add(new TrackerCategory("Games/Consoles", "", "50"));

		categories.add(new TrackerCategory("Music/Pack", "", "43"));
		categories.add(new TrackerCategory("Music/MP3", "", "6"));
		categories.add(new TrackerCategory("Music/FLAC", "", "48"));
		categories.add(new TrackerCategory("Music/DVDR", "", "25"));
		categories.add(new TrackerCategory("Music/Videos", "", "34"));

		categories.add(new TrackerCategory("Apps/PC", "", "1"));
		categories.add(new TrackerCategory("Apps/Misc", "", "22"));
		categories.add(new TrackerCategory("Apple/All", "", "52"));
		categories.add(new TrackerCategory("Linux/All", "", "51"));
		categories.add(new TrackerCategory("Misc", "", "31"));

		categories.add(new TrackerCategory("NonScene/XviD", "", "44"));
		categories.add(new TrackerCategory("NonScene/x264", "", "46"));
		categories.add(new TrackerCategory("NonScene/BRrip", "", "45"));

		categories.add(new TrackerCategory("Anime", "", "23"));
		categories.add(new TrackerCategory("Foreign Titles", "", "28"));

		categories.add(new TrackerCategory("XXX/Pack", "", "21"));
		categories.add(new TrackerCategory("XXX/SD-DVD", "", "9"));
		categories.add(new TrackerCategory("XXX/HD", "", "39"));
		categories.add(new TrackerCategory("XXX/Web", "", "29"));
		categories.add(new TrackerCategory("XXX/IMGSET", "", "24"));

	}

	private final TorrentParser torrentParser = new TorrentBytesTorrent();

	private final TorrentDetailedParser torrentDetailedParser = new TorrentBytesTorrentDetail();

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
		return Jsoup.parse(htmlContent).select("[href=\"recover.php\"]").size() <= 0;
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
		map.put("login", "Log in!");
		map.put("returnto", "/");
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
