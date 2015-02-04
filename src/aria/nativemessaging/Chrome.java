/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aria.nativemessaging;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moha
 */
public class Chrome {
	
	public static String extensions_id = "gaogianbgnmoompbfkmgnefkbehmeijh";

	public static void sendLength(int len) throws IOException {
		byte[] bs = getBytes(len);
		System.out.write(bs);
	}

	public static void sendMSG(String msg) {
		//System.out.print(msg);
		try {
			System.out.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMessage(String message) {
		try {
			sendLength(message.length());
			sendMSG(message);
		} catch (IOException ex) {
			{
				// what you want to do if flier case
			}
		}
	}

	public static int readLength() throws IOException {
		/*
		char[] cs = new char[4];
		for (int i = 0; i < cs.length; i++) {
			cs[i] = (char)System.in.read();
		}
		*/
		
		byte[] bs = new byte[4];
		int i = System.in.read(bs);
		if(i == 4)
			return getLength(bs);
		else 
			return -1;
	}

	public static String readMSG(int length) {
		String msg = "";
		try {

			// Scanner scanner = new Scanner(System.in);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

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
			Logger.getLogger(Chrome.class.getName())
					.log(Level.SEVERE, null, ex);
		}
		return msg;
	}

	public static String readMessage() {
		int length;
		try {
			length = readLength();
			return readMSG(length);
		} catch (IOException ex) {

		}
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
		return 	(bytes[3] << 24) & 0xff000000 |
				(bytes[2] << 16) & 0x00ff0000 |
				(bytes[1] << 8) & 0x0000ff00 | 
				(bytes[0]) & 0x000000ff;
	}

	public static int getLength(char[] bytes) {
		return 	(bytes[3] << 24) & 0xff000000 | 
				(bytes[2] << 16) & 0x00ff0000|
				(bytes[1] << 8) & 0x0000ff00 | 
				(bytes[0]) & 0x000000ff;
	}

	
	public static void mainTest(String[] args) throws Exception {
		if(args[0].contains(extensions_id)){
			FileOutputStream stream = new FileOutputStream("/home/salem/Desktop/logStream.txt", true);
			stream.write(Arrays.toString(args).getBytes());
			int l = readLength();
			stream.write(String.valueOf(l).getBytes());
			String str = readMSG(l);
			ChromeMSG msg = ChromeMSG.CreateMessage(str);
			
			stream.write(msg.toString().getBytes());
			
			stream.close();
		}
	}

}
