package ch.defiant.purplesky.fragments.common;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Patrick Bänziger
 */
public class TaskFragment<Result> extends DialogFragment {

    private Exception m_resultingException;
    private Result m_result;
    private AtomicBoolean m_isFinished = new AtomicBoolean(false);

    @NonNull
    private WeakReference<TaskListener<Result>> m_listener = new WeakReference<>(null);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        // Workaround: Dialog dismissed on rotation - https://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public void setListener(@Nullable TaskListener<Result> taskListener){
        m_listener = new WeakReference<>(taskListener);
    }

    public Result getResult() throws Exception{
        if(m_resultingException != null){
            throw m_resultingException;
        }
        return m_result;
    }


    private void taskEnded(boolean isException){
        TaskListener<Result> listener = m_listener.get();
        if(listener != null){
            listener.taskFinished(this, isException);
        }
        m_isFinished.set(true);
    }

    private void taskSuccessful(Result r) {
        m_result = r;
        taskEnded(false);
    }

    private void taskException(Exception e) {
        m_resultingException = e;
        taskEnded(true);
    }

    public boolean isTaskFinished(){
        return m_isFinished.get();
    }

    public void execute(@NonNull Callable<Result> task) {
        CallableAsyncTask<Result> callableTask = new CallableAsyncTask<>(task, this);
        callableTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static class CallableAsyncTask<Result> extends AsyncTask<Void, Void, Result> {

        private final Callable<Result> m_callable;
        private TaskFragment<Result> m_taskFragment;

        public CallableAsyncTask(@NonNull Callable<Result> callable, @NonNull TaskFragment<Result> taskFragment){
            m_callable = callable;
            m_taskFragment = taskFragment;
        }

        @Override
        protected Result doInBackground(Void[] params)  {
            try {
                Result r = m_callable.call();
                m_taskFragment.taskSuccessful(r);
                return r;
            } catch (Exception e){
                m_taskFragment.taskException(e);
                return null;
            }
        }

    }

    public static interface TaskListener<Result> extends EventListener {

        void taskFinished(TaskFragment<Result> taskFragment, boolean isException);

    }


}
