package mil.nga.mapcache.io.network;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.Observable;
import java.util.Observer;

/**
 * Class that is called from within the web view page.
 */
public class WebViewContentRetriever implements Observer, ValueCallback<String> {

    /**
     * The javascript that returns the urls html content.
     */
    private static final String theJavaScript = "(function() { return "
            + "(document.getElementsByTagName('html')[0].innerText); })();";

    /**
     * Contains the current url and html.
     */
    private final WebViewRequestModel model;

    /**
     * The web view being used to load urls.
     */
    private final WebView webView;

    /**
     * Constructor.
     *
     * @param model Contains the current url and html.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public WebViewContentRetriever(WebView webView, WebViewRequestModel model) {
        this.webView = webView;
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.model = model;
        this.model.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (WebViewRequestModel.CURRENT_URL_PROP.equals(o)) {
            this.webView.evaluateJavascript(theJavaScript, this);
        }
    }

    /**
     * Stops getting the html for urls.
     */
    public void close() {
        this.model.deleteObserver(this);
    }

    @Override
    public void onReceiveValue(String html) {
        Log.d(WebViewRequest.class.getSimpleName(), "Html: " + html);
    }
}
