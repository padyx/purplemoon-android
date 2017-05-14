package ch.defiant.purplesky.fragments.common;

import android.app.Fragment;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.core.AdapterOptions;
import ch.defiant.purplesky.util.Holder;


public abstract class ModelListFragment<T> extends Fragment {

    protected AtomicReference<AsyncTask<AdapterOptions, ?, Holder<List<T>>>> taskRef = new AtomicReference<>();


    private class LoadTask extends AsyncTask<AdapterOptions, Void, Holder<List<T>>> {

        @Override
        protected Holder<List<T>> doInBackground(AdapterOptions... params) {
            AdapterOptions options;
            if(params == null || params.length == 0){
                options=null;
            } else {
                options = params[0];
            }

            try {
                List<T> list = loadInBackground(options);
                return Holder.of(list);
            } catch (Exception e) {
                return new Holder(e);
            }
        }

        @Override
        protected void onPostExecute(Holder<List<T>> result) {
            try {
                if (result.isException()) {
                    onLoadMoreError(result.getException());
                } else {
                    onLoadMoreCompleted(result.getContainedObject());
                }
            } finally {
                taskRef.compareAndSet(LoadTask.this, null);
            }
        }

        @Override
        protected void onCancelled() {
            taskRef.compareAndSet(LoadTask.this, null);
        }
    }


    public void loadMore(AdapterOptions options) {
        AsyncTask<AdapterOptions, ?, Holder<List<T>>> task = taskRef.get();

        if (task == null || task.isCancelled()) {
            AsyncTask<AdapterOptions, ?, Holder<List<T>>> newTask = new LoadTask();
            if (taskRef.compareAndSet(task, newTask)){
                newTask.execute(options);
            }
        }
    }


    public void cancelTasks(){
        AsyncTask<AdapterOptions, ?, Holder<List<T>>> task = taskRef.get();
        if(task != null && !task.isCancelled()){
            task.cancel(true);
        }
    }

    public abstract List<T> loadInBackground(AdapterOptions options) throws Exception;
    protected abstract void onLoadMoreCompleted(List<T> newData);
    protected abstract void onLoadMoreError(Exception e);

}
