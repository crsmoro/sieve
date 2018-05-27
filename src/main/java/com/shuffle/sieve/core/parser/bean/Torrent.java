package com.shuffle.sieve.core.parser.bean;

import java.util.Date;

import com.shuffle.sieve.core.parser.Tracker;

public class Torrent implements Cloneable {

	private long id;

	private String username;

	private String password;

	private Tracker tracker;

	private String name;

	private double size;

	private Date added;

	private String category;

	private String link;

	private String downloadLink;

	private String imdbLink;

	private String youtubeLink;

	private String content;

	private boolean detailed;
	
	public Torrent() {

	}

	public Torrent(String link) {
		this.link = link;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public String getImdbLink() {
		return imdbLink;
	}

	public void setImdbLink(String imdbLink) {
		this.imdbLink = imdbLink;
	}

	public String getYoutubeLink() {
		return youtubeLink;
	}

	public void setYoutubeLink(String youtubeLink) {
		this.youtubeLink = youtubeLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isDetailed() {
		return detailed;
	}

	public void setDetailed(boolean detailed) {
		this.detailed = detailed;
	}

	@Override
	public String toString() {
		return "Torrent [id=" + id + ", username=" + username + ", password=[Protected], name=" + name + ", size=" + size + ", added=" + added + ", category=" + category + ", link=" + link + ", downloadLink="
				+ downloadLink + ", imdbLink=" + imdbLink + ", youtubeLink=" + youtubeLink + ", content=" + content + ", detailed=" + detailed + "]";
	}

	@Override
	public Torrent clone() throws CloneNotSupportedException {
		return (Torrent) super.clone();
	}

}
