package ch.defiant.purplesky.services;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.beans.UploadBean;
import ch.defiant.purplesky.beans.UploadBean.State;
import ch.defiant.purplesky.util.StreamUtility;

public class UploadService extends Service {

    public static final String TAG = UploadService.class.getSimpleName();

    private final static String LINE_END = "\r\n";
    private final static String HYPHENS = "--";

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

        AssetFileDescriptor fileDescriptor = null;
        long length = AssetFileDescriptor.UNKNOWN_LENGTH;
        try {
            fileDescriptor = getContentResolver().openAssetFileDescriptor(fileUri, "r");
            length = fileDescriptor.getLength();
        } catch (FileNotFoundException e) {
            b.setError("File not found");
            return false;
        } finally {
            try {
                if (fileDescriptor != null) {
                    fileDescriptor.close();
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "CLosing the assetFileDescriptor threw IOException", e);
                }
            }
        }

        if (length == AssetFileDescriptor.UNKNOWN_LENGTH) {
            length = Long.MAX_VALUE;
        }

        final URL u = b.getUrl();
        if (u == null) {
            b.setError("No url to upload provided");
            return false;
        }

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        InputStream inStream = null;
        String boundary = "*****";
        String charset = HTTP.UTF_8;
        int bytesRead;
        final int maxBufferSize = 1 * 1024 * 1024;
        byte[] buffer = new byte[maxBufferSize];
        try {
            // ------------------ CLIENT REQUEST

            BufferedInputStream fileInputStream;
            inStream = getContentResolver().openInputStream(fileUri);
            fileInputStream = new BufferedInputStream(inStream);
            // }
            // open a URL connection to the Servlet
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) u.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");

            // Authorization header
            if (b.getHeaderParams() != null) {
                for (NameValuePair p : b.getHeaderParams()) {
                    if (p != null) {
                        conn.setRequestProperty(p.getName(), p.getValue());
                    }
                }
            }

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Begin multipart/formdata
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(HYPHENS + boundary + LINE_END);

            // Write parameters
            if (b.getAdditionalParams() != null) {
                for (NameValuePair p : b.getAdditionalParams()) {
                    if (p != null) {
                        dos.writeBytes("Content-Disposition: form-data; name=\"" + p.getName() + "\"" + LINE_END);
                        dos.writeBytes(LINE_END); // Complete newline
                        dos.writeBytes(p.getValue());
                        dos.writeBytes(LINE_END);
                        dos.writeBytes(HYPHENS + boundary + LINE_END);
                    }
                }
            }

            String filename = "upload" + String.valueOf((new Random()).nextLong()) + "";
            dos.writeBytes("Content-Disposition: file; name=\"" + b.getFormName() + "\";");
            // uploaded_file_name is the Name of the File to be uploaded
            dos.writeBytes("filename=\"" + filename + "\"" + LINE_END);
            dos.writeBytes("Content-Transfer-Encoding: binary" + LINE_END);
            dos.writeBytes(LINE_END); // Complete newline

            buffer = new byte[maxBufferSize];
            long written = 0;

            // TODO Make Interruptable!
            // Read and write to stream
            while ((bytesRead = fileInputStream.read(buffer, 0, maxBufferSize)) != -1) {
                dos.write(buffer, 0, bytesRead);
                written += bytesRead;
                b.setProgressPercentage((int) (100.0 / length * written));
            }
            dos.writeBytes(LINE_END);
            dos.writeBytes(HYPHENS + boundary + HYPHENS + LINE_END);
            fileInputStream.close();
            // }
            dos.flush();
            dos.close();
        } catch (IOException ioe) {
            // Handle better
            b.setError("Network error");
            return false;
        }
        b.setProgressPercentage(100);

        // ------------------ read the SERVER RESPONSE
        try {
            inStream = new DataInputStream(conn.getInputStream());

            conn.getResponseCode();
            StreamUtility.inputStreamToString(inStream, charset);

            inStream.close();
            return true;
        } catch (IOException ioex) {
            StreamUtility.inputStreamToString(conn.getErrorStream(), charset);

            int responseCode;
            try {
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                b.setError("Network error");
                return false;
            }
            if (responseCode >= 400) {
                b.setError("Upload error (HTTP" + responseCode + ")");
                return false;
            }
            return false;
        }

    }
}
