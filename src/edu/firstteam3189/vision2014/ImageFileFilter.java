package edu.firstteam3189.vision2014;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class ImageFileFilter implements FilenameFilter{
	private Pattern patt;
	
	public ImageFileFilter(){
		patt = Pattern.compile("[processed ]*image.*\\.jpg");
	}
	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		return patt.matcher(name).matches();
	}

}
