package ch.defiant.purplesky.listeners;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.fragments.profile.DisplayProfileFragment;

public class OpenUserProfileListener implements AdapterView.OnItemClickListener {
    private final SherlockFragmentActivity m_activity;

    /**
     * @param userSearchResultsFragment
     */
    public OpenUserProfileListener(SherlockFragmentActivity activity) {
        m_activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MinimalUser pos = (MinimalUser) parent.getItemAtPosition(position);
        if (pos != null) {
            String profileId = pos.getUserId();
            if (profileId != null) {
                DisplayProfileFragment f = new DisplayProfileFragment();
                Bundle b = new Bundle();
                b.putString(ArgumentConstants.ARG_USERID, profileId);
                f.setArguments(b);

                FragmentTransaction transaction = m_activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
            } else {
                Toast.makeText(m_activity, m_activity.getResources().getString(R.string.ErrorCouldNotFindUser), Toast.LENGTH_SHORT).show();
            }
        }
    }
}