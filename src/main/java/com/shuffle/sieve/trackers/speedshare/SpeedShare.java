package com.shuffle.sieve.trackers.speedshare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.sieve.core.parser.TorrentDetailedParser;
import com.shuffle.sieve.core.parser.TorrentParser;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;

public class SpeedShare implements Tracker {

	private final String name = "Speed-Share";
	
	public final static String BASE_URL = "https://www.speed-share.org/";

	private final String url = BASE_URL + "buscar_tor.php?order=desc&sort=id";

	private final String authenticationUrl = BASE_URL + "account-login.php";

	private final ParameterType parameterType = ParameterType.DEFAULT;

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final boolean hasCaptcha = false;

	private final String captchaField = null;

	private final String authenticationMethod = "POST";
	
	private final String searchMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "search";

	private final String categoryField = "cat";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{

		categories.add(new TrackerCategory("Filmes : 4K", "", "244"));
		categories.add(new TrackerCategory("Filmes : HD", "", "214"));
		categories.add(new TrackerCategory("Filmes : DVD", "", "215"));
		categories.add(new TrackerCategory("Filmes : Rip", "", "218"));
		categories.add(new TrackerCategory("Filmes : TV 4K", "", "258"));
		categories.add(new TrackerCategory("Filmes : TV", "", "217"));
		categories.add(new TrackerCategory("Filmes : WEB 4K", "", "259"));
		categories.add(new TrackerCategory("Filmes : WEB", "", "216"));
		categories.add(new TrackerCategory("Filmes : Autorado", "", "220"));
		categories.add(new TrackerCategory("Filmes : Qualidade Inferior", "", "219"));

		categories.add(new TrackerCategory("Adulto : HD 4K", "", "239"));
		categories.add(new TrackerCategory("Adulto : HD", "", "187"));
		categories.add(new TrackerCategory("Adulto : DVD", "", "62"));
		categories.add(new TrackerCategory("Adulto : Rip", "", "63"));
		categories.add(new TrackerCategory("Adulto : TV 4K", "", "248"));
		categories.add(new TrackerCategory("Adulto : TV", "", "141"));
		categories.add(new TrackerCategory("Adulto : WEB 4K", "", "249"));
		categories.add(new TrackerCategory("Adulto : WEB", "", "225"));
		categories.add(new TrackerCategory("Adulto : Autorado", "", "226"));
		categories.add(new TrackerCategory("Adulto : Qualidade inferior", "", "68"));
		categories.add(new TrackerCategory("Adulto : Revistas", "", "101"));
		categories.add(new TrackerCategory("Adulto : Jogos", "", "207"));
		
		categories.add(new TrackerCategory("Animês : HD 4K", "", "240"));
		categories.add(new TrackerCategory("Animês : HD", "", "189"));
		categories.add(new TrackerCategory("Animês : DVD", "", "102"));
		categories.add(new TrackerCategory("Animês : Rip", "", "103"));
		categories.add(new TrackerCategory("Animês : TV 4K", "", "250"));
		categories.add(new TrackerCategory("Animês : TV", "", "144"));
		categories.add(new TrackerCategory("Animês : WEB 4K", "", "251"));
		categories.add(new TrackerCategory("Animês : WEB", "", "227"));
		categories.add(new TrackerCategory("Animês : Autorado", "", "228"));
		categories.add(new TrackerCategory("Animês : Qualidade inferior", "", "191"));

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
		return Jsoup.parse(htmlContent).select("a[href=\"http://speed-share.org/account-recover.php\"]").size() <= 0;
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
		return map;
	}

	@Override
	public String getSearchMethod() {
		return searchMethod;
	}

	@Override
	public Map<String, String> getSearchAdditionalParameters() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("action", "scrollpagination");
		map.put("number", "30");
		map.put("offset", "0");
		map.put("width", "1920");
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
		return new SpeedShareTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new SpeedShareTorrentDetail();
	}

}
