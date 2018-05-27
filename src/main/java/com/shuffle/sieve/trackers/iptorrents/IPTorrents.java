package com.shuffle.sieve.trackers.iptorrents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class IPTorrents implements Tracker {

	private final String name = "IPTorrents";

	private final String url = "https://www.iptorrents.com/t";

	private final String authenticationUrl = "https://www.iptorrents.com/take_login.php";

	private final ParameterType parameterType = ParameterType.DEFAULT;

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final boolean hasCaptcha = false;

	private final String captchaField = null;

	private final String authenticationMethod = "POST";

	private final String pageField = "p";

	private final String searchField = "q";

	private final String categoryField = "";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{
		categories.add(new TrackerCategory("Movie/3D", "", "40"));
		categories.add(new TrackerCategory("Movie/480p", "", "77"));
		categories.add(new TrackerCategory("Movie/4K", "", "101"));
		categories.add(new TrackerCategory("Movie/BD-R", "", "89"));
		categories.add(new TrackerCategory("Movie/BD-Rip", "", "90"));
		categories.add(new TrackerCategory("Movie/Cam", "", "96"));
		categories.add(new TrackerCategory("Movie/DVD-R", "", "6"));
		categories.add(new TrackerCategory("Movie/HD/Bluray", "", "48"));
		categories.add(new TrackerCategory("Movie/Kids", "", "54"));
		categories.add(new TrackerCategory("Movie/MP4", "", "62"));
		categories.add(new TrackerCategory("Movie/Non-English", "", "38"));
		categories.add(new TrackerCategory("Movie/Packs", "", "68"));
		categories.add(new TrackerCategory("Movie/Web-DL", "", "20"));
		categories.add(new TrackerCategory("Movie/x265", "", "100"));
		categories.add(new TrackerCategory("Movie/Xvid", "", "7"));

		categories.add(new TrackerCategory("TV", "", "73"));
		categories.add(new TrackerCategory("Documentaries", "", "26"));
		categories.add(new TrackerCategory("Sports", "", "55"));
		categories.add(new TrackerCategory("TV/480p", "", "78"));
		categories.add(new TrackerCategory("TV/BD", "", "23"));
		categories.add(new TrackerCategory("TV/DVD-R", "", "24"));
		categories.add(new TrackerCategory("TV/DVD-Rip", "", "25"));
		categories.add(new TrackerCategory("TV/Mobile", "", "66"));
		categories.add(new TrackerCategory("TV/Non-English", "", "82"));
		categories.add(new TrackerCategory("TV/Packs", "", "65"));
		categories.add(new TrackerCategory("TV/Packs/Non-English", "", "83"));
		categories.add(new TrackerCategory("TV/SD/x264", "", "79"));
		categories.add(new TrackerCategory("TV/Web-DL", "", "22"));
		categories.add(new TrackerCategory("TV/x264", "", "3"));
		categories.add(new TrackerCategory("TV/x265", "", "99"));
		categories.add(new TrackerCategory("TV/Xvid", "", "74"));

		categories.add(new TrackerCategory("Games", "", "74"));
		categories.add(new TrackerCategory("Games/Mixed", "", "2"));
		categories.add(new TrackerCategory("Games/Nintendo DS", "", "47"));
		categories.add(new TrackerCategory("Games/PC-ISO", "", "43"));
		categories.add(new TrackerCategory("Games/PC-Rip", "", "45"));
		categories.add(new TrackerCategory("Games/PS2", "", "39"));
		categories.add(new TrackerCategory("Games/PS3", "", "71"));
		categories.add(new TrackerCategory("Games/PSP", "", "40"));
		categories.add(new TrackerCategory("Games/Wii", "", "50"));
		categories.add(new TrackerCategory("Games/Xbox-360", "", "44"));

		categories.add(new TrackerCategory("Music", "", "75"));
		categories.add(new TrackerCategory("Music/Audio", "", "3"));
		categories.add(new TrackerCategory("Music/Flac", "", "80"));
		categories.add(new TrackerCategory("Music/Packs", "", "93"));
		categories.add(new TrackerCategory("Music/Video", "", "37"));
		categories.add(new TrackerCategory("Podcast", "", "21"));

		categories.add(new TrackerCategory("Miscellaneous", "", "76"));
		categories.add(new TrackerCategory("Anime", "", "60"));
		categories.add(new TrackerCategory("Appz", "", "1"));
		categories.add(new TrackerCategory("Appz/Non-English", "", "86"));
		categories.add(new TrackerCategory("AudioBook", "", "64"));
		categories.add(new TrackerCategory("Books", "", "34"));
		categories.add(new TrackerCategory("Comics", "", "94"));
		categories.add(new TrackerCategory("Educational", "", "95"));
		categories.add(new TrackerCategory("Fonts", "", "98"));
		categories.add(new TrackerCategory("Mac", "", "69"));
		categories.add(new TrackerCategory("Magazines / Newspapers", "", "92"));
		categories.add(new TrackerCategory("Mobile", "", "58"));
		categories.add(new TrackerCategory("Pics/Wallpapers", "", "36"));

		categories.add(new TrackerCategory("XXX", "", "88"));
		categories.add(new TrackerCategory("XXX/Magazines", "", "85"));
		categories.add(new TrackerCategory("XXX/Movie", "", "8"));
		categories.add(new TrackerCategory("XXX/Movie/0Day", "", "81"));
		categories.add(new TrackerCategory("XXX/Packs", "", "91"));
		categories.add(new TrackerCategory("XXX/Pics/Wallpapers", "", "84"));

	}

	private final TorrentParser torrentParser = new IPTorrentsTorrent();

	private final TorrentDetailedParser torrentDetailedParser = new IPTorrentsTorrentDetail();

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
		return String.valueOf(page + 1);
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
