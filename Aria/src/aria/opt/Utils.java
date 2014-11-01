package aria.opt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
	
	public static String toJson( Object object) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		return gson.toJson(object);
	}
	public static boolean toJsonFile(String file, Object object) {
		return writeJson(file, toJson(object));
	}

	public static boolean writeJson(String file, String json) {
		try {
			FileUtils.forceMkdir(new File(file).getParentFile());
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
//		System.out.println(json);
		return true;
	}
	
	public static <T> T fromJson(File file, Class<T> classT) {
		return fromJson(file.getPath(), classT);
	}
	
	  /**
	   *
	   * @param <T> the type of the desired object
	   * @param file file path to read from.
	   */
	public static <T> T fromJson(String file, Class<T> classT) {
		Gson gson = new Gson();
		T t = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			t = gson.fromJson(reader, classT );
//			System.out.println(url);
		} catch (Exception e) {
			System.err.println("no file found");
			return null;
		}
		return t;
	}
	
	/***
	 * return true only if it is a true URL
	 * @param fileURL {@link String} that represent that URL to test 
	 * @return {@link Boolean}
	 */
    public static boolean verifyURL(String fileURL) {
    	
        String str = fileURL.toLowerCase();
        // Allow FTP, HTTP and HTTPS URLs.
        if (str.startsWith("http://") || str.startsWith("https://")
                || str.startsWith("ftp://")) {
            // Verify format of URL.
            URL verifiedUrl = null;
            try {
                verifiedUrl = new URL(fileURL);
            } catch (MalformedURLException e) {
                System.err.println("not valid url");
                return false;
            }

            // Make sure URL specifies a file.
            if (verifiedUrl.getFile().length() < 9) {
                return false;
            }
//             System.out.println(verifiedUrl.toString() + " >>");
        }else{
        	return false;
        }
        return true;
    }
    
    
	  /**
	   * @param length file path to read from.
	   */
    public static String fileLengthUnite(double length) {
        String hrSize = "";
        DecimalFormat dec = new DecimalFormat("0.000");
        
        double b = length;
        double k = length / 1024.0;
        double m = ((length / 1024.0) / 1024.0);
        double g = (((length / 1024.0) / 1024.0) / 1024.0);
        double t = ((((length / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        if (t >= 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g >= 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m >= 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k >= 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static double fileLength(long length) {
        double k = length / 1024.0;
        double m = ((length / 1024.0) / 1024.0);
        double g = (((length / 1024.0) / 1024.0) / 1024.0);
        double t = ((((length / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        if (t > 1) {
            return t;
        } else if (g > 1) {
            return g;
        } else if (m > 1) {
            return m;
        } else if (k > 1) {
            return k;
        }
        return length;
    }
    
    

    public static String percent(long downloaded, long size) {
        String hrSize = "(";
        DecimalFormat dec = new DecimalFormat("0.000");
        hrSize += dec.format(100 * ((float) downloaded / size)).concat(" %)");
        return hrSize;
    }
    
    
    public static double percent(double downloaded, double size) {
        return downloaded / size;
    }
	public static  String fileLengthUnite(double progress, final long length) {
		return fileLengthUnite(progress*length);
	}
	
	static DateFormat format =
			new SimpleDateFormat("DD:HH:mm:ss:SS", Locale.getDefault(Locale.Category.DISPLAY));
	public static String calTimeLeft(long date){
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(date));
	}
}
