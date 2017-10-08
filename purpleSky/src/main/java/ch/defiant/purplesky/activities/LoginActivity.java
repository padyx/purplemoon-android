package ch.defiant.purplesky.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.chatlist.ChatListActivity;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.dialogs.AlertDialogFragment;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.SystemUtility;

public class LoginActivity extends BaseFragmentActivity {

    @Inject
    protected IMessageService messageService;

    private class LoginTask extends AsyncTask<Pair<String, String>, Object, Holder<Boolean>> {
    
        @Override
        protected void onPreExecute() {
            showFragmentDialog(DIALOG_LOGGING_IN, LoginTask.this);
        }
    
        @Override
        protected Holder<Boolean> doInBackground(Pair<String, String>... params) {
            if (params == null || params.length == 0 || params[0] == null) {
                Log.e(TAG, "No parameters to login with");
                return new Holder<>(new PurpleSkyException(LoginActivity.this, R.string.UnknownErrorOccured));
            }
    
            boolean success = false;
            try {
                success = apiAdapter.doLogin(params[0].first, params[0].second);
                return new Holder<>(success);
            } catch (Exception e) {
                return new Holder<>(e);
            }
        }
    
        @Override
        protected void onPostExecute(Holder<Boolean> result) {
            dismissFragmentDialog(DIALOG_LOGGING_IN);
            if (!isCancelled()) {
                if (result == null || result.getException() != null) {
                    // Handle exception
                    if (result == null) {
                        Log.w(TAG, "No login result received");
                        showFragmentDialog(DIALOG_LOGINFAILED_UNKNOWNERROR, null);
                    } else {
                        Exception e = result.getException();
                        if (e instanceof IOException) {
                            showFragmentDialog(DIALOG_LOGINFAILED_NONETWORK, null);
                        } else {
                            Log.w(TAG, "Unknown exception when logging in", e);
                            showFragmentDialog(DIALOG_LOGINFAILED_UNKNOWNERROR, null);
                        }
                    }
                } else {
                    Boolean success = result.getContainedObject();
                    if (CompareUtility.equals(success, true)) {
                        doLoginSuccess();
                    } else if (CompareUtility.equals(success, false)) {
                        showFragmentDialog(DIALOG_LOGINFAILED_CREDENTIALS, null);
                    } else {
                        Log.w(TAG, "Conflicting state when logging in: No exception or success result");
                        showFragmentDialog(DIALOG_LOGINFAILED_UNKNOWNERROR, null);
                    }
                }
            }
        }
    
        @Override
        protected void onCancelled(Holder<Boolean> result) {
            dismissFragmentDialog(DIALOG_LOGGING_IN);
        }
    
    }

    private final class OnLoginClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // Hide keyboard, then log in
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.loginview_loginButton).getWindowToken(), 0);
            initiateLogin();
        }
    }

    private final String TAG = LoginActivity.class.getSimpleName();
    private static final int DIALOG_LOGGING_IN = 0;
    private static final int DIALOG_LOGINFAILED_CREDENTIALS = 1;
    private static final int DIALOG_LOGINFAILED_NONETWORK = 2;
    private static final int DIALOG_LOGINFAILED_UNKNOWNERROR = 3;

    @Override
    protected boolean isShowNavigationDrawer() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PurpleSkyApplication.get().inject(this);

        setContentView(R.layout.loginview);

        View loginButton = findViewById(R.id.loginview_loginButton);
        loginButton.setOnClickListener(new OnLoginClickListener());

        View registerLink = findViewById(R.id.loginview_registerLink);
        registerLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.purplemoon.ch/register"));

                startActivity(intent);
            }
        });

        ((EditText) findViewById(R.id.loginview_passwordField)).setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    initiateLogin();
                    handled = true;
                }
                return handled;
            }
        });

        // Init / Update DB
        DBHelper dbHelper = DBHelper.fromContext(getApplicationContext());
        SQLiteDatabase readOnly = dbHelper.getReadableDatabase();
        readOnly.close();
        readOnly = null;
        // Post upgrade actions
        checkPostUpgrade();
        runPruning();
        checkPlayServices();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setHomeButtonEnabled(false);
        }
    }

    @Override
    public int getSelfNavigationIndex() {
        return -1;
    }

    private void runPruning() {
        messageService.cleanupDB();
    }

    private void checkPostUpgrade() {
        SharedPreferences pref = PreferenceUtility.getPreferences();
        final int none = -1;
        int lastVersion = pref.getInt(PreferenceConstants.lastVersionInt, none);

        int appVersion = SystemUtility.getAppVersion(this);

        if (lastVersion == none) {
            // Newly installed
            // TODO Welcome to App screen
        } else {
            if (appVersion != lastVersion) {
                // TODO New in this version screen
            }
        }

        // Load the default values
        PreferenceManager.setDefaultValues(PurpleSkyApplication.get(), R.xml.preferences, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apiAdapter.isLoggedIn()) {
            // OK, no need to login - go directly to HomeActivity
            doLoginSuccess();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOGGING_IN: {
                return ProgressDialog.show(LoginActivity.this, "Login in progress", "Trying to log you in." + " " + "Please wait...", true);
            }
            default:
                return null;
        }
    }

    /**
     * Starts all application services and goes to the HomeActivity.
     */
    private void doLoginSuccess() {
        startServices();
        Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        // Make sure this activity is not available using 'back'
        finish();
    }

    private void startServices() {
    }

    private synchronized Fragment showFragmentDialog(int dialogId, AsyncTask<?, ?, ?> task) {
        // Don't show if already present
        if (getFragmentManager().findFragmentByTag(String.valueOf(dialogId)) != null) {
            return null;
        }

        DialogFragment f;
        switch (dialogId) {
            case DIALOG_LOGINFAILED_CREDENTIALS: {
                f = AlertDialogFragment.newOKDialog(R.string.loginfailed, R.string.ErrorWrongCredentials, DIALOG_LOGINFAILED_CREDENTIALS);
                break;
            }
            case DIALOG_LOGINFAILED_NONETWORK: {
                f = AlertDialogFragment.newOKDialog(R.string.loginfailed, R.string.ErrorOccurred_NoNetwork, DIALOG_LOGINFAILED_NONETWORK);
                break;
            }
            case DIALOG_LOGGING_IN: {
                ProgressFragmentDialog pdf = new ProgressFragmentDialog();
                pdf.setTitleResource(R.string.login);
                pdf.setMessageResource(R.string.PleaseWait);
                pdf.setAsyncTask(task);
                f = pdf;
                break;
            }
            case DIALOG_LOGINFAILED_UNKNOWNERROR: {
                f = AlertDialogFragment.newOKDialog(R.string.loginfailed, R.string.UnknownErrorOccured, DIALOG_LOGINFAILED_UNKNOWNERROR);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown dialog");
            }
        }
        f.setRetainInstance(true);
        f.show(getFragmentManager(), String.valueOf(dialogId));
        return f;
    }

    private synchronized void dismissFragmentDialog(int dialogId) {
        final FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(String.valueOf(dialogId));
        if (frag instanceof DialogFragment) {
            ((DialogFragment) frag).dismissAllowingStateLoss();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
            } else {
                AlertDialogFragment dialog = AlertDialogFragment.newOKDialog(R.string.PushNotifications,
                        R.string.ErrorPushNotificationsUnavailableExpl, 0);
                dialog.show(getFragmentManager(), "PUSH_NOTIFICATIONS");
                Log.i(TAG, "Push notifications unavailable. Result code " + resultCode);
            }
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initiateLogin() {
        final EditText usernameField = (EditText) findViewById(R.id.loginview_emailField);
        final EditText passwordField = (EditText) findViewById(R.id.loginview_passwordField);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (StringUtility.isNullOrEmpty(username) || StringUtility.isNullOrEmpty(password)) {
            Toast.makeText(LoginActivity.this, R.string.EmailOrPasswordMayNotBeEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        Pair<String, String> pair = new Pair<>(username, password);
        // TODO Convert into loader (?)
        new LoginTask().execute(pair);
    }
}
