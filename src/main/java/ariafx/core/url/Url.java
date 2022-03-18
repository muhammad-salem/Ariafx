package ariafx.core.url;

import ariafx.opt.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Url {

    public String url;
    public String referer;

    public Url(URL url) {
        this.url = url.toString();
        this.referer = "";
    }

    public Url(URL url, URL referer) {
        this.url = url.toString();
        this.referer = referer.toString();
    }

    public Url(String url) throws MalformedURLException {
        try {
            if (url.indexOf("%") < 6) {
                url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Utils.verifyURL(url)) {
            this.url = url;
            this.referer = "";
        } else {
            System.err.println("fired exp");
            throw new MalformedURLException("not url formate");
        }
    }

    public Url(String url, String referer) throws MalformedURLException {
        this(url);
        this.referer = referer;
    }

    public static List<Url> fromEf2IDMFile(File file) {
        List<Url> urls = new ArrayList<Url>();

        List<String> list;
        try {
            list = FileUtils.readLines(file, Charset.defaultCharset());
        } catch (IOException e) {
            return new ArrayList<Url>();
        }
        list.removeIf(str -> str.contains(">") || str.contains("<"));

        int i = 0;
        do {
            try {
                Url url = new Url(list.get(i));
                urls.add(url);
                ++i;
                if (i >= list.size()) break;
                if (list.get(i).contains("referer: ")) {
                    String str = list.get(i).replaceFirst("referer: ", "");
                    url.setReferer(str);
                    ++i;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        } while (true);
        return urls;
    }

    public static List<Url> fromTextFile(File file) {
        List<Url> urls = new ArrayList<Url>();
        List<String> list;
        try {
            list = FileUtils.readLines(file, Charset.defaultCharset());
        } catch (IOException e) {
            return Collections.emptyList();
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                Url url = new Url(list.get(i));
                urls.add(url);
            } catch (Exception e) {
                continue;
            }
        }
        return urls;
    }

    @Override
    public String toString() {
        String str = "URL:		" + url;
        if (!referer.equals("")) str += "\nReferer:	" + referer;
        return str;
    }

    public String toIDMString() {
        String str = "<\n" + url;
        if (!referer.equals("")) str += "\nreferer: " + referer;
        str += "\n>";
        return str;
    }

    public boolean setURL(String link) {
        try {
            if (link.indexOf("%") < 6) {
                link = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Utils.verifyURL(link)) {
            url = link;
            return true;
        }
        return false;
    }

    public String getURL() {
        return url;
    }

    public URL getUrl() {
        try {
            URL url = new URL(this.url);
            return url;
        } catch (MalformedURLException e) {
        }
        return null;
    }


    public boolean setReferer(String link) {
        try {
            if (link.indexOf("%") < 6) {
                link = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Utils.verifyURL(link)) {
            referer = link;
            return true;
        }
        return false;
    }

    public String getReferer() {
        return referer;
    }

    public URL getReFeReR() {
        try {
            URL url = new URL(this.referer);
            return url;
        } catch (MalformedURLException e) {
        }
        return null;
    }

}
