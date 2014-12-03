package com.comeplus.droidincupdate;

public class BSPatch {
	static {
		System.loadLibrary("DroidBSDiff");
	}
	
	public static native int bspatch(String old_file, String new_file, String patch_file);
}
