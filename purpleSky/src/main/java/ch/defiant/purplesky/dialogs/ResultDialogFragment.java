package ch.defiant.purplesky.dialogs;

import java.io.Serializable;

import android.content.Intent;
import android.support.v4.app.Fragment;
import ch.defiant.purplesky.constants.ArgumentConstants;

public class ResultDialogFragment<T extends Serializable> extends AlertDialogFragment {

    private T m_result;

    private int m_requestCode;

    public static final String RESULT_DATA = "result_data";
    public static final int CANCELLED = -1;
    public static final int SUCCESS = 0;
    public static final int ERROR_GENERAL = 1;
    
    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
        m_requestCode = requestCode;
    }
    
    protected void setResult(T result) {
        m_result = result;
    }

    public T getResult() {
        return m_result;
    }

    protected void deliverResult(T result) {
        if (getTargetFragment() != null) {
            Intent i = new Intent();
            addResultElements(i);
            i.putExtra(ArgumentConstants.ARG_SERIALIZABLEOBJECT, result);
            getTargetFragment().onActivityResult(m_requestCode, SUCCESS, i);
        }
    }
    
    protected void addResultElements(Intent i){
    }

    protected void abort() {
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(m_requestCode, CANCELLED, new Intent());
        }
    }
}
