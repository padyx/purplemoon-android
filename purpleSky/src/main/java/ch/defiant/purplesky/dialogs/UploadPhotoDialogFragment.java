package ch.defiant.purplesky.dialogs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.NullPictureFolder;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.beans.UploadBean;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurplemoonAPIAdapter;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.StringUtility;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class UploadPhotoDialogFragment extends SherlockDialogFragment implements LoaderCallbacks<Holder<List<PictureFolder>>> {

    public static final String ARGUMENT_PICTURE_URI = "picture";
    public static final int INVALID_POSITION = -1;

    private ArrayAdapter<PictureFolder> m_spinnerAdapter;
    private OnDismiss m_onDismissListener;

    private Uri m_imageURI;

    public static final String TAG = UploadPhotoDialogFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null || getArguments().get(ARGUMENT_PICTURE_URI) == null) {
            Toast.makeText(getSherlockActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Missing image for upload");
            dismiss();
        }
        m_imageURI = (Uri) getArguments().get(ARGUMENT_PICTURE_URI);
    }

    @Override
    public void onActivityCreated(Bundle arg) {
        super.onActivityCreated(arg);
        // Initialize loader
        getLoaderManager().initLoader(R.id.loader_uploadpicture_getfolders, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.Upload);
        View inflatedView = inflater.inflate(R.layout.uploadphoto_dialog_fragment, container, false);

        Button btn = (Button) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_uploadBtn);
        btn.setOnClickListener(new UploadClickListener());

        Button btnCancel = (Button) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_cancelBtn);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final ImageView imgV = (ImageView) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_imgView);
        imgV.setImageResource(R.drawable.picture_placeholder);
        final ViewTreeObserver vto = imgV.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int finalHeight = imgV.getMeasuredHeight();
                int finalWidth = imgV.getMeasuredWidth();
                if (finalHeight != 0 && finalWidth != 0) {
                    new PictureLoaderTask().execute(finalHeight, finalWidth);
                    imgV.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });

        // Set placeholder
        Spinner spinner = (Spinner) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);

        List<PictureFolder> list = Collections.<PictureFolder> singletonList(new NullPictureFolder(getString(R.string.Loading_)));
        m_spinnerAdapter = new ArrayAdapter<PictureFolder>(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(m_spinnerAdapter);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Holder<List<PictureFolder>>> onCreateLoader(int arg0, Bundle arg1) {
        return new SimpleAsyncLoader<Holder<List<PictureFolder>>>(getSherlockActivity()) {
            @Override
            public Holder<List<PictureFolder>> loadInBackground() {
                List<PictureFolder> folders = null;
                try {
                    folders = PurplemoonAPIAdapter.getInstance().getMyPictureFolders();
                } catch (Exception e) {
                    return new Holder<List<PictureFolder>>(e);
                }

                return new Holder<List<PictureFolder>>(folders);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<PictureFolder>>> loader, Holder<List<PictureFolder>> result) {
        if (result != null && result.getContainedObject() != null) {
            ArrayList<PictureFolder> all = new ArrayList<PictureFolder>();
            all.add(new NullPictureFolder(getString(R.string.PleaseChoose)));
            all.addAll(result.getContainedObject());

            Spinner spinner = (Spinner) getView().findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);
            m_spinnerAdapter = new ArrayAdapter<PictureFolder>(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, all);
            spinner.setAdapter(m_spinnerAdapter);
        } else {
            Toast.makeText(getSherlockActivity(), getString(R.string.ErrorOccurred_NoNetwork), Toast.LENGTH_LONG).show();
            FragmentTransaction t = getFragmentManager().beginTransaction();
            t.detach(this);
            t.commitAllowingStateLoss();
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<PictureFolder>>> arg0) {
        // showLoadingDialog();
    }

    public OnDismiss getOnDismissListener() {
        return m_onDismissListener;
    }

    public void setDismissListener(OnDismiss dismissListener) {
        m_onDismissListener = dismissListener;
    }

    private class PictureLoaderTask extends AsyncTask<Object, Object, Holder<Bitmap>> {

        @Override
        protected Holder<Bitmap> doInBackground(Object... params) {
            if (params == null || params.length != 2) {
                throw new IllegalArgumentException("No height/width");
            }
            Integer width = (Integer) params[1];
            Integer height = (Integer) params[0];

            Bitmap b = null;
            InputStream stream = null;
            try {
                stream = getSherlockActivity().getContentResolver().openInputStream(m_imageURI);
                // Read image size
                Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                b = BitmapFactory.decodeStream(stream); // Will return null anyway because we set flag above

                stream.close();
                return new Holder<Bitmap>(Bitmap.createScaledBitmap(b, width, height, false));
            } catch (FileNotFoundException e) {
                return new Holder<Bitmap>(e);
            } catch (IOException e) {
                Log.w(TAG, "Could not close stream after reading");
                return new Holder<Bitmap>(e);
            }
        }

        @Override
        protected void onPostExecute(Holder<Bitmap> result) {
            ImageView imageview = (ImageView) getView().findViewById(R.id.uploadphoto_dialog_fragment_imgView);
            if (result != null && result.getContainedObject() != null) {
                // Set the image
                imageview.setImageBitmap(result.getContainedObject());
            } else {
                imageview.setImageResource(R.drawable.no_image);
            }
        }

    }

    private class UploadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Get selected picture folder
            Spinner folderSpinner = (Spinner) getView().findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);
            int position = folderSpinner.getSelectedItemPosition();

            if (position == Spinner.INVALID_POSITION || m_spinnerAdapter.getItem(position) instanceof NullPictureFolder) {
                Toast.makeText(getSherlockActivity(), "Must select a folder", Toast.LENGTH_SHORT).show();
                return;
            }

            PictureFolder folder = m_spinnerAdapter.getItem(position);

            EditText descEditTxt = (EditText) getView().findViewById(R.id.uploadphoto_dialog_fragment_descriptionEditText);
            String desc = descEditTxt.getText().toString();

            final String accessToken = PersistantModel.getInstance().getOAuthAccessToken();
            final URL u;
            try {
                u = new URL(PurplemoonAPIConstantsV1.BASE_URL + PurplemoonAPIConstantsV1.PICTURE_UPLOAD_URL);
                if (StringUtility.isNullOrEmpty(accessToken)) {
                    PersistantModel.getInstance().handleWrongCredentials(getSherlockActivity());
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Malformed url exception on upload", e);
                Toast.makeText(getSherlockActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_LONG).show();
                return;
            }

            final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.AUTH_HEADER_NAME, PurplemoonAPIConstantsV1.AUTH_HEADER_VALUEPREFIX
                    + accessToken));
            final ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PICTURE_POST_FOLDER, folder.getFolderId()));
            list.add(new BasicNameValuePair(PurplemoonAPIConstantsV1.PICTURE_POST_DESCRIPTION, desc));

            UploadBean b = new UploadBean(u, m_imageURI, PurplemoonAPIConstantsV1.PICTURE_POST_PICTURE, list, params);
            if (m_onDismissListener != null) {
                m_onDismissListener.dismissSuccess(b);
            }
        }

    }

    public interface OnDismiss {

        public void dismissSuccess(UploadBean u);

        public void dismissAbort();

    }

}
