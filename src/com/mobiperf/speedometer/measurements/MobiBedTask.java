package com.mobiperf.speedometer.measurements;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Debug;


import com.google.myjson.Gson;
import com.mobiperf.speedometer.Logger;
import com.mobiperf.speedometer.MeasurementDesc;
import com.mobiperf.speedometer.MeasurementError;
import com.mobiperf.speedometer.MeasurementResult;
import com.mobiperf.speedometer.MeasurementTask;
import com.mobiperf.util.AndroidContribClassProvider;
import com.mobiperf.util.MobiBedUtils;
import com.mobiperf.util.PhoneUtils;


import drcl.ruv.MobiBedShell;
import drcl.ruv.ShellEvalException;



public class MobiBedTask extends MeasurementTask{
	// Type name for internal use
	public static final String TYPE = "mobibed";
	// Human readable name for the task
	public static final String DESCRIPTOR = "MobiBed";
	private static final long TIMEOUT = 5*1000; // 5s
	
	public String script = null;
//	private ShellAndroid shell;
	
	public static class MobiBedDesc extends MeasurementDesc {

		public MobiBedDesc(String key, Date startTime,
				Date endTime, double intervalSec, long count, long priority,
				Map<String, String> params, String script, String contrib_url) {
			super(MobiBedTask.TYPE, key, startTime, endTime, intervalSec, count, 
					priority, params, script, contrib_url);
		}

		@Override
		public String getType() {
			return MobiBedTask.TYPE;
		}

		@Override
		protected void initalizeParams(Map<String, String> params) {
		}
		
	}
	
	
	public MobiBedTask(MeasurementDesc desc, Context parent) {
		super(new MobiBedDesc(desc.key, desc.startTime, desc.endTime, desc.intervalSec, desc.count,
		        desc.priority, desc.parameters, desc.script, desc.contrib_url), parent);
		this.script = desc.script;
	}

	@SuppressWarnings("rawtypes")
	public static Class getDescClass() throws InvalidClassException {
		return MobiBedDesc.class;
	}
	
	@Override
	public String getDescriptor() {
		return DESCRIPTOR;
	}

	@Override
	public MeasurementResult call() throws MeasurementError {
		// TODO Auto-generated method stub
		Logger.d("MobiBed is running");
		return executeMobiBed();
	}
    
    @SuppressWarnings("finally")
	@SuppressLint({ "NewApi", "NewApi" })
	public MeasurementResult executeMobiBed(){
    	if(script == null)
    	{
    		Logger.e("script is empty");
    		return null;
    	}
    	MeasurementResult measurementResult = null;
    	try {
    		
//    		TrafficStats ts = new TrafficStats();
//    		int uid = android.os.Process.myUid();
    		
//    		long startRx = TrafficStats.getUidUdpRxBytes(uid);
//    		long startTx = TrafficStats.getUidUdpTxBytes(uid);
    		
			String argu = "-d";
			drcl.ruv.System.WAITING_CNT = 50; // timeout 50*10s
			drcl.ruv.System.cleanUpSystem();
			drcl.ruv.System.Android = true;
			drcl.ruv.System.registerContribClassProvider(new AndroidContribClassProvider());
			drcl.ruv.System.start(argu.split(" "), script);
			Logger.d("MobiBed system started");

			while(drcl.ruv.System.WAITING_CNT>0)
			{
				drcl.ruv.System.WAITING_CNT--;
				Thread.sleep(TIMEOUT);
				Logger.d("timeout...");
			}
			Logger.d("executeMobiBed timeout");
//			measurementResult = constructResult(drcl.ruv.System.getPcapList());
//			drcl.ruv.System.cleanUpSystem();
//			long usedRx = TrafficStats.getUidUdpRxBytes(uid) - startRx;
//			long usedTx = TrafficStats.getUidUdpTxBytes(uid) - startTx;
//			Logger.v("used Rx data (bytes) = " + usedRx);
//			Logger.v("used Tx data (bytes) = " + usedTx);
//			return measurementResult;
		} 
    	catch (Exception e) {
    		Logger.e("executeMobiBed exception"+ e);
		}finally{
			measurementResult = constructResult(drcl.ruv.System.getPcapList());
			drcl.ruv.System.cleanUpSystem();
			return measurementResult;
		}
    }
	
	private MeasurementResult constructResult(LinkedList<byte[]> pcapList) {
		// TODO Auto-generated method stub
		PhoneUtils phoneUtils = PhoneUtils.getPhoneUtils();
		boolean success = true;
	    MeasurementResult result = new MeasurementResult(phoneUtils.getDeviceInfo().deviceId,
	        phoneUtils.getDeviceProperty(), MobiBedTask.TYPE, System.currentTimeMillis() * 1000, success,
	        this.measurementDesc);
	    if (!pcapList.isEmpty()){
	    	// TODO 
	    	byte[] pcap = constructPcap(pcapList);
	    	// save to file
	    	MobiBedUtils.logData("", "phone.pcap", pcap);
	    	
//	    	result.addResult("pcap", pcap);
	    	Logger.d("add pcap byte[]");
	    }
	    return result;
	}

	private byte[] constructPcap(LinkedList<byte[]> pcapList) {
		int len = 0,tx=0,rx=0;
		for(byte[] pkt: pcapList)
		{	
			len += pkt.length;
		}
		Logger.v("PCAP captured bytes: "+ len);
		len += 24;
		byte[] pcap = new byte[len];
		int pos = 24;
		for(byte[] pkt: pcapList){
			System.arraycopy(pkt, 0, pcap, pos, pkt.length);
			pos += pkt.length;
		}
		// add pcap header
		drcl.inet.tool.PCapTrace.constructPCapHeader(pcap);
 		return pcap;
	}

	@Override
	public String getType() {
		return TYPE;
	}



	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MeasurementTask clone() {
		// TODO Auto-generated method stub
		MeasurementDesc desc = this.measurementDesc;
		MobiBedDesc newdesc = new MobiBedDesc(desc.key, desc.startTime, desc.endTime, desc.intervalSec, desc.count,
		        desc.priority, desc.parameters, desc.script, desc.contrib_url);
		return new MobiBedTask(newdesc, parent);
	}

	

}
