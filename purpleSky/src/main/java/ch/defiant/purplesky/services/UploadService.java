package ch.defiant.purplesky.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.UploadBean;
import ch.defiant.purplesky.beans.UploadBean.State;
import ch.defiant.purplesky.network.ContentUriRequestBody;
import ch.defiant.purplesky.util.ErrorUtility;

public class UploadService extends Service {

    public static final String TAG = UploadService.class.getSimpleName();

    private final BinderServiceWrapper<UploadService> m_binder = new BinderServiceWrapper<UploadService>(this);
    private final LinkedList<UploadBean> m_queue = new LinkedList<UploadBean>();
    private final Set<UploadBean> m_completed = new HashSet<UploadBean>();

    private AtomicReference<Thread> m_uploader = new AtomicReference<Thread>();

    @Override
    public IBinder onBind(Intent intent) {
        return m_binder;
    }

    public void enqueue(UploadBean b) {
        if (b == null) {
            throw new IllegalArgumentException("Null bean cannot be uploaded");
        }
        synchronized (m_queue) {
            m_queue.add(b);
            Thread uploader = m_uploader.get();
            if (uploader == null || !uploader.isAlive()) {
                m_uploader.compareAndSet(uploader, new Thread(new UploadRunnable()));
                m_uploader.get().start();
            }
        }
    }

    public Set<UploadBean> getCompletedUploads() {
        return Collections.unmodifiableSet(m_completed);
    }

    public List<UploadBean> getPendingUploads() {
        return Collections.unmodifiableList(m_queue);
    }

    private class UploadRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                UploadBean b;
                synchronized (m_queue) {
                    if (m_queue.isEmpty()) {
                        return;
                    } else {
                        b = m_queue.getFirst();
                    }
                }
                b.setState(State.IN_PROGRESS);

                final boolean ok = upload(b);

                synchronized (m_queue) {
                    m_queue.removeFirst();
                }
                synchronized (m_completed) {
                    m_completed.add(b);
                    b.setState(ok ? State.COMPLETE : State.ERROR);
                }
            }
        }
    }

    private boolean upload(UploadBean b) {
        // Get file
        final Uri fileUri = b.getFileUri();
        final AssetFileDescriptor descriptor;
        if(fileUri == null){
            Log.e(TAG, "No fileUri passed to upload service");
            b.setError(getString(R.string.PictureNotFound));
            return false;
        }

        // Get length and stuff
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = getContentResolver().openAssetFileDescriptor(fileUri, "r");
            if(fileDescriptor == null) {
                b.setError(getString(R.string.PictureNotFound));
                return false;
            }

        } catch (FileNotFoundException e) {
            b.setError("File not found");
            return false;
        } finally {
            if(fileDescriptor != null) {
                // Cannot use IOUtils.closeQuietly below util minSdk is >= 4.4 (Lvl 19):
                // Only since then AssetFileDescriptor implements Closable (Bug #100)
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        final URL u = b.getUrl();
        final String mimeType = getContentResolver().getType(fileUri);
        if (u == null) {
            b.setError(getString(R.string.UnknownError_X, ErrorUtility.getErrorId(new Throwable())));
            return false;
        }

        Request.Builder builder = createRequestBuilder(b);
        MultipartBuilder multipartBuilder = createMultipartBuilder(b, fileUri, mimeType);

        try {
            builder.url(b.getUrl()).post(multipartBuilder.build());
            Response response = new OkHttpClient().newCall(builder.build()).execute();

            if(response.isSuccessful()) {
                b.setProgressPercentage(100);
                return true;
            } else {
                b.setError(getString(R.string.UnknownErrorOccured));
                return false;
                // TODO Handle api responses
            }

        } catch (IOException ioe) {
            // Handle better
            b.setError(getString(R.string.ErrorNoNetworkGenericShort));
            return false;
        }
    }

    private Request.Builder createRequestBuilder(UploadBean b) {
        Request.Builder builder = new Request.Builder();

        Collection<NameValuePair> headers = b.getHeaderParams();
        if(headers != null) {
            for(NameValuePair header : headers){
                builder.header(header.getName(), header.getValue());
            }
        }
        return builder;
    }

    private MultipartBuilder createMultipartBuilder(UploadBean b, Uri fileUri, String mimeType) {
        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        // Additional params
        Collection<NameValuePair> additionalParams = b.getFormParams();
        if(additionalParams != null) {
            for (NameValuePair pair : additionalParams) {
                multipartBuilder.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + pair.getName() + "\""),
                        RequestBody.create(null, pair.getValue()));
            }
        }

        multipartBuilder.addPart(
                Headers.of(
                        "Content-Disposition", "file; name=\"" + b.getFormName() + "\";filename=\""+fileUri.getLastPathSegment()+"\"",
                        "Content-Transfer-Encoding", "binary"
                ),
                ContentUriRequestBody.create(getContentResolver(), MediaType.parse(mimeType), fileUri, b)
        );
        return multipartBuilder;
    }

}
