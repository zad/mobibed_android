package com.mobiperf.speedometer.measurements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import android.content.Context;

import com.mobiperf.speedometer.MeasurementDesc;
import com.mobiperf.speedometer.MeasurementError;
import com.mobiperf.speedometer.MeasurementResult;
import com.mobiperf.speedometer.MeasurementTask;

public class TCPTask extends MeasurementTask {

	// Type name for internal use
	public static final String TYPE = "tcp";
	// Human readable name for the task
	public static final String DESCRIPTOR = "tcp";
	private static final int SIZE = 5024000;
	private static final int BUF_SIZE = 3*1024*1024;//110592;
	public TCPTask(MeasurementDesc measurementDesc, Context parent) {
		super(measurementDesc, parent);
	}
	
	public static class TCPTaskDesc extends MeasurementDesc {

		public TCPTaskDesc(String key, Date startTime,
				Date endTime, double intervalSec, long count, long priority,
				Map<String, String> params, String script, String contrib_url) {
			super(TYPE, key, startTime, endTime, intervalSec, count, priority, params,
					script, contrib_url);
		}

		@Override
		public String getType() {
			return TCPTask.TYPE;
		}

		@Override
		protected void initalizeParams(Map<String, String> params) {			
		}
		
	}

	@Override
	public String getDescriptor() {
		return TCPTask.DESCRIPTOR;
	}

	@Override
	public MeasurementResult call() throws MeasurementError {
//		Debug.startMethodTracing("mobibed");
		System.out.println("tcp task called.");
		Socket clientSocket = null;
		try {
			long start = System.nanoTime();
			System.out.println("start " + System.currentTimeMillis());
//			clientSocket = new Socket("192.168.1.140", 5003);
			clientSocket = new Socket("172.20.21.254", 5003);
			clientSocket.setReceiveBufferSize(BUF_SIZE);
			byte[] data = new byte[SIZE];
		  
			InputStream is = clientSocket.getInputStream();

		  
		  
			int count = 0;
			long curr = System.nanoTime();
			long last;
			while(count < SIZE){
				int num = is.read(data);
				if(num < 0)
					break;
				last = curr;
				curr = System.nanoTime();
//				System.out.println((curr-last)/1000000.0 + " " + System.currentTimeMillis() + " " + num);
				count += num;
			}
		  
//	      for(byte i: data)
//	    	  System.out.println(i);
			is.close();
			clientSocket.close();
			long time = System.nanoTime() - start;
			System.out.println(time/1000000.0 + " " + System.currentTimeMillis());
		} catch (SocketTimeoutException se){
			System.out.println("tcp socket timeout");
		} catch (IOException e) {
			
			e.printStackTrace();
		}  
		finally{
			if(clientSocket!=null)
				try {
					clientSocket.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
//			Debug.stopMethodTracing();
		}
		return null;
	}

	@Override
	public String getType() {
		
		return TYPE;
	}

	@Override
	public MeasurementTask clone() {
		MeasurementDesc desc = this.measurementDesc;
		TCPTaskDesc newdesc = new TCPTaskDesc(desc.key, desc.startTime, desc.endTime, desc.intervalSec, desc.count,
		        desc.priority, desc.parameters, desc.script, desc.contrib_url);
		return null;
	}

	@Override
	public void stop() {
		
		
	}

}
