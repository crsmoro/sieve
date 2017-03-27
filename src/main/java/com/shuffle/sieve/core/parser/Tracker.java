package com.shuffle.sieve.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.shuffle.sieve.core.parser.bean.TrackerCategory;
import com.shuffle.sieve.trackers.demonoid.Demonoid;
import com.shuffle.sieve.trackers.manicomioshare.ManicomioShare;
import com.shuffle.sieve.trackers.sceneaccess.SceneAccess;
import com.shuffle.sieve.trackers.speedshare.SpeedShare;
import com.shuffle.sieve.trackers.torrentleech.TorrentLeech;

/**
 * Default interface to implement a new tracker parser
 * @see ManicomioShare
 * @see Demonoid
 * @see SceneAccess
 * @see TorrentLeech
 * @see SpeedShare
 * @author crsmoro
 *
 */
public interface Tracker {

	String getName();

	String getUrl();

	String getAuthenticationUrl();

	public enum ParameterType {
		DEFAULT, PATH
	}

	ParameterType getParameterType();

	boolean isAuthenticated(String htmlContent);

	String getUsernameField();

	String getPasswordField();

	boolean hasCaptcha();

	String captchaField();

	String getAuthenticationMethod();
	
	default String getSearchMethod() {
		return "POST";
	}

	Map<String, String> getAuthenticationAdditionalParameters();
	
	default Map<String, String> getSearchAdditionalParameters() {
		return new HashMap<>();
	}

	String getPageField();

	String getSearchField();

	String getCategoryField();

	List<TrackerCategory> getCategories();

	String getPageValue(long page);

	TorrentParser getTorrentParser();

	TorrentDetailedParser getTorrentDetailedParser();

	List<Tracker> loadedTrackers = Collections.unmodifiableList(new Reflections("com.shuffle.sieve.trackers").getSubTypesOf(Tracker.class).stream().filter(t -> !t.isInterface()).map(t -> {
		try {
			return t.newInstance();
		} catch (Exception e) {
		}
		return null;
	}).filter(t -> t != null).collect(Collectors.toList()));

	static Class<? extends Tracker> getClass(String className) {
		return Tracker.loadedTrackers.stream().filter(t -> t.getClass().getName().equalsIgnoreCase(className)).findFirst().orElse(null).getClass();
	}

	static Tracker getInstance(Class<? extends Tracker> clazz) {
		return loadedTrackers.stream().filter(t -> t.getClass().equals(clazz)).findFirst().orElse(null);
	}
	
	static Tracker getInstance(String trackerName) {
		return loadedTrackers.stream().filter(t -> t.getName().equalsIgnoreCase(trackerName)).findFirst().orElse(null);
	}

	default TrackerCategory getCategory(String code) {
		return getCategories().stream().filter(c -> c.getCode().equals(code)).findFirst().orElse(null);
	}
}