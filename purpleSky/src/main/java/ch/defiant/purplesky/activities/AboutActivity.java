package ch.defiant.purplesky.activities;

import android.os.Bundle;
import android.webkit.WebView;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;

/**
 * @author Patrick Bänziger
 */
public class AboutActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_full);
        setActionBarTitle(getString(R.string.PreferenceAboutApp), null);

        ((WebView)findViewById(R.id.webview_full_webview)).loadUrl("file:///android_asset/licence.html");
    }

    @Override
    public int getSelfNavigationIndex() {
        return -1;
    }

}
