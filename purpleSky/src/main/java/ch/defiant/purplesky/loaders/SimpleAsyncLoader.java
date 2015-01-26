package ch.defiant.purplesky.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.IdRes;

/**
 * A custom loader that provides basic implementation for starting, cancelling and stopping the loading.
 */
public abstract class SimpleAsyncLoader<T> extends AsyncTaskLoader<T> {

    private final int m_type;

    public SimpleAsyncLoader(Context context) {
        this(context, 0);
    }

    public SimpleAsyncLoader(Context context, @IdRes int type) {
        super(context);
        m_type = type;
    }

    private T m_result;

    /**
     * Called when there is new data to deliver to the client. The super class will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(T value) {
        if (isReset()) {
            // An async query came in while the loader is stopped.
            // We don't need the result.
            if (m_result != null) {
                onReleaseResources(m_result);
            }
        }
        T oldValue = m_result; // TODO pbn Check that this is correct (Source in Android Docs use the new result...)
        m_result = value;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(value);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldValue != null) {
            onReleaseResources(oldValue);
        }
    }

    /**
     * Custom method to allow subclasses to release resources from results
     * 
     * @param result
     */
    protected void onReleaseResources(T result) {
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (m_result != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(m_result);
        }

        if (takeContentChanged() || m_result == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
    }

    public int getType() {
        return m_type;
    }

}
