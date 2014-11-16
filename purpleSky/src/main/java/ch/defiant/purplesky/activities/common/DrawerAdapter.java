package ch.defiant.purplesky.activities.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;

class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private static class ViewHolder {
        TextView textV;
        ImageView imgV;
        TextView notifLbl;
    }

    public DrawerAdapter(Context context, int textViewResourceId, List<DrawerItem> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(R.layout.drawer_item, null);

            holder.imgV = (ImageView) v.findViewById(R.id.drawer_item_imageV);
            holder.notifLbl = (TextView) v.findViewById(R.id.drawer_item_notificationLbl);
            holder.textV = (TextView) v.findViewById(R.id.drawer_item_textLbl);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        if (position == 0) {
            // Messages
            holder.notifLbl.setBackgroundResource(R.drawable.rounded_rect_red);
        } else {
            holder.notifLbl.setBackgroundResource(R.drawable.rounded_rect_blue);
        }

        DrawerItem item = getItem(position);
        if (item.iconRes == 0) {
            holder.imgV.setImageBitmap(null);
            holder.imgV.setVisibility(View.GONE);
        } else {
            holder.imgV.setImageResource(item.iconRes);
            holder.imgV.setVisibility(View.VISIBLE);
        }

        holder.textV.setText(item.titleRes);

        holder.notifLbl.setVisibility(View.GONE);
        if (item.eventType != null) {
            Integer c = PurpleSkyApplication.get().getEventCount(item.eventType);
            if (c != null && c > 0) {
                holder.notifLbl.setText(String.valueOf(c));
                holder.notifLbl.setVisibility(View.VISIBLE);
            }
        }

        return v;
    }

}