package com.comeplus.droidincupdate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static String caclStringMd5(String src) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes("utf8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return StringUtils.bytesToHex(md.digest());
    }
    
    public static String caclFileMd5(String filePath) throws IOException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        File f = new File(filePath);
        if(!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        DigestInputStream is = null;
        try {
            is = new DigestInputStream(new FileInputStream(new File(filePath)), md);
            IOUtils.pipe(is, null, null);
            return StringUtils.bytesToHex(md.digest());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
