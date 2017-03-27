package com.shuffle.sieve.core.parser;

import java.util.HashMap;
import java.util.Map;

import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.sieve.parser.SieveTrackerManager;

public abstract class TrackerManagerFactory {
	
	private static Map<Tracker, TrackerManager> trackerManagerInstance = new HashMap<>();

	private TrackerManagerFactory() {

	}
	
	/**
	 * Returns an instance to manage the tracker<br>
	 * <b>TorrentLeech</b><br>
	 * <b>SceneAccess</b><br>
	 * <b>Demonoid</b><br>
	 * <b>Manicomio-Share</b><br>
	 * <b>Speed-Share</b><br>
	 * @see Tracker
	 * @see Tracker#getName()
	 * @see TrackerManager
	 * @param trackerName Official tracker name
	 * @return {@link TrackerManager}`s instance
	 */
	public static TrackerManager getInstance(String trackerName) {
		return getInstance(Tracker.getInstance(trackerName));
	}

	/**
	 * Returns an instance to manage the tracker
	 * @see Tracker
	 * @see TrackerManager
	 * @param tracker {@linkplain Tracker}'s instance
	 * @return {@link TrackerManager}`s instance
	 */
	public static TrackerManager getInstance(Tracker tracker) {
		TrackerManager trackerManager = trackerManagerInstance.get(tracker);
		if (trackerManager == null) {
			trackerManager = new SieveTrackerManager(tracker);
			trackerManagerInstance.put(tracker, trackerManager);
		}
		return getInstance(trackerManager);
	}

	/**
	 * Returns an custom TrackerManager implementation
	 * @see TrackerManager
	 * @param trackerManager
	 * @return {@link TrackerManager}`s instance
	 */
	public static TrackerManager getInstance(TrackerManager trackerManager) {
		trackerManager.setCaptcha(null);
		return trackerManager;
	}
}
