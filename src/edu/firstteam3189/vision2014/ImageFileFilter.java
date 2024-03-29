package edu.firstteam3189.vision2014;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Unused
 */
public class ImageFileFilter implements FilenameFilter {
	private Pattern patt;

	public ImageFileFilter() {
		patt = Pattern.compile("[processed ]*image.*\\.jpg");
	}

	@Override
	public boolean accept(File dir, String name) {
		return patt.matcher(name).matches();
	}
}
