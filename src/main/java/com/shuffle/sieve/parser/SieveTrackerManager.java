package com.shuffle.sieve.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;
import org.apache.http.annotation.ThreadSafe;

import com.shuffle.sfhttprequest.SfHttpRequest;
import com.shuffle.sieve.core.bittorrent.TorrentFile;
import com.shuffle.sieve.core.exception.AuthenticationException;
import com.shuffle.sieve.core.exception.CaptchaException;
import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.exception.TimeoutException;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.Tracker.ParameterType;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.sieve.core.parser.bean.TorrentFilter;
import com.shuffle.sieve.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;
import com.shuffle.sieve.core.service.TrackerManager;

@ThreadSafe
public class SieveTrackerManager implements TrackerManager {

	private static final Log log = LogFactory.getLog(SieveTrackerManager.class);

	private boolean authenticated;

	private final Tracker tracker;

	private String username;

	private String password;

	private String captcha;

	private QueryParameters queryParameters;

	private long page;

	private SfHttpRequest httpRequest = new SfHttpRequest();

	private Lock lock = new ReentrantLock();
	
	public SieveTrackerManager(Tracker tracker) {
		this.tracker = tracker;
	}

	private Map<ParameterType, Map<String, String>> urlPatterns = new HashMap<>();

	{
		Map<String, String> urlPatternsDefault = new HashMap<>();
		urlPatternsDefault.put("initial-separator", "?");
		urlPatternsDefault.put("separator", "&");
		urlPatternsDefault.put("assigner", "=");
		urlPatterns.put(ParameterType.DEFAULT, urlPatternsDefault);

		Map<String, String> urlPatternsPath = new HashMap<>();
		urlPatternsPath.put("initial-separator", "/");
		urlPatternsPath.put("separator", "/");
		urlPatternsPath.put("assigner", "/");
		urlPatterns.put(ParameterType.PATH, urlPatternsPath);
	}

	@Override
	public Tracker getTracker() {
		return tracker;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setUsername(String username) {
		lock.lock();
		authenticated = authenticated && username.equals(this.username);
		this.username = username;
		lock.unlock();
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		lock.lock();
		authenticated = authenticated && username.equals(this.username);
		this.password = password;
		lock.unlock();
	}

	@Override
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	@Override
	public void setUser(String username, String password) {
		lock.lock();
		setUsername(username);
		setPassword(password);
		lock.unlock();
	}

	@Override
	public void setUser(String username, String password, String captcha) {
		lock.lock();
		setUser(username, password);
		this.captcha = captcha;
		lock.unlock();

	}

	@Override
	public QueryParameters getQueryParameters() {
		return queryParameters;
	}

	@Override
	public void setQueryParameters(QueryParameters queryParameters) {
		lock.lock();
		this.queryParameters = queryParameters;
		lock.unlock();
	}

	@Override
	public void setPage(long page) {
		lock.lock();
		this.page = page;
		lock.unlock();
	}

	@Override
	public long getPage() {
		return page;
	}

	private List<Torrent> buildResults(String body) {
		log.debug("QueryParameters : " + getQueryParameters());
		List<Torrent> torrents = new ArrayList<Torrent>();
		for (String row : getTracker().getTorrentParser().getRows(body)) {
			Torrent torrent = new Torrent();
			torrent.setTracker(getTracker());
			torrent.setUsername(getUsername());
			torrent.setPassword(getPassword());
			torrent.setId(getTracker().getTorrentParser().getId(row));
			torrent.setName(getTracker().getTorrentParser().getNome(row));
			torrent.setCategory(getTracker().getTorrentParser().getCategory(row));
			torrent.setAdded(getTracker().getTorrentParser().getAdded(row));
			torrent.setSize(getTracker().getTorrentParser().getSize(row));
			torrent.setLink(getTracker().getTorrentParser().getLink(row));
			torrent.setDownloadLink(getTracker().getTorrentParser().getDownlodLink(row));

			boolean add = getQueryParameters().getTorrentFilters().isEmpty();
			if (!add) {
				log.debug("getting torrent details");
				getDetails(torrent);
			}
			log.trace(torrent);
			int totalFilters = getQueryParameters().getTorrentFilters().size();
			int totalPass = 0;
			for (TorrentFilter torrentFilter : getQueryParameters().getTorrentFilters()) {
				try {
					Field field = Torrent.class.getDeclaredField(torrentFilter.getField());
					field.setAccessible(true);
					Object torrentValue = field.get(torrent);
					if (torrentValue instanceof Long) {
						Long longTorrentValue = Long.valueOf(torrentValue.toString());
						Long longFilterValue = Long.valueOf(torrentFilter.getValue().toString());
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && longTorrentValue.equals(longFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !longTorrentValue.equals(longFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && longTorrentValue.compareTo(longFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && longTorrentValue.compareTo(longFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && longTorrentValue.compareTo(longFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && longTorrentValue.compareTo(longFilterValue) <= 0) {
							totalPass++;
							continue;
						}
					} else if (torrentValue instanceof Double) {
						Double doubleTorrentValue = Double.valueOf(torrentValue.toString());
						Double doubleFilterValue = Double.valueOf(torrentFilter.getValue().toString());
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && doubleTorrentValue.equals(doubleFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !doubleTorrentValue.equals(doubleFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && doubleTorrentValue.compareTo(doubleFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && doubleTorrentValue.compareTo(doubleFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && doubleTorrentValue.compareTo(doubleFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && doubleTorrentValue.compareTo(doubleFilterValue) <= 0) {
							totalPass++;
							continue;
						}
					} else if (torrentValue instanceof String) {
						String stringTorrentValue = torrentValue.toString();
						String stringFilterValue = torrentFilter.getValue().toString();
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && stringTorrentValue.equals(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !stringTorrentValue.equals(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && stringTorrentValue.compareTo(stringFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && stringTorrentValue.compareTo(stringFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && stringTorrentValue.compareTo(stringFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && stringTorrentValue.compareTo(stringFilterValue) <= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LIKE) && stringTorrentValue.contains(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NLIKE) && !stringTorrentValue.contains(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.REGEX) && stringTorrentValue.matches(stringFilterValue)) {
							totalPass++;
							continue;
						}
					}

				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}
			}
			add = totalFilters == totalPass;
			if (add) {
				log.trace("added on return list!");
				torrents.add(torrent);
			}
		}
		return torrents;
	}

	private String buildUrlCategory() {
		StringBuilder urlCategory = new StringBuilder();
		for (TrackerCategory trackerCategory : getQueryParameters().getTrackerCategories()) {
			if (getTracker().getCategories().contains(trackerCategory)) {
				if (getTracker().getParameterType().equals(ParameterType.DEFAULT)) {
					if (urlCategory.length() > 0) {
						urlCategory.append(urlPatterns.get(getTracker().getParameterType()).get("separator"));
					}
					if (StringUtils.isNotBlank(trackerCategory.getProperty())) {
						urlCategory.append(trackerCategory.getProperty());
					} else if (StringUtils.isNotBlank(getTracker().getCategoryField())) {
						urlCategory.append(getTracker().getCategoryField());
					}
					if (StringUtils.isNotBlank(trackerCategory.getCode())) {
						urlCategory.append(urlPatterns.get(getTracker().getParameterType()).get("assigner") + trackerCategory.getCode());
					}
				} else if (getTracker().getParameterType().equals(ParameterType.PATH)) {
					if (urlCategory.length() > 0) {
						urlCategory.append(",");
					}
					if (StringUtils.isNotBlank(trackerCategory.getCode())) {
						urlCategory.append(trackerCategory.getCode());
					}
				}
			}
		}
		return urlCategory.toString();
	}

	private String buildUrlFetchTorrents() {
		StringBuilder url = new StringBuilder();
		url.append(getTracker().getUrl() + (getTracker().getUrl().contains(urlPatterns.get(getTracker().getParameterType()).get("initial-separator"))
				? (getTracker().getParameterType().equals(ParameterType.DEFAULT) ? urlPatterns.get(getTracker().getParameterType()).get("separator") : "") : urlPatterns.get(getTracker().getParameterType()).get("initial-separator")));

		if (getTracker().getParameterType().equals(ParameterType.PATH) && StringUtils.isNotBlank(getTracker().getCategoryField()) && !getQueryParameters().getTrackerCategories().isEmpty()) {
			url.append(getTracker().getCategoryField() + urlPatterns.get(getTracker().getParameterType()).get("separator"));
		}
		String urlCategory = buildUrlCategory();
		if (urlCategory.length() > 0) {
			url.append(urlCategory.toString());
		}
		if (!url.substring(url.length() - 1, url.length()).equals(urlPatterns.get(getTracker().getParameterType()).get("separator"))) {
			url.append(urlPatterns.get(getTracker().getParameterType()).get("separator"));
		}
		url.append(getTracker().getSearchField() + urlPatterns.get(getTracker().getParameterType()).get("assigner"));
		if (StringUtils.isNoneBlank(getQueryParameters().getSearch())) {
			try {
				url.append(URLEncoder.encode(getQueryParameters().getSearch(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				log.warn("Error encoding search query value, using value without encode", e);
				url.append(getQueryParameters().getSearch());
			}			
		}
		url.append(urlPatterns.get(getTracker().getParameterType()).get("separator") + getTracker().getPageField() + urlPatterns.get(getTracker().getParameterType()).get("assigner") + getTracker().getPageValue(getPage()));
		return url.toString();
	}

	@Override
	public List<Torrent> fetchTorrents() {
		lock.lock();
		try {
			String url = buildUrlFetchTorrents();
			log.info("URL : " + url.toString());

			List<Torrent> torrents = new ArrayList<>();
			if (authenticate()) {
				httpRequest.clearParameters();
				tracker.getSearchAdditionalParameters().forEach(httpRequest::addParameter);
				httpRequest.setHttpMethod(tracker.getSearchMethod());
				httpRequest.setUrl(url.toString());
				getLoggedContent(httpRequest, new SieveAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						throw new TimeoutException("Timeout trying to fetch torrents");
					}

					@Override
					public void contentLoaded(String content, byte[] byteContent) {
						torrents.addAll(buildResults(content));
					}
				});
			} else {
				throw new AuthenticationException(getTracker().getName() + " Invalid Username and/or password");
			}
			return torrents;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Torrent getDetails(Torrent torrent) {
		lock.lock();
		try {

			if (getTracker().getTorrentDetailedParser() == null) {
				throw new SieveException("Torrent Detailed Parser not set");
			}
			if (authenticate()) {
				httpRequest.clearParameters();
				httpRequest.setHttpMethod("GET");
				httpRequest.setUrl(torrent.getLink());

				getLoggedContent(httpRequest, new SieveAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						throw new TimeoutException("Timeout trying to fetch torrents");
					}

					@Override
					public void contentLoaded(String content, byte[] byteContent) {
						torrent.setDetailed(true);
						torrent.setYear(getTracker().getTorrentDetailedParser().getAno(content));
						torrent.setYoutubeLink(getTracker().getTorrentDetailedParser().getYoutubeLink(content));
						torrent.setImdbLink(getTracker().getTorrentDetailedParser().getImdbLink(content));
						torrent.setContent(getTracker().getTorrentDetailedParser().getContent(content));
					}
				});
			} else {
				throw new AuthenticationException(getTracker().getName() + " Invalid Username and/or password");
			}

			return torrent;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean authenticate() {
		lock.lock();
		try {
			int attemptLogin = 0;
			SfHttpRequest sfHttpRequest = new SfHttpRequest();
			sfHttpRequest.setUrl(getTracker().getAuthenticationUrl()).setHttpMethod(getTracker().getAuthenticationMethod());
			sfHttpRequest.addParameter(getTracker().getUsernameField(), getUsername());
			sfHttpRequest.addParameter(getTracker().getPasswordField(), getPassword());
			getTracker().getAuthenticationAdditionalParameters().forEach(sfHttpRequest::addParameter);
			if (getTracker().hasCaptcha()) {
				if (StringUtils.isBlank(getTracker().captchaField())) {
					throw new SieveException("Need to set a captcha field name");
				} else if (StringUtils.isNotBlank(captcha)) {
					sfHttpRequest.addParameter(getTracker().captchaField(), captcha);
				}
			}
			while (!authenticated && attemptLogin < TrackerManager.MAX_ATTEMPTS) {
				if (getTracker().hasCaptcha()) {
					if (StringUtils.isBlank(captcha)) {
						throw new CaptchaException("Tracker " + getTracker().getName() + " has captcha, " + getUsername() + " needs to login manually");
					}
				}
				log.debug("Login Attempt " + attemptLogin);
				attempt(sfHttpRequest, new SieveAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						if (statusLine.getStatusCode() != 200 || StringUtils.isBlank(content)) {
							throw new TimeoutException("Timeout trying to login");
						}
					}

					@Override
					public void contentLoaded(String content, byte[] byteContent) {
						authenticated = getTracker().isAuthenticated(content);
						if (authenticated) {
							httpRequest.getCookieStore().clear();
							httpRequest.addCookies(sfHttpRequest.getCookies());
						}
					}
				});
				attemptLogin++;
				if (!authenticated) {
					try {
						Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
					} catch (InterruptedException dontcare) {

					}
				}
			}
			return authenticated;
		} finally {
			lock.unlock();
		}
	}

	private void getLoggedContent(SfHttpRequest httpRequest, SieveAttemptListener listener) {
		int loggedAttempt = 0;
		boolean reqOk = false;
		SfHttpRequest sfHttpRequest = null;
		try {
			sfHttpRequest = httpRequest.clone();
		} catch (CloneNotSupportedException e1) {
			SieveException sieveException = new SieveException("Generic Error");
			sieveException.addSuppressed(e1);
			throw sieveException;
		}

		while (!reqOk && loggedAttempt < TrackerManager.MAX_ATTEMPTS) {
			log.debug("Logged attempt " + loggedAttempt);
			try {
				sfHttpRequest.request();
			} catch (TimeoutException e) {
				log.info("Logged attempt " + loggedAttempt + " timeout, trying again", e);
			}
			String response = sfHttpRequest.getStringResponse();
			if (sfHttpRequest.getStatusLine().getStatusCode() == 200 && StringUtils.isNotBlank(response) && getTracker().isAuthenticated(response)) {
				log.debug("content ok, auth ok");
				log.trace(response);
				listener.contentLoaded(response, sfHttpRequest.getByteArrayResponse());
				reqOk = true;
			} else if (sfHttpRequest.getStatusLine().getStatusCode() == 200 && StringUtils.isBlank(response)
					&& !getTracker().isAuthenticated(sfHttpRequest.setUrl(getHomeUrl(getTracker().getAuthenticationUrl())).setHttpMethod("GET").request().getStringResponse())) {
				log.debug("content nok, auth nok");
				sfHttpRequest = httpRequest;
				authenticated = false;
				authenticate();
			}
			loggedAttempt++;
			if (!reqOk) {
				try {
					Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
				} catch (InterruptedException dontcare) {

				}
			}
		}
		if (!reqOk) {
			authenticated = false;
			listener.loadFailed(httpRequest.getStatusLine(), sfHttpRequest.getStringResponse());
		}
	}

	private String getHomeUrl(String url) {
		try {
			URI uri = new URI(url);
			String homeUrl = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() >= 0 ? ":" + uri.getPort() : "");
			return homeUrl;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void attempt(SfHttpRequest httpRequest, SieveAttemptListener listener) {
		boolean reqOk = false;
		int attempt = 0;
		while (!reqOk && attempt < TrackerManager.MAX_ATTEMPTS) {
			log.debug("Attempt " + attempt);
			try {
				httpRequest.request();
			} catch (TimeoutException e) {
				log.info("Attempt " + attempt + " timeout, trying again", e);
			}
			String response = httpRequest.getStringResponse();
			byte[] byteResponse = httpRequest.getByteArrayResponse();
			if (httpRequest.getStatusLine().getStatusCode() == 200 && (StringUtils.isNotBlank(response) || byteResponse.length > 0)) {
				listener.contentLoaded(response, byteResponse);
				reqOk = true;
			}
			attempt++;
			if (!reqOk) {
				try {
					Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
				} catch (InterruptedException dontcare) {

				}
			}
		}
		if (!reqOk) {
			listener.loadFailed(httpRequest.getStatusLine(), httpRequest.getStringResponse());
		}
	}

	private InputStream inputStreamDownload;

	@Override
	public TorrentFile download(Torrent torrent) {
		lock.lock();
		try {
			if (authenticate())
			{				
				httpRequest.clearParameters();
				httpRequest.setHttpMethod("GET");
				httpRequest.setUrl(torrent.getDownloadLink());
				getLoggedContent(httpRequest, new SieveAttemptListener() {
					
					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						
					}
					
					@Override
					public void contentLoaded(String content, byte[] byteContent) {
						inputStreamDownload = new ByteArrayInputStream(byteContent);
					}
				});
				return new TorrentFile(inputStreamDownload);
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	private interface SieveAttemptListener {

		void contentLoaded(String content, byte[] byteContent);

		void loadFailed(StatusLine statusLine, String content);
	}
}
