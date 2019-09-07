package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Hash{

    MD5("MD5"),
    SHA1("SHA1"),
    SHA256("SHA-256"),
    SHA512("SHA-512");

    private String name;

    Hash(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String checksum(String path) {
        if (!FileOperations.canRead(path)){
            return null;
        }
        File input = new File(path);
        try (InputStream in = new FileInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance(getName());
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return toHex(digest.digest());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String toHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte currentByte : hash){
            //for (int i = 0; i < hash.length; i++) {

            if ((0xff & currentByte) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & currentByte)));
            } else {
                hexString.append(Integer.toHexString(0xFF & currentByte));
            }
        }
        return hexString.toString();
    }

    public static void main(String args[]) {
        String file = "/tmp/monitors.auditd.log";
        System.out.println("MD5    : " + Hash.MD5.checksum(file));
        System.out.println("SHA1   : " + Hash.SHA1.checksum(file));
        System.out.println("SHA256 : " + Hash.SHA256.checksum(file));
        System.out.println("SHA512 : " + Hash.SHA512.checksum(file));
    }

}