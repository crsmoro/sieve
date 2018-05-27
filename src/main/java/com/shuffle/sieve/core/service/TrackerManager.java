package com.shuffle.sieve.core.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.shuffle.sieve.core.bittorrent.TorrentFile;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.TrackerManagerFactory;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.sieve.core.parser.bean.Torrent;

public interface TrackerManager {

	long DELAY_BETWEEN_REQUESTS = 2000;

	int MAX_ATTEMPTS = 2;

	Tracker getTracker();

	String getUsername();

	String getPassword();

	void setCaptcha(String captcha);

	QueryParameters getQueryParameters();

	void setQueryParameters(QueryParameters queryParameters);

	void setPage(long page);

	long getPage();

	boolean authenticate();

	List<Torrent> fetchTorrents();

	Torrent getDetails(Torrent torrent);

	TorrentFile download(Torrent torrent);

	/**
	 * Call the URL to the Tracker, using the same authentication parameters and
	 * validation to get a valid return
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 */
	String callURL(String url, Map<String, String> parameters);

	Set<TrackerManager> trackerManagerInstances = new HashSet<>();

	/**
	 * Returns an instance to manage the tracker<br>
	 * <b>TorrentLeech</b><br>
	 * <b>SceneAccess</b><br>
	 * <b>Demonoid</b><br>
	 * <b>Manicomio-Share</b><br>
	 * <b>Speed-Share</b><br>
	 * 
	 * @see Tracker
	 * @see Tracker#getName()
	 * @see TrackerManager
	 * @param trackerName
	 *            Official tracker name
	 * @return {@link TrackerManager}`s instance
	 */
	static TrackerManager getInstance(String trackerName, String username, String password) {
		return getInstance(Tracker.getInstance(trackerName), username, password);
	}

	/**
	 * Returns an instance to manage the tracker
	 * 
	 * @see Tracker
	 * @see TrackerManager
	 * @param tracker
	 *            {@linkplain Tracker}'s instance
	 * @return {@link TrackerManager}`s instance
	 */
	static TrackerManager getInstance(final Tracker tracker, final String username, final String password) {
		TrackerManager trackerManager = trackerManagerInstances.stream()
				.filter(tm -> tm.getTracker()
						.equals(Optional.ofNullable(tracker)
								.orElseThrow(() -> new IllegalArgumentException("Tracker required")))
						&& tm.getUsername()
								.equals(Optional.ofNullable(username)
										.orElseThrow(() -> new IllegalArgumentException("Username required")))
						&& tm.getPassword()
								.equals(Optional.ofNullable(password)
										.orElseThrow(() -> new IllegalArgumentException("Password required"))))
				.findFirst().orElse(null);
		if (trackerManager == null) {
			trackerManager = TrackerManagerFactory.newInstance(tracker, username, password);
			trackerManagerInstances.add(trackerManager);
		}
		return getInstance(trackerManager);
	}

	/**
	 * Returns an custom TrackerManager implementation
	 * 
	 * @see TrackerManager
	 * @param trackerManager
	 * @return {@link TrackerManager}`s instance
	 */
	static TrackerManager getInstance(TrackerManager trackerManager) {
		trackerManager.setCaptcha(null);
		return trackerManager;
	}

}
