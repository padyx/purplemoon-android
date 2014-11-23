package ch.defiant.purplesky.network;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Custom Body
 * @author Patrick Baenziger
 * @since 1.1.0
 */
public class ContentUriRequestBody extends RequestBody {

    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE
    private static final String contentScheme = "content";

    private final Uri contentUri;
    private final MediaType type;
    private final ContentResolver resolver;
    private final long contentLength;

    private ProgressListener listener;

    public ContentUriRequestBody(ContentResolver resolver, MediaType type, Uri contentUri, ProgressListener listener) {
        this.type = type;
        this.contentUri = contentUri;
        this.resolver = resolver;
        if(!contentScheme.equals(contentUri.getScheme())){
            throw new IllegalArgumentException("This request body only accepts 'content' URIs");
        }

        AssetFileDescriptor fileDescriptor = null;
        long length = AssetFileDescriptor.UNKNOWN_LENGTH;
        try {
            fileDescriptor = resolver.openAssetFileDescriptor(contentUri, "r");
            if(fileDescriptor == null) {
                length = 0;
            } else {
                length = fileDescriptor.getLength();
            }
        } catch (FileNotFoundException e) {
            length = 0;
        } finally {
            IOUtils.closeQuietly(fileDescriptor);
        }
        this.contentLength = length;
        this.listener = listener;
    }

    public static ContentUriRequestBody create(ContentResolver resolver, MediaType type, Uri contentUri, ProgressListener listener){
        return new ContentUriRequestBody(resolver, type, contentUri, listener);
    }

    @Override
    public MediaType contentType() {
        return type;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        InputStream stream = null;
        try{
            stream = resolver.openInputStream(contentUri);
            Source source = Okio.source(stream);

            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                if(listener != null) {
                    this.listener.transferred(total, contentLength);
                }
            }
            total = contentLength;
            this.listener.transferred(total, contentLength);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    public interface ProgressListener {
        void transferred(long bytesTransferred, long totalBytes);
    }
}
