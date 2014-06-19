package ch.defiant.purplesky.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.beans.ProfileTriplet;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.broadcast.LocalBroadcastReceiver;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.ProfileListMap;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1.ProfileDetails;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.interfaces.IBroadcastReceiver;
import ch.defiant.purplesky.util.LocationUtility;
import ch.defiant.purplesky.util.StringUtility;
import ch.defiant.purplesky.util.UserUtility;

public class UserStatsFragment extends SherlockFragment implements IBroadcastReceiver {

    public static final String TAG = UserStatsFragment.class.getSimpleName();

    private static final String URL_DRAWABLE_PREFIX = "file:///android_res/drawable/";

    private static final String DRAWABLE_NOPICTURE_URL = URL_DRAWABLE_PREFIX + "social_person.png";
    private static final int DRAWABLE_NOPICTURE_BASESIZE = 125;

    private static final String PLACEHOLDER_PREPOSTFIX = "##";
    private static final String PLACEHOLDER_IMAGE = PLACEHOLDER_PREPOSTFIX + "IMGSRC" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_ACC_PROFILEPICTURE = PLACEHOLDER_PREPOSTFIX + "ACCESSIBILITY_PROFILEPICTURE" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_HEIGHT = PLACEHOLDER_PREPOSTFIX + "IMGHEIGHT" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_WIDTH = PLACEHOLDER_PREPOSTFIX + "IMGWIDTH" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_GENDER = PLACEHOLDER_PREPOSTFIX + "GENDER" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_USERNAME = PLACEHOLDER_PREPOSTFIX + "USERNAME" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_STATUS = PLACEHOLDER_PREPOSTFIX + "STATUS" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_STATUSCOLOR = PLACEHOLDER_PREPOSTFIX + "STATUSCOLOR" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_OVERVIEWTBL = PLACEHOLDER_PREPOSTFIX + "TABLE_OVERVIEW" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_TABLES_ALL = PLACEHOLDER_PREPOSTFIX + "TABLES_ALL" + PLACEHOLDER_PREPOSTFIX;

    private static final String PLACEHOLDER_ERROR = PLACEHOLDER_PREPOSTFIX + "ERROR_OCCURRED" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_ERRORSUB = PLACEHOLDER_PREPOSTFIX + "ERROR_SUBTITLE" + PLACEHOLDER_PREPOSTFIX;
    private static final String PLACEHOLDER_ERRORTEXT = PLACEHOLDER_PREPOSTFIX + "ERROR_TEXT" + PLACEHOLDER_PREPOSTFIX;

    private MinimalUser m_user;
    private String m_profileId;

    private LocalBroadcastReceiver m_localBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // TODO Add loading include
        View inflated = inflater.inflate(R.layout.displayuser, container, false);

        Bundle arguments = getArguments();
        if (savedInstanceState != null) {
            Object content = savedInstanceState.get(ArgumentConstants.ARG_USER);
            m_profileId = savedInstanceState.getString(ArgumentConstants.ARG_USERID);
            m_user = (MinimalUser) content; // Here it can also be a null user!
        }
        else if (arguments != null) {

            // Content can be null, thats ok. Add later
            Object content = arguments.get(ArgumentConstants.ARG_USER);
            if (content != null) {
                if (!(content instanceof DetailedUser)) {
                    throw new IllegalArgumentException("Expected DetailedUser!");
                } else {
                    m_user = (DetailedUser) content;
                }
            }

            m_profileId = arguments.getString(ArgumentConstants.ARG_USERID);
        }

        if (m_user != null) {
            updateUI(m_user);
        }

        if (m_profileId == null) {
            throw new IllegalArgumentException("Need profileId");
        }

        return inflated;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ArgumentConstants.ARG_USERID, m_profileId);
        if (m_user != null) {
            outState.putSerializable(ArgumentConstants.ARG_USER, m_user);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BroadcastTypes.BROADCAST_USERBEAN_RETRIEVED);
        m_localBroadcastReceiver = new LocalBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getSherlockActivity()).registerReceiver(m_localBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getSherlockActivity()).unregisterReceiver(m_localBroadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (m_user != null) {
            updateUI(m_user);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadcastTypes.BROADCAST_USERBEAN_RETRIEVED.equals(intent.getAction())) {

            MinimalUser u = (MinimalUser) intent.getSerializableExtra(ArgumentConstants.ARG_USER);
            if (u instanceof NullUser || m_profileId.equals(u.getUserId())) {
                updateUI(u);
            }
        }
    }

    public void updateUI(MinimalUser content) {
        View root = getView();
        if (root == null) {
            assert (false); // Should never happen
            return;
        }
        final WebView webView = (WebView) root.findViewById(R.id.displayuser_contentWebView);

        m_user = content;
        if (content instanceof NullUser) {
            webView.loadDataWithBaseURL("file:///android_asset/", createErrorContent((NullUser) content), "text/html", "UTF-8", null);
        } else if (content instanceof DetailedUser) {
            webView.loadDataWithBaseURL("file:///android_asset/", createContent((DetailedUser) m_user), "text/html", "UTF-8", null);
        } else {
            throw new IllegalArgumentException("Expected DetailedUser!");
        }
    }

    private String createErrorContent(NullUser user) {
        // Load asset raw file
        String rawHtml;
        try {
            InputStream rawResource = getResources().openRawResource(R.raw.error);
            rawHtml = IOUtils.toString(rawResource);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error when loading profile template!", e);
            }
            return "";
        }

        StringBuilder sb = new StringBuilder(rawHtml);
        StringUtility.replace(sb, PLACEHOLDER_ERROR, getString(R.string.AnErrorOccurred));
        StringUtility.replace(sb, PLACEHOLDER_ERRORSUB, user.getErrorString());
        StringUtility.replace(sb, PLACEHOLDER_ERRORTEXT, getString(R.string.ErrorApologizeForTheInconvenience));

        return sb.toString();
    }

    public String createContent(DetailedUser user) {
        // Load asset raw file
        String rawHtml;
        try {
            InputStream rawResource = getResources().openRawResource(R.raw.user_profile);
            rawHtml = IOUtils.toString(rawResource);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error when loading profile template!", e);
            }
            return "";
        }

        if (StringUtility.isNullOrEmpty(rawHtml)) {
            return "";
        }

        StringBuilder sb = new StringBuilder(rawHtml);
        StringUtility.replace(sb, PLACEHOLDER_USERNAME, escapeUserInput(user.getUsername()));
        StringUtility.replace(sb, PLACEHOLDER_ACC_PROFILEPICTURE, getString(R.string.Accessibility_UserProfilePicture));

        String gender;
        switch (user.getGender()) {
            case FEMALE:
                gender = "&#9792;";
                break;
            case MALE:
                gender = "&#9794;";
                break;
            default:
                gender = "";
                break;
        }

        StringUtility.replace(sb, PLACEHOLDER_GENDER, gender);

        String picture = DRAWABLE_NOPICTURE_URL;
        UserPreviewPictureSize size = UserPreviewPictureSize.getPictureSizeForDpi(DRAWABLE_NOPICTURE_BASESIZE, getResources());
        URL previewPic = UserService.getUserPreviewPictureUrl(user, size);
        if (previewPic != null) {
            picture = previewPic.toString();
        }

        StringUtility.replace(sb, PLACEHOLDER_IMAGE, picture);
        StringUtility.replace(sb, PLACEHOLDER_HEIGHT, String.valueOf(DRAWABLE_NOPICTURE_BASESIZE));
        StringUtility.replace(sb, PLACEHOLDER_WIDTH,  String.valueOf(DRAWABLE_NOPICTURE_BASESIZE));

        // Compose status
        StringUtility.replace(sb, PLACEHOLDER_STATUSCOLOR, mapStatusToColor(user.getOnlineStatus()));
        String status = "";
        final boolean hasCustomStatus = StringUtility.isNotNullOrEmpty(user.getOnlineStatusText());
        if (hasCustomStatus) {
            status += escapeUserInput(user.getOnlineStatusText());
        }
        if (hasCustomStatus) {
            status += " (";
        }
        if (user.getOnlineStatus() != null) {
            status += user.getOnlineStatus().getLocalizedString(getSherlockActivity());
        }
        if (hasCustomStatus) {
            status += ")";
        }

        StringUtility.replace(sb, PLACEHOLDER_STATUS, status);

        StringBuilder overviewTable = createOverviewTable(user);
        StringUtility.replace(sb, PLACEHOLDER_OVERVIEWTBL, overviewTable.toString());

        StringBuilder allTables = createLocationsTable(user);
        StringBuilder details = createDetailsTable(user);
        allTables.append(details);
        StringUtility.replace(sb, PLACEHOLDER_TABLES_ALL, allTables.toString());

        return sb.toString();
    }

    private StringBuilder createLocationsTable(DetailedUser user) {
        LocationBean home = user.getHomeLocation();
        LocationBean home2 = user.getHome2Location();
        LocationBean curr = user.getCurrentLocation();

        if (home == null && home2 == null && curr == null) {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(createHeader(getResources(), R.string.profile_header_locations));

        sb.append("<table class='content_tables'>");
        if (curr != null && curr.getLocationDescription() != null) {
            final StringBuilder url = createLocationTextAndLink(curr);

            createAndAddTableRow(sb, R.string.profile_locations_current, url.toString());
        }
        if (home != null) {
            final StringBuilder url = createLocationTextAndLink(home);

            createAndAddTableRow(sb, R.string.profile_locations_home, url.toString());
        }
        if (home2 != null) {
            final StringBuilder url = createLocationTextAndLink(home2);

            createAndAddTableRow(sb, R.string.profile_locations_secondhome, url.toString());
        }
        sb.append("</table>");
        return sb;
    }

    private StringBuilder createLocationTextAndLink(LocationBean bean) {
        final String country = LocationUtility.getCountryNameByIsoId(bean.getCountryId());
        final StringBuilder url = new StringBuilder();
        final boolean shouldCreateLink = bean.getLatitude() != null && bean.getLongitude() != null;

        if (shouldCreateLink) {
            url.append("<a href='geo:" + bean.getLatitude() + "," + bean.getLongitude() + "' target='_blank'>");
        }
        url.append(bean.getLocationDescription());
        if (country != null) {
            url.append(" (");
            url.append(country);
            url.append(")");
        }
        if (shouldCreateLink) {
            url.append("</a>");
        }
        return url;
    }

    private StringBuilder createOverviewTable(DetailedUser user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='overview_text'>");
        ProfileTriplet detail = UserUtility.getDetail(ProfileDetails.TARGET_PARTNER, user);
        String relStatus = null;
        if (detail != null) {
            // Try to get the status...
            ProfileTriplet relationship = UserUtility.getDetailRecursive(ProfileDetails.TARGET_PARTNER_RELATIONSHIPSTATUS, detail);
            if(relationship != null){
                relStatus = relationship.getDisplayValue();
            }
        }

        sb.append( StringUtility.join(", ", 
                String.valueOf(user.getAge()), 
                user.getSexuality().getLocalizedString(),
                relStatus)
                );
        sb.append("</div>");

        if (user.isAgeVerified()) {
            sb.append("<div class='overview_text'><div id='verifieduser'>" + getString(R.string.VerifiedUser) + "</div></div>");
        }
        return sb;
    }

    private void createAndAddTableRow(StringBuilder sb, int labelResource, String value) {
        createAndAddTableRow(sb, getString(labelResource), value);
    }

    private void createAndAddTableRow(StringBuilder sb, String label, String value) {
        sb.append("<tr><td class='rowlabel_cell'><div class='rowlabel'>");
        sb.append(label);
        sb.append("</div></td><td><div class='spacer_10px_horizontal'></div></td><td class='rowvalue_cell'><div class='rowvalue'>");
        if (value != null) {
            sb.append(escapeUserInput(value));
        }
        sb.append("</div></td></tr>");
    }

    private void createAndAddSpanningTableRow(StringBuilder sb, String content) {
        sb.append("<tr><td colspan='3'>");
        if (content != null) {
            sb.append(content);
        }
        sb.append("</td></tr>");
    }

    private StringBuilder createDetailsTable(DetailedUser user) {
        StringBuilder sb = new StringBuilder();

        Map<String, ProfileTriplet> profileDetails = user.getProfileDetails();

        final Resources resources = getResources();
        ProfileListMap listMap = ProfileListMap.getInstance();
        for (int ithgroup = 0, size = listMap.GROUPS.size(); ithgroup < size; ithgroup++) {

            /* Create a new row to be added. */
            StringBuilder title = createHeader(resources, listMap.GROUPS.get(ithgroup));

            List<StringBuilder> rows = new ArrayList<StringBuilder>();

            for (String apikey : listMap.GROUP_LIST_APIKEYS.get(ithgroup)) {
                ProfileTriplet t = profileDetails.get(apikey);
                if (t == null)
                    continue;
                if (t.getDisplayKey() == null && t.getList() == null) {
                    continue;
                }
                if (StringUtility.isNullOrEmpty(t.getDisplayValue()) && t.getRawValue() == null && !t.isNested()) {
                    // Not interested in those
                    continue;
                }
                /* Create a new row to be added. */
                List<StringBuilder> tr = createRows(t, false);
                rows.addAll(tr);
            }
            // Add header only if there are any rows from this section
            if (rows.size() > 0) {
                sb.append(title);
                sb.append("<table class='content_tables'>");
                for (StringBuilder r : rows) {
                    // Add row to TableLayout.
                    sb.append(r);
                }
                sb.append("</table>");
            }
        }
        return sb;
    }

    private List<StringBuilder> createRows(final ProfileTriplet t, boolean nested) {
        ArrayList<StringBuilder> rows = new ArrayList<StringBuilder>();
        if (!nested && t.isSimple()) {
            StringBuilder sb = new StringBuilder();

            String val = "";
            if (t.getDisplayValue() != null) {
                val = t.getDisplayValue();
            } else {
                if (t.getRawValue() != null) {
                    val = t.getRawValue().toString();
                }
            }
            createAndAddTableRow(sb, t.getDisplayKey(), val);

            rows.add(sb);
        } else if (t.getChildren() != null || t.getList() != null) {
            // Then take this key as 'title'

            if (t.getChildren() != null) {
                // Iterate over all children
                for (Entry<String, ProfileTriplet> e : t.getChildren().entrySet()) {
                    List<StringBuilder> list = createRows(e.getValue(), true);
                    rows.addAll(list);
                }
            } else if (t.getList() != null) {
                // Iterate over all children
                for (Map<String, ProfileTriplet> element : t.getList()) {
                    if (!rows.isEmpty()) {
                        // separator between list elements
                        // TODO maybe do this better?
                        StringBuilder sep = new StringBuilder();
                        createAndAddSpanningTableRow(sep, "&nbsp;");
                        rows.add(sep);
                    }

                    for (Entry<String, ProfileTriplet> entry : element.entrySet()) {
                        List<StringBuilder> list = createRows(entry.getValue(), true);
                        rows.addAll(list);
                    }
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            String val = "";
            if (t.getDisplayValue() != null) {
                val = t.getDisplayValue();
            } else {
                if (t.getRawValue() != null) {
                    val = t.getRawValue().toString();
                }
            }
            createAndAddTableRow(sb, t.getDisplayKey(), val);

            rows.add(sb);
        }

        return rows;
    }

    private StringBuilder createHeader(final Resources resources, int titleResId) {
        return createHeader(resources.getString(titleResId));
    }

    private StringBuilder createHeader(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='section_header'>");
        sb.append(header);
        sb.append("</div>");
        return sb;
    }

    private static String mapStatusToColor(OnlineStatus onlineStatus) {
        final String def = "#909090";
        if (onlineStatus == null) {
            return def;
        }
        String color;
        switch (onlineStatus) {
            case ONLINE:
                color = "#009900";
                break;
            case AWAY:
                // Orange
                color = "#FF6600";
                break;
            case BUSY:
                color = "#CC0000";
                break;
            default:
                color = def; // Default: Grey
                break;
        }
        return color;
    }

    private static String escapeUserInput(String input) {
        if (input == null)
            return null;

        return input.replaceAll(PLACEHOLDER_PREPOSTFIX, "&#35;&#35;");
    }
}
