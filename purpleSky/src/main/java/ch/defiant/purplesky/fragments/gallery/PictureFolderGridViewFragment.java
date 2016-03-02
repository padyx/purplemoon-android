package ch.defiant.purplesky.fragments.gallery;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.PictureGridViewActivity;
import ch.defiant.purplesky.api.gallery.EnterPasswordResponse;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.beans.Picture;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.dialogs.EnterPasswordDialogFragment;
import ch.defiant.purplesky.enums.UserPictureSize;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.fragments.common.IndeterminateProgressTaskFragment;
import ch.defiant.purplesky.fragments.common.TaskFragment;
import ch.defiant.purplesky.loaders.EnterPasswordResponseComposite;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.PictureUrlUtility;

public class PictureFolderGridViewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<List<PictureFolder>>>, EnterPasswordDialogFragment.PasswordResult {

    private static final String PASSWORD_CHECK_FRAGMENT_TAG = "PASSWORD_CHECK_PROGRESS";
    private static final String PASSWORD_ENTER_FRAMENT_TAG = "PASSWORD_ENTER";
    private static final String STATE_CHOSENFOLDER = "STATE_CHOSENFOLDER";

    private static final String TAG = PictureFolderGridViewFragment.class.getSimpleName();

    @Inject
    protected IGalleryAdapter galleryAdapter;
    private ImageAdapter m_adapter;
    private PictureFolder clickedFolder;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_CHOSENFOLDER, clickedFolder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            clickedFolder = (PictureFolder) savedInstanceState.getSerializable(STATE_CHOSENFOLDER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reattachCheckDialog();
    }

    private void reattachCheckDialog() {
        @SuppressWarnings("unchecked")
        TaskFragment<EnterPasswordResponseComposite> fragment = (TaskFragment<EnterPasswordResponseComposite>) getFragmentManager().findFragmentByTag(PASSWORD_CHECK_FRAGMENT_TAG);
        if(fragment != null){
            fragment.setListener(new PasswordCheckingListener());
            if(fragment.isTaskFinished()){
                getFragmentManager().beginTransaction().remove(fragment).commit();
                try {
                    onPasswordCheckComplete(Holder.of(fragment.getResult()));
                } catch (Exception e) {
                    onPasswordCheckComplete(new Holder<EnterPasswordResponseComposite>(e));
                }
                // Remove listener again
                fragment.setListener(null);
            }
        }
    }

    private void handlePasswordCheckComplete(TaskFragment<EnterPasswordResponseComposite> fragment){
        if(getActivity().isFinishing()){
            return;
        }
        fragment.setListener(null);
        getFragmentManager().beginTransaction().detach(fragment).commit();
        try {
            onPasswordCheckComplete(Holder.of(fragment.getResult()));
        } catch (Exception e) {
            onPasswordCheckComplete(new Holder<EnterPasswordResponseComposite>(e));
        }
    }

    private class PasswordCheckingListener implements TaskFragment.TaskListener<EnterPasswordResponseComposite> {
        @Override
        public void taskFinished(TaskFragment<EnterPasswordResponseComposite> taskFragment, boolean isException) {
            handlePasswordCheckComplete(taskFragment);
        }
    }

    private void startCheckDialog(final IGalleryAdapter galleryAdapter, final String userId, final String folder, final String password){
        IndeterminateProgressTaskFragment<EnterPasswordResponseComposite> taskFragment = new IndeterminateProgressTaskFragment<>();
        taskFragment.setListener(new PasswordCheckingListener());
        taskFragment.setProgressTextResource(R.string.CheckingPassword);
        getFragmentManager().beginTransaction().add(taskFragment, PASSWORD_CHECK_FRAGMENT_TAG).commit();
        taskFragment.execute(new Callable<EnterPasswordResponseComposite>() {
            @Override
            public EnterPasswordResponseComposite call() throws Exception {
                EnterPasswordResponse response = galleryAdapter.enterPassword(userId, folder, password);
                PictureFolder pictureFolder = null;
                if(response == EnterPasswordResponse.OK){
                    List<PictureFolder> folders = galleryAdapter.getFoldersWithPictures(userId, Collections.singletonList(folder));
                    pictureFolder = CollectionUtil.firstElement(folders);
                }
                return new EnterPasswordResponseComposite(response, pictureFolder);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.grid_layout, container, false);

        GridView gv = (GridView) v.findViewById(R.id.grid_view);
        m_adapter = new ImageAdapter(Collections.<PictureFolder> emptyList());
        gv.setAdapter(m_adapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < m_adapter.m_size) {
                    PictureFolder value = m_adapter.getData().get(position);

                    if (!value.isAccessGranted() && value.isPasswordProtected()) {
                        clickedFolder = value;
                        startEnterPassword();
                        return;
                    }

                    openFolder(value);
                }
            }
        });

        return v;
    }

    private void openFolder(PictureFolder value) {
        Intent intent = new Intent(getActivity(), PictureGridViewActivity.class);
        intent.putExtra(ArgumentConstants.ARG_FOLDER, value);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(R.id.loader_picturefolders_main, getActivity().getIntent().getExtras(), this);
    }

    @Override
    public void onResult(final String password) {
        final String userid = getArguments().getString(ArgumentConstants.ARG_USERID);
        startCheckDialog(galleryAdapter, userid, clickedFolder.getFolderId(), password);
    }

    public void onPasswordCheckComplete(final Holder<EnterPasswordResponseComposite> result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result.isObject()) {
                    int errorString = 0;
                    switch (result.getContainedObject().getResponse()) {
                        case OK:
                            PictureFolder folder = result.getContainedObject().getFolder();
                            // FIXME may be null
                            if(folder != null) {
                                updateAdapter(folder);
                                openFolder(folder);
                            }
                            break;
                        case ERROR:
                            errorString = R.string.ErrorGeneric;
                            break;
                        case WRONG_PASSWORD:
                            errorString = R.string.WrongPassword;
                            break;
                        case FOLDER_UNAVAILABLE:
                            errorString = R.string.ErrorFolderNotFound;
                            break;
                        case PROFILE_NOT_FOUND:
                            errorString = R.string.ErrorCouldNotFindUser;
                            break;
                        case TOO_MANY:
                            errorString = R.string.TooManyWrongAttempts;
                            break;
                    }
                    if (errorString != 0){
                        Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (result.getException() instanceof IOException) {
                        Toast.makeText(getActivity(), R.string.ErrorNoNetworkGenericShort, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Unknown error occurred when entering password", result.getException());
                    }
                }
            }
        });
    }

    private void updateAdapter(@NonNull PictureFolder clickedFolder) {
        List<PictureFolder> data = m_adapter.getData();
        if(data != null){
            int idx = -1;
            for(PictureFolder p : data){
                idx++;
                if(CompareUtility.equals(p.getFolderId(), clickedFolder.getFolderId())){
                    m_adapter.replace(idx, clickedFolder);
                    break;
                }
            }
        }
    }

    public class ViewHolder {
        TextView lblTextView;
        TextView countTextView;
        ImageView imgV;
        ImageView lockImgV;
    }

    private class ImageAdapter extends BaseAdapter {

        private List<PictureFolder> m_data;
        private int m_size;

        public ImageAdapter(List<PictureFolder> folder) {
            setData(folder);
        }

        private void setData(List<PictureFolder> folder) {
            m_data = folder;
            m_size = folder.size();
            notifyDataSetChanged();
        }

        public List<PictureFolder> getData() {
            return Collections.unmodifiableList(m_data);
        }

        public void replace(int index, PictureFolder folder ){
            m_data.set(index, folder);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return m_size;
        }

        @Override
        public Object getItem(int position) {
            return m_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0; // All of same type
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PictureFolder pictureFolder = m_data.get(position);

            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = LayoutInflater.from(getActivity());
                v = vi.inflate(R.layout.picturefoldergrid_item, parent, false);
                holder = new ViewHolder();
                holder.lblTextView = (TextView) v.findViewById(R.id.picturefoldergrid_item_textView);
                holder.countTextView = (TextView) v.findViewById(R.id.picturefoldergrid_item_count);
                holder.imgV = (ImageView) v.findViewById(R.id.picturefoldergrid_item_imageView);
                holder.lockImgV = (ImageView) v.findViewById(R.id.picturefoldergrid_item_lockImgV);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.imgV.setTag(null);

            // Set the text
            holder.lblTextView.setText(pictureFolder.getName());
            holder.countTextView.setText(String.valueOf(pictureFolder.getDeclaredPictureCount()));

            // Set the password protected drawable
            holder.lockImgV.setVisibility(pictureFolder.isPasswordProtected() ? View.VISIBLE : View.INVISIBLE);

            // Load image
            // Get first image
            if (pictureFolder.getPictureCount() > 0) {
                Picture picture = pictureFolder.getPictures().get(0);
                 
                final int px = LayoutUtility.dpToPx(getResources(), 96);
                UserPictureSize size = UserPictureSize.getPictureSizeForPx(px);
                String url = PictureUrlUtility.getPictureUrl(picture.getUrl(), size);
                
                holder.imgV.setTag(url);
                Picasso.with(getActivity()).load(url).placeholder(R.drawable.picture_placeholder).
                        error(R.drawable.no_image).resize(px, px).centerCrop().into(holder.imgV);
            } else {
                holder.imgV.setImageResource(R.drawable.black);
            }

            return v;
        }
    }

    @Override
    public SimpleAsyncLoader<Holder<List<PictureFolder>>> onCreateLoader(int arg0, Bundle arg1) {
        final String userid = arg1.getString(ArgumentConstants.ARG_USERID);

        return new SimpleAsyncLoader<Holder<List<PictureFolder>>>(getActivity()) {

            @Override
            public Holder<List<PictureFolder>> loadInBackground() {
                try {
                    return new Holder<>(galleryAdapter.getFoldersWithPictures(userid, null));
                } catch (IOException e) {
                    return new Holder<>(e);
                } catch (PurpleSkyException e) {
                    return new Holder<>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<PictureFolder>>> arg0, Holder<List<PictureFolder>> arg1) {
        if (arg1 == null || arg1.getException() != null) {
            // TODO handle errors
            return;
        }
        m_adapter.setData(arg1.getContainedObject());
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<PictureFolder>>> arg0) { }

    private void startEnterPassword() {
        EnterPasswordDialogFragment f = new EnterPasswordDialogFragment();
        f.setTargetFragment(this, 0);
        f.setRetainInstance(true); // Must retain it so that the loader can find it again
        f.show(getFragmentManager(), PASSWORD_ENTER_FRAMENT_TAG);
    }

}
