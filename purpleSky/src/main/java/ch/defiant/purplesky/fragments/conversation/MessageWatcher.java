package ch.defiant.purplesky.fragments.conversation;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

/**
 * Checks that the length of the entered text is not zero.
 */
class MessageWatcher implements TextWatcher {

    private final ImageView m_field;

    public MessageWatcher(ImageView e) {
        m_field = e;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == null) {
            return;
        }
        m_field.setEnabled(s.length() > 0);
    }
}