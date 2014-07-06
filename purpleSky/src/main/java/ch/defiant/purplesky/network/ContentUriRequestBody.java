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

    private final Uri contentUri;
    private final MediaType type;
    private final ContentResolver resolver;
    private final long contentLength;
    private final String contentScheme = "content";

    public ContentUriRequestBody(ContentResolver resolver, MediaType type, Uri contentUri) {
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
            length = -0;
        } finally {
            IOUtils.closeQuietly(fileDescriptor);
        }
        this.contentLength = length;

    }

    public static ContentUriRequestBody create(ContentResolver resolver, MediaType type, Uri contentUri){
        return new ContentUriRequestBody(resolver, type, contentUri);
    }

    @Override
    public MediaType contentType() {
        return type;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        InputStream stream = null;
        try{
            Source source = Okio.source(resolver.openInputStream(contentUri));
            stream = resolver.openInputStream(contentUri);
            sink.writeAll(source);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Override
    public long contentLength() {
        return contentLength;
    }
}
