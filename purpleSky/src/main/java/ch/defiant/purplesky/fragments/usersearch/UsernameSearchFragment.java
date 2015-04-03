package ch.defiant.purplesky.fragments.usersearch;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.activities.UserSearchResultsActivity;
import ch.defiant.purplesky.activities.UserSearchTabbedActivity;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.adapters.NullAdapter;
import ch.defiant.purplesky.adapters.UserSearchResultListAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;

public class UsernameSearchFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Holder<List<MinimalUser>>>, UserSearchTabbedActivity.ISearchUserFragment, Callback {

    public static final String TAG = UsernameSearchFragment.class.getSimpleName();
    private static final int MINCHARACTERS = 3;
    private static final String EXTRA_SEARCHSTRING = "username";
    private EditText m_searchField;
    private Handler m_textwatcherHandler;
    private AtomicReference<ListView> m_suggestionList = new AtomicReference<>(null);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_textwatcherHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.usersearch_byname, container, false);

        m_searchField = (EditText) inflated.findViewById(R.id.usersearch_byname_editText);
        m_searchField.addTextChangedListener(new UsernameTextChangeListener());

        m_suggestionList.set((ListView) inflated.findViewById(R.id.usersearch_byname_list));
        m_suggestionList.get().setOnItemClickListener(new UserClickListener());

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
                    return new Holder<>(list);
                } catch (IOException e) {
                    return new Holder<>(e);
                } catch (WrongCredentialsException e) {
                    PersistantModel.getInstance().handleWrongCredentials(getActivity());
                    return new Holder<>(e);
                } catch (PurpleSkyException e) {
                    return new Holder<>(e);
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
                    replaceAdapter(new NullAdapter<>(getActivity(), new NullUser(), R.string.NoResultsFound));
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
        m_suggestionList.get().setAdapter(adapter);
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

    @Override
    public void startSearch(){
        // Called from another thread possibly - must use AtomicReference
        startSearch(m_searchField.getText().toString());
    }

    private void startSearch(String username) {
        Intent intent = new Intent(getActivity(), UserSearchResultsActivity.class);
        intent.putExtra(UserSearchResultsActivity.EXTRA_SEARCHNAME, username);
        getActivity().startActivity(intent);
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
