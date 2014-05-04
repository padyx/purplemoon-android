package ch.defiant.purplesky.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UploadBean;
import ch.defiant.purplesky.beans.UploadBean.State;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.dialogs.UploadPhotoDialogFragment;
import ch.defiant.purplesky.dialogs.UploadPhotoDialogFragment.OnDismiss;
import ch.defiant.purplesky.services.BinderServiceWrapper;
import ch.defiant.purplesky.services.UploadService;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MultiUploadFragment extends SherlockFragment {

    private static final String FRAGMENTTAG_PICTURE_UPLOADER = "fragment_picture_uploader";
    private static final int REQUESTCODE_CHOOSEIMAGE = 0;
    private static final String TAG = MultiUploadFragment.class.getSimpleName();

    private AtomicReference<UploadService> m_service = new AtomicReference<UploadService>();
    private PendingAdapter m_adapter;
    private ScheduledExecutorService m_executor;

    private final ServiceConnection m_connection = new ServiceConnection() {
        @Override
        @SuppressWarnings("unchecked")
        public void onServiceConnected(ComponentName className, IBinder binder) {
            m_service.set(((BinderServiceWrapper<UploadService>) binder).getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            m_service = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.multiupload, container, false);

        m_adapter = new PendingAdapter(Collections.<UploadBean> emptyList());
        ((ListView) v.findViewById(R.id.multiupload_uploadsList)).setAdapter(m_adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(PurpleSkyApplication.getContext(), UploadService.class);
        PurpleSkyApplication.getContext().startService(intent);
        boolean bindService = PurpleSkyApplication.getContext().bindService(intent, m_connection, Context.BIND_AUTO_CREATE);

        if (!bindService) {
            throw new IllegalStateException("Cannot bind service!");
        }

        m_executor = Executors.newSingleThreadScheduledExecutor();
        m_executor.scheduleAtFixedRate(new UpdateRunnable(), 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        m_executor.shutdown();
        try {
            m_executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Runnable did not shutdown in time");
            }
        }
        m_executor = null;
        PurpleSkyApplication.getContext().unbindService(m_connection);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.multiupload_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.multiupload_menu_add: {
                startChoose();
                return true;
            }
            default:
                return false;
        }
    }

    private class PendingAdapter extends BaseAdapter {

        private List<UploadBean> m_list;
        private int m_size;

        public PendingAdapter(List<UploadBean> list) {
            super();
            setData(list);
        }

        public void setData(List<UploadBean> list) {
            m_list = list;
            if (m_list == null) {
                m_size = 0;
            } else {
                m_size = m_list.size();
            }
            if (getSherlockActivity() != null) {
                getSherlockActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getCount() {
            if (m_list == null || m_list.isEmpty()) {
                return 1;
            } else {
                return m_size;
            }
        }

        @Override
        public Object getItem(int position) {
            if (m_list == null || m_list.isEmpty()) {
                return null;
            } else {
                return m_list.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
            if (m_list == null || m_list.isEmpty()) {
                View inflated = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                ((TextView) inflated.findViewById(android.R.id.text1)).setText(R.string.NoPendingRecentlyCompletedUploads);
                return inflated;
            } else {
                final UploadBean b = m_list.get(position);
                final int progress = b.getProgressPercentage();

                View inflated = inflater.inflate(R.layout.multiupload_item, parent, false);
                ((TextView) inflated.findViewById(R.id.multiupload_item_title)).setText(b.getFileUri().getLastPathSegment());
                TextView stateTxt = (TextView) inflated.findViewById(R.id.multiupload_item_stateText);

                ProgressBar progressBar = (ProgressBar) inflated.findViewById(R.id.multiupload_item_progressBar);
                progressBar.setMax(100);

                State state = b.getState();
                switch (state) {
                    case COMPLETE:
                        stateTxt.setText(R.string.UploadStatusComplete);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case ERROR:
                        stateTxt.setText(R.string.UploadStatusError);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case IN_PROGRESS:
                        stateTxt.setText(R.string.UploadStatusUploading);
                        progressBar.setProgress(progress);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case PENDING:
                        stateTxt.setText(R.string.UploadStatusWaiting);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
                return inflated;
            }
        }

    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (m_service) {
                try {
                    if (m_service.get() == null) {
                        return;
                    }
                    List<UploadBean> pendingUploads = m_service.get().getPendingUploads();
                    Set<UploadBean> completedUploads = m_service.get().getCompletedUploads();

                    ArrayList<UploadBean> list = new ArrayList<UploadBean>();
                    list.addAll(pendingUploads);
                    list.addAll(completedUploads);
                    m_adapter.setData(list);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Update runnable encountered error", e);
                    }
                }
            }
        }

    }

    private void startChoose() {
        Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getImageIntent.setType("image/*");
        startActivityForResult(getImageIntent, REQUESTCODE_CHOOSEIMAGE);
    }

    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        switch (request) {
            case REQUESTCODE_CHOOSEIMAGE:
                handleResultImageChoose(result, intent);
                break;
            default:
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Unknown request code in onActivityResult: " + request);
        }
    }

    private void handleResultImageChoose(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            final Uri selectedImage = intent.getData();
            final FragmentManager fm = getSherlockActivity().getSupportFragmentManager();

            // Wrap this as a workaround. See bug
            new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... params) {
                    return null;
                }

                @Override
                protected void onPostExecute(Object result) {
                    final UploadPhotoDialogFragment fragment = new UploadPhotoDialogFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(UploadPhotoDialogFragment.ARGUMENT_PICTURE_URI, selectedImage);
                    fragment.setArguments(args);
                    fragment.setDismissListener(new OnDismiss() {

                        @Override
                        public void dismissSuccess(UploadBean u) {
                            enqueue(u);
                            fragment.dismissAllowingStateLoss();
                        }

                        @Override
                        public void dismissAbort() {
                            fragment.dismissAllowingStateLoss();
                        }
                    });
                    fragment.show(fm, FRAGMENTTAG_PICTURE_UPLOADER);
                };
            }.execute();

        }
    }

    private void enqueue(UploadBean b) {
        synchronized (m_service) {
            UploadService service = m_service.get();
            if (service != null) {
                service.enqueue(b);
            }
        }
    }
}
