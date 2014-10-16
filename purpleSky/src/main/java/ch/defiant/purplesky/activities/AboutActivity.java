package ch.defiant.purplesky.activities;

import android.os.Bundle;
import android.webkit.WebView;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

/**
 * @author Chakotay
 */
public class AboutActivity extends BaseFragmentActivity {
    // TODO This is a workaround until we can use a fragment for preferences to show the about info in a fragment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_full);

        ((WebView)findViewById(R.id.webview_full_webview)).loadUrl("file:///android_asset/licence.html");
    }

    @Override
    public int getSelfNavigationIndex() {
        return -1;
    }

}
