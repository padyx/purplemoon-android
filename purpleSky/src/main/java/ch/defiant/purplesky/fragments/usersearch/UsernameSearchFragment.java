package ch.defiant.purplesky.fragments.usersearch;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.adapters.NullAdapter;
import ch.defiant.purplesky.adapters.UserSearchResultListAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.broadcast.LocalBroadcastReceiver;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.interfaces.IBroadcastReceiver;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

public class UsernameSearchFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Holder<List<MinimalUser>>>, IBroadcastReceiver, Callback {

    public static final String TAG = UsernameSearchFragment.class.getSimpleName();
    private static final int MINCHARACTERS = 3;
    private static final String EXTRA_SEARCHSTRING = "username";
    private EditText m_searchField;
    private boolean m_menuVisible;
    private BroadcastReceiver m_broadcastReceiver;
    private Handler m_textwatcherHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        m_textwatcherHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.usersearch_byname, container, false);

        m_searchField = (EditText) inflated.findViewById(R.id.usersearch_byname_editText);
        m_searchField.addTextChangedListener(new UsernameTextChangeListener());

        ((ListView) inflated.findViewById(R.id.usersearch_byname_list)).setOnItemClickListener(new UserClickListener());

        return inflated;
    }

    @Override
    public Loader<Holder<List<MinimalUser>>> onCreateLoader(int loaderId, Bundle arg1) {
        getActivity().setProgressBarIndeterminateVisibility(true);
        final String username = arg1.getString(EXTRA_SEARCHSTRING);
        return new SimpleAsyncLoader<Holder<List<MinimalUser>>>(this.getActivity()) {

            @Override
            public Holder<List<MinimalUser>> loadInBackground() {
                UserSearchOptions options = new UserSearchOptions();
                options.setNumber(11);
                options.setUserClass(MinimalUser.class);

                try {
                    List<MinimalUser> list = apiAdapter.searchUserByName(username, options);
                    return new Holder<List<MinimalUser>>(list);
                } catch (IOException e) {
                    return new Holder<List<MinimalUser>>(e);
                } catch (WrongCredentialsException e) {
                    PersistantModel.getInstance().handleWrongCredentials(getActivity());
                    return new Holder<List<MinimalUser>>(e);
                } catch (PurpleSkyException e) {
                    return new Holder<List<MinimalUser>>(e);
                }
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Holder<List<MinimalUser>>> arg0, Holder<List<MinimalUser>> holder) {
        if (getView() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            if (holder.getException() != null) {
                // OOPS. Error
                Exception e = holder.getException();
                if (e instanceof WrongCredentialsException) {
                    PersistantModel.getInstance().handleWrongCredentials(getActivity());
                } else if (e instanceof PurpleSkyException) {
                    Log.w(TAG, "Unknown exception occurred at searching by name", e);
                }
                replaceAdapter(new ErrorAdapter(getActivity()));
            } else {
                List<MinimalUser> result = holder.getContainedObject();
                if(result.isEmpty()){
                    replaceAdapter(new NullAdapter<NullUser>(getActivity(), new NullUser(), R.string.NoResultsFound));
                } else {
                    replaceAdapter(new UserSearchResultListAdapter(getActivity(), result));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<MinimalUser>>> loader) {
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    private class UsernameTextChangeListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            m_textwatcherHandler.removeMessages(0);
            if (s.length() >= MINCHARACTERS) {
                m_textwatcherHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }

    }

    private void replaceAdapter(BaseAdapter adapter) {
        ListView t = (ListView) getView().findViewById(R.id.usersearch_byname_list);
        t.setAdapter(adapter);
    }

    private class UserClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {

            Object item = adapter.getItemAtPosition(position);
            if (item == null) {
                return;
            }
            MinimalUser user = (MinimalUser) item;
            if (user.getUserId() != null) {
                Intent intent = new Intent(getActivity(), DisplayProfileActivity.class);
                intent.putExtra(ArgumentConstants.ARG_USERID, user.getUserId());

                getActivity().startActivity(intent);
            }
        }

    }

    // @Override
    // public void onResume() {
    // super.onResume();
    // final View view = getView().findViewById(R.id.usersearch_byname_editText);
    // // Do this delayed - animation of tab switching otherwise choppy
    // new Handler().postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // view.requestFocus();
    // InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    // ime.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    // }
    //
    // }, 100);
    // }
    //
    // @Override
    // public void onPause() {
    // super.onPause();
    // final View findViewById = getView().findViewById(R.id.usersearch_byname_editText);
    // // Do this delayed - animation of tab switching otherwise choppy
    // new Handler().postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    // ime.hideSoftInputFromWindow(findViewById.getWindowToken(), 0);
    // }
    //
    // }, 100);
    // }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        m_menuVisible = menuVisible;
    }

    @Override
    public void onResume() {
        super.onResume();
        m_broadcastReceiver = new LocalBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SEARCH);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(m_broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(m_broadcastReceiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (m_menuVisible) {
            final String s = ((TextView) getView().findViewById(R.id.usersearch_byname_editText)).getText().toString();

            UserSearchResultsFragment f = new UserSearchResultsFragment();
            Bundle b = new Bundle();
            b.putString(UserSearchResultsFragment.EXTRA_SEARCHNAME, s);
            f.setArguments(b);

            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(getActivity() != null){
            Bundle b = new Bundle();
            b.putString(EXTRA_SEARCHSTRING, m_searchField.getText().toString());
            getLoaderManager().restartLoader(R.id.loader_usernamesearch_main, b, UsernameSearchFragment.this);
        } else {
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Dropped textwatcher message because we are no longer attached.");
            }
        }
        return true;
    }

}
