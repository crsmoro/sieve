package com.shuffle.sieve.core.parser;

import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.sieve.parser.SieveTrackerManager;

public abstract class TrackerManagerFactory {

	private TrackerManagerFactory() {

	}

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
	public static TrackerManager newInstance(String trackerName, String username, String password) {
		return newInstance(Tracker.getInstance(trackerName), username, password);
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
	public static TrackerManager newInstance(Tracker tracker, String username, String password) {
		return newInstance(new SieveTrackerManager(tracker, username, password));
	}

	/**
	 * Returns an custom TrackerManager implementation
	 * 
	 * @see TrackerManager
	 * @param trackerManager
	 * @return {@link TrackerManager}`s instance
	 */
	public static TrackerManager newInstance(TrackerManager trackerManager) {
		trackerManager.setCaptcha(null);
		return trackerManager;
	}
}
