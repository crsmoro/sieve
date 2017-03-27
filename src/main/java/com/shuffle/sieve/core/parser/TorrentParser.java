package com.shuffle.sieve.core.parser;

import java.util.Date;
import java.util.List;

public interface TorrentParser {

	List<String> getRows(String htmlContent);

	long getId(String rowContent);

	String getNome(String rowContent);

	double getSize(String rowContent);

	Date getAdded(String rowContent);

	String getCategory(String rowContent);

	String getLink(String rowContent);

	String getDownlodLink(String rowContent);

	final static long KB_FACTOR = 1024;

	final static long MB_FACTOR = 1024 * KB_FACTOR;

	final static long GB_FACTOR = 1024 * MB_FACTOR;

	public default double parseSize(String size) {
		int spaceNdx = size.indexOf(" ");
		double ret = Double.parseDouble(size.substring(0, spaceNdx));
		switch (size.substring(spaceNdx + 1)) {
		case "GB":
			return ret * GB_FACTOR;
		case "MB":
			return ret * MB_FACTOR;
		case "KB":
			return ret * KB_FACTOR;
		}
		return -1;
	}
}
