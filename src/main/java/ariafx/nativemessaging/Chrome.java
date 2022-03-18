/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ariafx.nativemessaging;

import ariafx.notify.Notifier;
import ariafx.opt.R;
import com.sun.javafx.PlatformUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chrome {

    public static String extensions_id = "gaogianbgnmoompbfkmgnefkbehmeijh";

    public static void CheckNetiveMessage() {
        if (PlatformUtil.isMac()) {
            try {
                URL source = Chrome.class.getResource("osx/org.javafx.ariafx.json");
                File parent = new File(R.ConfigPath).getParentFile().getParentFile();
                // /Application Support/Google/Chrome/NativeMessagingHosts/org.javafx.ariafx.json
                parent = new File(parent, "Google/Chrome/NativeMessagingHosts/");
                parent.mkdir();
                File destination = new File(parent, "org.javafx.ariafx.json");
                Notifier.Copy(source, destination);
            } catch (Exception e) {
                R.info("Google chrome : NativeMessagingHosts : " + e.getMessage());
            }
        }
    }

    public static void sendLength(int len) throws IOException {
        byte[] bs = getBytes(len);
        System.out.write(bs);
    }

    public static void sendMSG(String msg) {
        try {
            System.out.write(msg.getBytes());
        } catch (IOException e) {}
    }

    public static void sendMessage(String message) {
        try {
            sendLength(message.length());
            sendMSG(message);
        } catch (IOException ex) {}
    }

    public static int readLength() throws IOException {
        /*
         * char[] cs = new char[4]; for (int i = 0; i < cs.length; i++) { cs[i]
         * = (char)System.in.read(); }
         */

        byte[] bs = new byte[4];
        int i = System.in.read(bs);
        if (i == 4)
            return getLength(bs);
        else
            return -1;
    }

    public static String readMSG(int length) {
        String msg = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));){

            // Scanner scanner = new Scanner(System.in);


            /*
             * byte[] b = new byte[length];
             *
             * try { System.in.read(b); msg= new String(b); } catch (IOException
             * ex) { return ""; }
             */
            char[] cs = new char[length];
            br.read(cs);
            msg = new String(cs);
        } catch (IOException ex) {
            Logger.getLogger(Chrome.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }

    public static String readMessage() {
        int length;
        try {
            length = readLength();
            return readMSG(length).trim();
        } catch (IOException ex) {}
        return "";
    }

    public static byte[] getBytes(int length) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (length & 0xFF);
        bytes[1] = (byte) ((length >> 8) & 0xFF);
        bytes[2] = (byte) ((length >> 16) & 0xFF);
        bytes[3] = (byte) ((length >> 24) & 0xFF);
        return bytes;
    }

    public static int getLength(byte[] bytes) {
        return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000
                | (bytes[1] << 8) & 0x0000ff00 | (bytes[0]) & 0x000000ff;
    }

    public static int getLength(char[] bytes) {
        return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000
                | (bytes[1] << 8) & 0x0000ff00 | (bytes[0]) & 0x000000ff;
    }

}
