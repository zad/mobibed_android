package com.mobiperf.util;


import java.io.File;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import android.content.Context;
import android.os.Environment;

import com.mobiperf.speedometer.Logger;
import com.mobiperf.speedometer.MeasurementScheduler;






public class MobiBedUtils {
	private static MeasurementScheduler sche;
	private static final int BUF_SIZE = 8 * 1024;
	private static File contrib, outPath;
	private static String SECONDARY_DEX_NAME = "contrib.jar";
	
	public static void registerMeasurementScheduler(MeasurementScheduler ser_){
		sche = ser_;
		contrib = getContrib(SECONDARY_DEX_NAME);
		outPath = sche.getDir("outdex", Context.MODE_PRIVATE);
	}
	
	private static File getContrib(String name){
		if(sche == null)
			return null;
		return new File(sche.getDir("dex", Context.MODE_PRIVATE), name);
	}
	
	public static File getContribFile(){
		return contrib;
	}

	public static ClassLoader getClassLoader(){
		if(sche == null)
			return null;
		return sche.getClassLoader();
	}
	
	public static File getOutPath(){
		return outPath;
	}
	
	public static boolean prepareDex(byte[] data){
		if(sche == null)
			return false;
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(contrib);
			fos.write(data);
			fos.close();
			return true;
		}catch(IOException e){
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			return false;
		}
	}
	  public static void logData(String directory, String fileName, byte[] bytes) {
		  // andong
		  String state = Environment.getExternalStorageState();
		  Logger.i("flashManager: " + state);
		  if (Environment.MEDIA_MOUNTED.equals(state)) {
			  // We can read and write the media
			  File rootdir = Environment.getExternalStorageDirectory();
			  if (rootdir != null){
				  try{
					  File tempDir = new File(rootdir, directory);
					  if (tempDir.exists() == false){
						  tempDir.mkdirs();
					  }
					  File tempFile = new File(tempDir, fileName);
					  if (tempFile.exists() == false){
						  tempFile.createNewFile();
					  }
					  FileOutputStream fw = new FileOutputStream(tempFile);
					  
					  fw.write(bytes);
					  fw.flush();
					  fw.close();
					  Logger.i("flashManager: save to"+ fileName);
				  }
				  catch(Exception e){
					  Logger.e("flashManager: "+e.getMessage());
				  }
			  }else
				  Logger.e("flashManager: "+"rootdir error.");
		  }
	  }

	  public static void logData(String directory, String fileName, String string) {
		  // andong
		  String state = Environment.getExternalStorageState();
		  Logger.i("flashManager: " + state);
		  fileName += System.currentTimeMillis();
		  if (Environment.MEDIA_MOUNTED.equals(state)) {
			  // We can read and write the media
			  File rootdir = Environment.getExternalStorageDirectory();
			  if (rootdir != null){
				  try{
					  File tempDir = new File(rootdir, directory);
					  if (tempDir.exists() == false){
						  tempDir.mkdirs();
					  }
					  File tempFile = new File(tempDir, fileName);
					  if (tempFile.exists() == false){
						  tempFile.createNewFile();
					  }
					  PrintWriter logFile = new PrintWriter(
							  new BufferedWriter(new FileWriter(tempFile, true)));
					  logFile.print(string);
					  logFile.close();
					  Logger.i("flashManager: "+ logFile.toString());
				  }
				  catch(Exception e){
					  Logger.e("flashManager: "+e.getMessage());
				  }
			  }else
				  Logger.e("flashManager: "+"rootdir error.");
		  }
	  }
}
