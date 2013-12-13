package com.mobiperf.util;

import java.io.File;

import dalvik.system.DexClassLoader;
import drcl.ruv.ContribClassProvider;

public class AndroidContribClassProvider extends ContribClassProvider {

	@Override
	public Class getClass(String name_) {
		try{
			ClassLoader cl = MobiBedUtils.getClassLoader();
			File outpath = MobiBedUtils.getOutPath();
			if(outpath == null)
				return null;
			DexClassLoader dcl = new DexClassLoader(MobiBedUtils.getContribFile().getAbsolutePath()
					,outpath.getAbsolutePath(),null,cl);
			Class<?> class_ = dcl.loadClass(name_);
			return class_;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
