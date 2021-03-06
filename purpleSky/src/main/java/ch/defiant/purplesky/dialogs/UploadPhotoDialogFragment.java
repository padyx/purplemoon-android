package ch.defiant.purplesky.dialogs;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.gallery.IGalleryAdapter;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.NullPictureFolder;
import ch.defiant.purplesky.beans.PictureFolder;
import ch.defiant.purplesky.beans.UploadBean;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.fragments.BaseDialogFragment;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.ImageUtility;
import ch.defiant.purplesky.util.StringUtility;

public class UploadPhotoDialogFragment extends BaseDialogFragment implements LoaderManager.LoaderCallbacks<Holder<List<PictureFolder>>> {

    public static final String ARGUMENT_PICTURE_URI = "picture";

    @Inject
    protected IGalleryAdapter galleryAdapter;

    private ArrayAdapter<PictureFolder> m_spinnerAdapter;
    private OnDismiss m_onDismissListener;

    private Uri m_imageURI;

    public static final String TAG = UploadPhotoDialogFragment.class.getSimpleName();
    private ImageView m_imgV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null || getArguments().get(ARGUMENT_PICTURE_URI) == null) {
            Toast.makeText(getActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_SHORT).show();
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

        m_imgV = (ImageView) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_imgView);
        m_imgV.setImageResource(R.drawable.picture_placeholder);
        final ViewTreeObserver vto = m_imgV.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int finalHeight = m_imgV.getMeasuredHeight();
                int finalWidth = m_imgV.getMeasuredWidth();
                if (finalHeight != 0 && finalWidth != 0) {
                    new PictureLoaderTask().execute(finalHeight, finalWidth);
                    m_imgV.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });

        // Set placeholder
        Spinner spinner = (Spinner) inflatedView.findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);

        List<PictureFolder> list = Collections.<PictureFolder> singletonList(new NullPictureFolder(getString(R.string.Loading_)));
        m_spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(m_spinnerAdapter);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Holder<List<PictureFolder>>> onCreateLoader(int arg0, Bundle arg1) {
        return new SimpleAsyncLoader<Holder<List<PictureFolder>>>(getActivity()) {
            @Override
            public Holder<List<PictureFolder>> loadInBackground() {
                List<PictureFolder> folders = null;
                try {
                    folders = galleryAdapter.getMyPictureFolders();
                } catch (Exception e) {
                    return new Holder<>(e);
                }

                return new Holder<>(folders);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<PictureFolder>>> loader, Holder<List<PictureFolder>> result) {
        if (result != null && result.getContainedObject() != null) {
            ArrayList<PictureFolder> all = new ArrayList<>();
            all.add(new NullPictureFolder(getString(R.string.PleaseChoose)));
            all.addAll(result.getContainedObject());
            if(getView() != null) {
                Spinner spinner = (Spinner) getView().findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);
                m_spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, all);
                spinner.setAdapter(m_spinnerAdapter);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.ErrorOccurred_NoNetwork), Toast.LENGTH_LONG).show();
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
            Integer height = (Integer) params[0];
            Integer width = (Integer) params[1];

            Bitmap b = null;
            InputStream stream = null;
            try {
                stream = getActivity().getContentResolver().openInputStream(m_imageURI);
                // Read image size
                Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(stream, null, options); // Will return null anyway because we set flag above
                stream.close();
                options.inJustDecodeBounds = false;
                options.inSampleSize = ImageUtility.calculateInsampleSize(options, height, width);
                stream = getActivity().getContentResolver().openInputStream(m_imageURI);
                return new Holder<>(BitmapFactory.decodeStream(stream, null, options));
            } catch (FileNotFoundException e) {
                return new Holder<>(e);
            } catch (IOException e) {
                Log.w(TAG, "Could not close stream after reading");
                return new Holder<>(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // NOP
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Holder<Bitmap> result) {
            if(getActivity() != null) {
                if (result != null && result.getContainedObject() != null) {
                    // Set the image
                    m_imgV.setImageBitmap(result.getContainedObject());
                } else {
                    m_imgV.setImageResource(R.drawable.no_image);
                }
            }
        }

    }

    private class UploadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Get selected picture folder
            if (getView() == null){
                return;
            }
            Spinner folderSpinner = (Spinner) getView().findViewById(R.id.uploadphoto_dialog_fragment_folderSpinner);
            int position = folderSpinner.getSelectedItemPosition();

            if (position == Spinner.INVALID_POSITION || m_spinnerAdapter.getItem(position) instanceof NullPictureFolder) {
                Toast.makeText(getActivity(), "Must select a folder", Toast.LENGTH_SHORT).show();
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
                    PersistantModel.getInstance().handleWrongCredentials(getActivity());
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Malformed url exception on upload", e);
                Toast.makeText(getActivity(), getString(R.string.UnknownErrorOccured), Toast.LENGTH_LONG).show();
                return;
            }

            final ArrayList<Pair<String,String>> params = new ArrayList<>();
            params.add(new Pair<>(PurplemoonAPIConstantsV1.AUTH_HEADER_NAME, PurplemoonAPIConstantsV1.AUTH_HEADER_VALUEPREFIX
                    + accessToken));
            final ArrayList<Pair<String,String>> list = new ArrayList<>();
            list.add(new Pair<>(PurplemoonAPIConstantsV1.PICTURE_POST_FOLDER, folder.getFolderId()));
            list.add(new Pair<>(PurplemoonAPIConstantsV1.PICTURE_POST_DESCRIPTION, desc));

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
