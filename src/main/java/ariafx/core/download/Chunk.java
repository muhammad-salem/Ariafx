package ariafx.core.download;

import ariafx.core.download.ProxySetting.ProxyConfig;
import ariafx.core.download.ProxySetting.ProxyType;
import ariafx.core.url.Item;
import ariafx.gui.fxml.control.ChunkUI;
import ariafx.opt.R;
import ariafx.opt.Setting;
import ariafx.opt.Utils;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class Chunk extends Service<Number> implements ChunkUI {

    private final String url;
    private final String cachedFile;
    private final Item item;
    private final SimpleIntegerProperty id = new SimpleIntegerProperty(this, "id", -1);
    private final SimpleStringProperty stateCode = new SimpleStringProperty(this, "stateCode", "");
    private final SimpleStringProperty size = new SimpleStringProperty(this, "size", "");
    private CloseableHttpClient httpClient;
    private HttpGet httpGet;
    private HttpClientContext context;
    private Header[] headers;
    private long[] range;
    private RandomAccessFile file;
    private boolean stop = false;
    //private SimpleStringProperty done = new SimpleStringProperty(this, "done", "");
    private int httpStateCode;


    public Chunk(int id, Item item) {
        super();
        setId(id + 1);
        this.item = item;
        this.url = item.getURL();
        this.cachedFile = item.getCacheFile();
        this.range = item.ranges[id];

    }

    public Chunk(CloseableHttpClient httpClient, Item item, int id) {
        super();
        this.httpClient = httpClient;
        setId(id + 1);
        this.item = item;
        this.url = item.getURL();
        this.cachedFile = item.getCacheFile();
        this.range = item.ranges[id];

    }


    private void closeConnection() {
        if (httpClient != null) {
            try {
                httpClient.close();
                if (file != null)
                    file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void scheduled() {
        initChunk();
        super.scheduled();
    }

    @Override
    public boolean cancel() {
        stop = true;
        closeConnection();
        return super.cancel();
    }

    @Override
    protected void failed() {
        closeConnection();
        stop = true;
    }

    private void initChunk() {
        if (httpClient == null)
            httpClient = createHttpClient();

        httpGet = new HttpGet(url);
        RequestConfig localConfig = RequestConfig.custom()
                .setCookieSpec(StandardCookieSpec.RELAXED)
                .build();
        httpGet.setConfig(localConfig);
        if (headers != null) {
            httpGet.setHeaders(headers);
        }
        if (item.getUserAgent() != null) {
            httpGet.addHeader(HttpHeaders.USER_AGENT, item.getUserAgent());
        }
        try {
            FileUtils.forceMkdir(new File(cachedFile).getParentFile());
        } catch (IOException e) {
            R.info("couldn't make dir file");
        }
    }


    /**
     * @return
     */
    private CloseableHttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClients.custom();
        CookieStore store = null;
        context = HttpClientContext.create();
        if (item.haveCookie()) {
            store = item.getCookieStore();
            if (store != null) {
                builder.setDefaultCookieStore(item.getCookieStore());
                context.setCookieStore(item.getCookieStore());
            }


        }

        if (Setting.GetProxyConfig() == ProxyConfig.ManualProxy
                && Setting.GetProxyType() == ProxyType.SOCKS) {
            // Client HTTP connection objects when fully initialized can be bound to
            // an arbitrary network socket. The process of network socket initialization,
            // its connection to a remote address and binding to a local one is controlled
            // by a connection socket factory.

            // SSL context for secure connections can be created either based on
            // system or application specific properties.
            SSLContext sslcontext = SSLContexts.createSystemDefault();

            // Create a registry of custom connection socket factories for supported
            // protocol schemes.
            class SocksPlainConnectionSocketFactory extends PlainConnectionSocketFactory {

                public SocksPlainConnectionSocketFactory() {
                    super();
                }

                @Override
                public Socket createSocket(final HttpContext context) throws IOException {
                    InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
                    Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
                    return new Socket(proxy);
                }

            }

            class SocksSSLConnectionSocketFactory extends SSLConnectionSocketFactory {
                public SocksSSLConnectionSocketFactory(final SSLContext sslContext) {
                    super(sslContext);
                }
                @Override
                public Socket createSocket(final HttpContext context) throws IOException {
                    InetSocketAddress socketAddress = (InetSocketAddress) context.getAttribute("socks.address");
                    Proxy proxy = new Proxy(Proxy.Type.SOCKS, socketAddress);
                    return new Socket(proxy);
                }

            }
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new SocksPlainConnectionSocketFactory())
                    .register("https", new SocksSSLConnectionSocketFactory(sslcontext))
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
            builder.setConnectionManager(cm);

        } else {
            HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
            builder.setConnectionManager(connMrg);
        }
        builder.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE);

        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(StandardCookieSpec.RELAXED)
                .build();
        builder.setDefaultRequestConfig(globalConfig);
        builder.useSystemProperties();

        //proxy setting gos her
        //proxy setting gos her
        ProxySetting setting = Setting.getProxySetting();
        if (setting.getProxyConfig() != ProxyConfig.NoProxy) {
            if (setting.getProxyConfig() == ProxyConfig.SystemProxy) {
                builder.useSystemProperties();
            } else if (setting.getProxyConfig() == ProxyConfig.ManualProxy) {
                if (ProxyType.SOCKS.equals(setting.proxyType)) {
                    InetSocketAddress socksAddress = new InetSocketAddress(setting.getRemoteAddress(), setting.getRemotePort());
                    context.setAttribute("socks.address", socksAddress);
                } else {
                    URIScheme scheme = ProxyType.HTTPS.equals(setting.proxyType)
                            ? URIScheme.HTTPS
                            : URIScheme.HTTP;
                    HttpHost proxy = new HttpHost(scheme.getId(), setting.getRemoteAddress(), setting.getRemotePort());
                    builder.setProxy(proxy);
                }
            } else if (setting.getProxyConfig() == ProxyConfig.AutoConfigProxy) {
                builder.setProxy(new HttpHost(setting.getUrlAutoConfig()));
            }
        }
        return builder.build();
    }

    @Override
    protected Task<Number> createTask() {
        return new Task<Number>() {
            @Override
            protected Number call() throws Exception {
                if (range != null) {
                    updateValue(range[0] + range[2]);
                    updateProgress(range[2], (range[1] - range[0]));
                    setSize(Utils.fileLengthUnite(range[1] - range[0]));
                    updateMessage(Utils.fileLengthUnite(range[2]));
                    if (range[1] == (range[0] + range[2])) {
                        setStateCode("Done");
                        return 1;
                    } else if (range[1] == (range[0] + range[2] - 1)) {
                        setStateCode("Done");
                        return 1;
                    } else if (range[2] >= range[0] && (range[0] != 0)) {
                        updateProgress((range[0] + range[2]), range[1]);
                        setStateCode("Error");
                        return -1;
                    }
                    /* how to get range for that @param id */
                    String byteRange = range[0] + range[2] + "-";
                    if (range[1] != -1) {
                        byteRange += range[1];
                    }
                    httpGet.addHeader(HttpHeaders.RANGE, "bytes=" + byteRange);
                }
                CloseableHttpResponse response = httpClient.execute(httpGet, context);
                R.info("-> " + response.toString());
                stateCode.set(response.getReasonPhrase());
                setSize(Utils.fileLengthUnite(range[1] - range[0]));
                httpStateCode = response.getCode();
                if (httpStateCode / 100 != 2) {
                    String strLog = "id:" + id.get() + " process canceled \"state code\":" + httpStateCode;
                    R.info(strLog);
                    cancel();
                    return 0;
                }
                updateTitle(response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                if (range == null) {
                    range = new long[3];
                    range[0] = 0; // start
                    range[1] = entity.getContentLength(); // end
                    range[2] = 0; // download
                }
                file = new RandomAccessFile(cachedFile, "rw");
                file.seek(range[0] + range[2]);
                if (entity != null) {

                    InputStream inputStream = entity.getContent();
                    int read = 0;
                    long length = range[1] - range[0];
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1
                            && !isCancelled() && !stop) {
                        file.write(bytes, 0, read);
                        range[2] += read;
                        Platform.runLater(() -> {
                            if (item.isUnknowLength()) {
                                updateProgress(range[2], range[1]);
                                item.downloaded = range[2];
                            } else {
                                updateProgress(range[2], length);
                            }
                            updateValue(range[2]);
                            updateMessage(Utils.fileLengthUnite(range[2]));
                        });
                    }
                    file.close();
                    inputStream.close();

                    if (!isCancelled() && !stop) {
                        setStateCode("Done");
                    }

                } else {
                    R.info("download failed");
                }

                /* how to update the chunk progress */
                // updateProgress(range[2] , range[1]-range[0]); // for now
                updateValue(range[2]);
                stop = true;
                return 1;
            }
        };
    }


    @Override
    public StringProperty stateCodeProperty() {
        return stateCode;
    }

    @Override
    public IntegerProperty idProperty() {
        return id;
    }

    @Override
    public StringProperty sizeProperty() {
        return size;
    }

    @Override
    public ReadOnlyStringProperty doneProperty() {
        return messageProperty();
    }


    public int getIntStateCode() {
        return httpStateCode;
    }

    public long[] getRange() {
        return range;
    }

    public boolean isStop() {
        return stop;
    }


}
