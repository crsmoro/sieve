package com.shuffle.sieve.core.service;

import java.util.List;

import com.shuffle.sieve.core.bittorrent.TorrentFile;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.sieve.core.parser.bean.Torrent;

public interface TrackerManager {

	long DELAY_BETWEEN_REQUESTS = 2000;

	int MAX_ATTEMPTS = 2;

	Tracker getTracker();

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);

	void setCaptcha(String captcha);

	void setUser(String username, String password);

	void setUser(String username, String password, String captcha);

	QueryParameters getQueryParameters();

	void setQueryParameters(QueryParameters queryParameters);

	void setPage(long page);

	long getPage();

	boolean authenticate();

	List<Torrent> fetchTorrents();

	Torrent getDetails(Torrent torrent);

	TorrentFile download(Torrent torrent);
}
