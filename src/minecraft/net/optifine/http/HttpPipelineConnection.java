package net.optifine.http;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.src.Config;

public class HttpPipelineConnection {
    private String host;
    private int port;
    private Proxy proxy;
    private final List<HttpPipelineRequest> listRequests;
    private final List<HttpPipelineRequest> listRequestsSend;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HttpPipelineSender httpPipelineSender;
    private HttpPipelineReceiver httpPipelineReceiver;
    private int countRequests;
    private boolean responseReceived;
    private long keepaliveTimeoutMs;
    private int keepaliveMaxCount;
    private long timeLastActivityMs;
    private boolean terminated;
    private static final String LF = "\n";
    public static final int TIMEOUT_CONNECT_MS = 5000;
    public static final int TIMEOUT_READ_MS = 5000;
    private static final Pattern patternFullUrl = Pattern.compile("^[a-zA-Z]+://.*");

    public HttpPipelineConnection(final String host, final int port) {
        this(host, port, Proxy.NO_PROXY);
    }

    public HttpPipelineConnection(final String host, final int port, final Proxy proxy) {
        this.host = null;
        this.port = 0;
        this.proxy = Proxy.NO_PROXY;
        this.listRequests = new LinkedList();
        this.listRequestsSend = new LinkedList();
        this.socket = null;
        this.inputStream = null;
        this.outputStream = null;
        this.httpPipelineSender = null;
        this.httpPipelineReceiver = null;
        this.countRequests = 0;
        this.responseReceived = false;
        this.keepaliveTimeoutMs = 5000L;
        this.keepaliveMaxCount = 1000;
        this.timeLastActivityMs = System.currentTimeMillis();
        this.terminated = false;
        this.host = host;
        this.port = port;
        this.proxy = proxy;
        this.httpPipelineSender = new HttpPipelineSender(this);
        this.httpPipelineSender.start();
        this.httpPipelineReceiver = new HttpPipelineReceiver(this);
        this.httpPipelineReceiver.start();
    }

    public synchronized boolean addRequest(final HttpPipelineRequest pr) {
        if (this.isClosed()) {
            return false;
        } else {
            this.addRequest(pr, this.listRequests);
            this.addRequest(pr, this.listRequestsSend);
            ++this.countRequests;
            return true;
        }
    }

    private void addRequest(final HttpPipelineRequest pr, final List<HttpPipelineRequest> list) {
        list.add(pr);
        this.notifyAll();
    }

    public synchronized void setSocket(final Socket s) throws IOException {
        if (!this.terminated) {
            if (this.socket != null) {
                throw new IllegalArgumentException("Already connected");
            } else {
                this.socket = s;
                this.socket.setTcpNoDelay(true);
                this.inputStream = this.socket.getInputStream();
                this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
                this.onActivity();
                this.notifyAll();
            }
        }
    }

    public synchronized OutputStream getOutputStream() throws IOException, InterruptedException {
        while (this.outputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }

        return this.outputStream;
    }

    public synchronized InputStream getInputStream() throws IOException, InterruptedException {
        while (this.inputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }

        return this.inputStream;
    }

    public synchronized HttpPipelineRequest getNextRequestSend() throws InterruptedException, IOException {
        if (this.listRequestsSend.size() <= 0 && this.outputStream != null) {
            this.outputStream.flush();
        }

        return this.getNextRequest(this.listRequestsSend, true);
    }

    public synchronized HttpPipelineRequest getNextRequestReceive() throws InterruptedException {
        return this.getNextRequest(this.listRequests, false);
    }

    private HttpPipelineRequest getNextRequest(final List<HttpPipelineRequest> list, final boolean remove) throws InterruptedException {
        while (list.size() <= 0) {
            this.checkTimeout();
            this.wait(1000L);
        }

        this.onActivity();

        if (remove) {
            return list.remove(0);
        } else {
            return list.get(0);
        }
    }

    private void checkTimeout() {
        if (this.socket != null) {
            long i = this.keepaliveTimeoutMs;

            if (this.listRequests.size() > 0) {
                i = 5000L;
            }

            final long j = System.currentTimeMillis();

            if (j > this.timeLastActivityMs + i) {
                this.terminate(new InterruptedException("Timeout " + i));
            }
        }
    }

    private void onActivity() {
        this.timeLastActivityMs = System.currentTimeMillis();
    }

    public synchronized void onRequestSent(final HttpPipelineRequest pr) {
        if (!this.terminated) {
            this.onActivity();
        }
    }

    public synchronized void onResponseReceived(final HttpPipelineRequest pr, final HttpResponse resp) {
        if (!this.terminated) {
            this.responseReceived = true;
            this.onActivity();

            if (this.listRequests.size() > 0 && this.listRequests.get(0) == pr) {
                this.listRequests.remove(0);
                pr.setClosed(true);
                String s = resp.getHeader("Location");

                if (resp.getStatus() / 100 == 3 && s != null && pr.getHttpRequest().getRedirects() < 5) {
                    try {
                        s = this.normalizeUrl(s, pr.getHttpRequest());
                        final HttpRequest httprequest = HttpPipeline.makeRequest(s, pr.getHttpRequest().getProxy());
                        httprequest.setRedirects(pr.getHttpRequest().getRedirects() + 1);
                        final HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(httprequest, pr.getHttpListener());
                        HttpPipeline.addRequest(httppipelinerequest);
                    } catch (final IOException ioexception) {
                        pr.getHttpListener().failed(pr.getHttpRequest(), ioexception);
                    }
                } else {
                    final HttpListener httplistener = pr.getHttpListener();
                    httplistener.finished(pr.getHttpRequest(), resp);
                }

                this.checkResponseHeader(resp);
            } else {
                throw new IllegalArgumentException("Response out of order: " + pr);
            }
        }
    }

    private String normalizeUrl(final String url, final HttpRequest hr) {
        if (patternFullUrl.matcher(url).matches()) {
            return url;
        } else if (url.startsWith("//")) {
            return "http:" + url;
        } else {
            String s = hr.getHost();

            if (hr.getPort() != 80) {
                s = s + ":" + hr.getPort();
            }

            if (url.startsWith("/")) {
                return "http://" + s + url;
            } else {
                final String s1 = hr.getFile();
                final int i = s1.lastIndexOf("/");
                return i >= 0 ? "http://" + s + s1.substring(0, i + 1) + url : "http://" + s + "/" + url;
            }
        }
    }

    private void checkResponseHeader(final HttpResponse resp) {
        final String s = resp.getHeader("Connection");

        if (s != null && !s.equalsIgnoreCase("keep-alive")) {
            this.terminate(new EOFException("Connection not keep-alive"));
        }

        final String s1 = resp.getHeader("Keep-Alive");

        if (s1 != null) {
            final String[] astring = Config.tokenize(s1, ",;");

            for (int i = 0; i < astring.length; ++i) {
                final String s2 = astring[i];
                final String[] astring1 = this.split(s2, '=');

                if (astring1.length >= 2) {
                    if (astring1[0].equals("timeout")) {
                        final int j = Config.parseInt(astring1[1], -1);

                        if (j > 0) {
                            this.keepaliveTimeoutMs = j * 1000;
                        }
                    }

                    if (astring1[0].equals("max")) {
                        final int k = Config.parseInt(astring1[1], -1);

                        if (k > 0) {
                            this.keepaliveMaxCount = k;
                        }
                    }
                }
            }
        }
    }

    private String[] split(final String str, final char separator) {
        final int i = str.indexOf(separator);

        if (i < 0) {
            return new String[]{str};
        } else {
            final String s = str.substring(0, i);
            final String s1 = str.substring(i + 1);
            return new String[]{s, s1};
        }
    }

    public synchronized void onExceptionSend(final HttpPipelineRequest pr, final Exception e) {
        this.terminate(e);
    }

    public synchronized void onExceptionReceive(final HttpPipelineRequest pr, final Exception e) {
        this.terminate(e);
    }

    private synchronized void terminate(final Exception e) {
        if (!this.terminated) {
            this.terminated = true;
            this.terminateRequests(e);

            if (this.httpPipelineSender != null) {
                this.httpPipelineSender.interrupt();
            }

            if (this.httpPipelineReceiver != null) {
                this.httpPipelineReceiver.interrupt();
            }

            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (final IOException var3) {
            }

            this.socket = null;
            this.inputStream = null;
            this.outputStream = null;
        }
    }

    private void terminateRequests(final Exception e) {
        if (this.listRequests.size() > 0) {
            if (!this.responseReceived) {
                final HttpPipelineRequest httppipelinerequest = this.listRequests.remove(0);
                httppipelinerequest.getHttpListener().failed(httppipelinerequest.getHttpRequest(), e);
                httppipelinerequest.setClosed(true);
            }

            while (this.listRequests.size() > 0) {
                final HttpPipelineRequest httppipelinerequest1 = this.listRequests.remove(0);
                HttpPipeline.addRequest(httppipelinerequest1);
            }
        }
    }

    public synchronized boolean isClosed() {
        return this.terminated || this.countRequests >= this.keepaliveMaxCount;
    }

    public int getCountRequests() {
        return this.countRequests;
    }

    public synchronized boolean hasActiveRequests() {
        return this.listRequests.size() > 0;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}
