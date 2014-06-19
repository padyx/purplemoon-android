package ch.defiant.purplesky.adapters;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.LocationUtility;
import ch.defiant.purplesky.util.UserUtility;

import com.squareup.picasso.Picasso;

public class UserSearchResultListAdapter extends ArrayAdapter<MinimalUser> {

    private LocationBean m_ownLocation;

    public UserSearchResultListAdapter(Context context) {
        this(context, new ArrayList<MinimalUser>());
    }

    public UserSearchResultListAdapter(Context context, List<MinimalUser> list) {
        super(context, 0, list);
    }

    private class ViewHolder {
        ImageView userImgV;
        TextView userNameLbl;
        TextView descriptionLbl;
        TextView distanceLbl;
        TextView onlinestatusLbl;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder h = new ViewHolder();
        h.userImgV = (ImageView) v.findViewById(R.id.usersearch_result_userImgV);
        h.onlinestatusLbl = (TextView) v.findViewById(R.id.usersearch_result_item_onlinestatus);
        h.userNameLbl = (TextView) v.findViewById(R.id.usersearch_result_usernameLbl);
        h.descriptionLbl = (TextView) v.findViewById(R.id.usersearch_result_item_description);
        h.distanceLbl = (TextView) v.findViewById(R.id.usersearch_distanceLbl);
        return h;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.usersearch_result_item, null);

            holder = createViewHolder(v);
            if (getOwnLocation() != null) {
                holder.distanceLbl.setVisibility(View.VISIBLE);
            }

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        MinimalUser item = getItem(position);

        holder.userImgV.setImageResource(R.drawable.picture_placeholder);
        if (item != null) {
            holder.userNameLbl.setText(item.getUsername());
            int imgSize = LayoutUtility.dpToPx(getContext().getResources(), 75);
            URL url = UserService.getUserPreviewPictureUrl(item, UserService.UserPreviewPictureSize.getPictureForPx(imgSize));
            if (url != null) {
                Picasso.with(getContext()).load(url.toString()).placeholder(R.drawable.social_person).
                        error(R.drawable.no_image).resize(imgSize, imgSize).centerCrop().into(holder.userImgV);
            } else {
                holder.userImgV.setImageResource(R.drawable.social_person);
            }

            if (item.getOnlineStatus() != null) {
                holder.onlinestatusLbl.setText(item.getOnlineStatus().getLocalizedString(getContext()));
                holder.onlinestatusLbl.setTextColor(getContext().getResources().getColor(item.getOnlineStatus().getColor()));
            } else {
                // Has no status
                holder.onlinestatusLbl.setText(null);
                holder.descriptionLbl.setText(null);
            }

            holder.descriptionLbl.setText(UserUtility.createDescription(getContext(), item));

            if (getOwnLocation() != null) {
                if (item instanceof PreviewUser) {
                    PreviewUser prevUsr = (PreviewUser) item;
                    if (prevUsr.getCurrentLocation() != null) {
                        String str = LocationUtility.getApproximateDistanceString(prevUsr.getCurrentLocation(), getOwnLocation());
                        holder.distanceLbl.setText(str);
                    } else {
                        holder.distanceLbl.setVisibility(View.GONE);
                    }
                }
            }
        }

        return v;
    }

    public LocationBean getOwnLocation() {
        return m_ownLocation;
    }

    public void setOwnLocation(LocationBean ownLocation) {
        m_ownLocation = ownLocation;
    }
}