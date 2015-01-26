package ch.defiant.purplesky.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;

public class OpenUserProfileListener implements AdapterView.OnItemClickListener {
    private final Activity m_activity;

    public OpenUserProfileListener(Activity activity) {
        m_activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MinimalUser pos = (MinimalUser) parent.getItemAtPosition(position);
        if (pos != null) {
            String profileId = pos.getUserId();
            if (profileId != null) {
                Intent intent = new Intent(m_activity, DisplayProfileActivity.class);
                intent.putExtra(ArgumentConstants.ARG_USERID, profileId);
                m_activity.startActivity(intent);
                // FIXME PBN Animation is not nice - should be like the drawer
            } else {
                Toast.makeText(m_activity, m_activity.getResources().getString(R.string.ErrorCouldNotFindUser), Toast.LENGTH_SHORT).show();
            }
        }
    }
}