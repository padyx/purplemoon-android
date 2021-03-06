package ch.defiant.purplesky.fragments.profile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.EventActivity;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.LocationBean;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.NullUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.broadcast.LocalBroadcastReceiver;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.enums.profile.RelationshipStatus;
import ch.defiant.purplesky.interfaces.IBroadcastReceiver;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.LocationUtility;
import ch.defiant.purplesky.util.StringUtility;

public class UserStatsFragment extends Fragment implements IBroadcastReceiver {

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

    private static final String GEO_SCHEME = "geo:";

    private MinimalUser m_user;
    private String m_profileId;

    private LocalBroadcastReceiver m_localBroadcastReceiver;

    private static class EventInterface {
        private final Context context;

        public EventInterface(Context c){
            this.context = c;
        }

        @JavascriptInterface
        public void go(String eventString){
            int eventId;
            try{
                eventId = Integer.parseInt(eventString);
            } catch (NumberFormatException nfe){
                Log.e(TAG, "Event Id not a number:"+eventString);
                return;
            }

            Intent intent = new Intent(context, EventActivity.class);
            intent.putExtra(ArgumentConstants.ARG_ID, eventId);
            context.startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // TODO Add loading include
        View inflated = inflater.inflate(R.layout.webview_full, container, false);
        setupWebView((WebView) inflated.findViewById(R.id.webview_full_webview));

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

    private void setupWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new EventInterface(getActivity()), "ppmoonEvent");
        webView.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith(GEO_SCHEME)){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(m_localBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(m_localBroadcastReceiver);
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
        final WebView webView = (WebView) root.findViewById(R.id.webview_full_webview);

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
            InputStream rawResource = getResources().openRawResource(R.raw.error_raw);
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
            InputStream rawResource = getResources().openRawResource(R.raw.user_profile_raw);
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
            status += user.getOnlineStatus().getLocalizedString(getActivity());
        }
        if (hasCustomStatus) {
            status += ")";
        }

        StringUtility.replace(sb, PLACEHOLDER_STATUS, status);

        StringBuilder overviewTable = createOverviewTable(user);
        StringUtility.replace(sb, PLACEHOLDER_OVERVIEWTBL, overviewTable.toString());

        StringBuilder allTables = createEventTable(user);
        StringBuilder locationsTable = createLocationsTable(user);

        StringBuilder details = createGeneralTable(user);
        details.append(createOccupationTable(user));
        details.append(createBodyTable(user));

        StringBuilder relationshipTable = createRelationshipTable(user);
        StringBuilder friendshipTable = createFriendshipTable(user);
        if(relationshipTable.length() > 0 || friendshipTable.length() > 0){
            details.append(createHeader(getResources(), R.string.profile_sectionHeader_getToKnow));
            if(relationshipTable.length() > 0) {
                details.append(relationshipTable);
            }
            if(friendshipTable.length() > 0){
                details.append(friendshipTable);
            }
        }

        // FIXME Implement Occupation

        details.append(createBeliefTable(user));
        details.append(createChatHomepageTable(user));
        allTables.append(locationsTable);
        allTables.append(details);
        allTables.append(createAboutProfileTable(user));
        StringUtility.replace(sb, PLACEHOLDER_TABLES_ALL, allTables.toString());

        return sb.toString();
    }

    private StringBuilder createGeneralTable(DetailedUser user) {
        StringBuilder sb = new StringBuilder();

        if (StringUtility.isNotNullOrEmpty(user.getFirstName())) {
            createAndAddTableRow(sb, R.string.profile_firstname, user.getFirstName());
        }
        if (StringUtility.isNotNullOrEmpty(user.getNicknames())) {
            createAndAddTableRow(sb, R.string.profile_nicknames, user.getNicknames());
        }
        if (StringUtility.isNotNullOrEmpty(user.getLastName())) {
            createAndAddTableRow(sb, R.string.profile_lastname, user.getLastName());
        }
        if (user.getBirthDate() != null) {
            createAndAddTableRow(sb, R.string.profile_birthdate, DateUtility.getMediumDateString(user.getBirthDate()));
        }
        if (StringUtility.isNotNullOrEmpty(user.getEmailAddress())) {
            createAndAddTableRow(sb, R.string.profile_emailaddress, user.getEmailAddress());
        }

        if(sb.length() > 0){
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_General));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
    }

    private StringBuilder createEventTable(DetailedUser user){
        Map<Integer, String> eventsTmp = user.getEventsTmp();

        StringBuilder builder = new StringBuilder();
        if (eventsTmp == null || eventsTmp.isEmpty()){
           return builder;
        }

        builder.append("<table class='content_tables'>");
        builder.append(createHeader(getResources(), R.string.profile_header_events));
        for(Entry<Integer, String> e : eventsTmp.entrySet()){
            String link = "<a onClick='ppmoonEvent.go("+e.getKey()+"); return false;' href='#'>"+e.getValue()+"</a>";
            createAndAddSpanningTableRow(builder, link);
        }
        builder.append("</table>");

        return builder;
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
                    url.append("<a href='").
                    append(GEO_SCHEME).
                    append(bean.getLatitude()).
                    append(",").
                    append(bean.getLongitude()).
                    append("' target='_blank'>");
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

        RelationshipStatus status = null;
        if(user.getRelationshipInformation() != null){
            status = user.getRelationshipInformation().getRelationshipStatus();
        }
        String relStatus = null;
        if(status != null){
            relStatus = getString(status.getStringResource());
        }

        sb.append( StringUtility.join(", ", 
                String.valueOf(user.getAge()), 
                user.getSexuality().getLocalizedString(getResources(), user.getGender()),
                relStatus)
                );
        sb.append("</div>");

        if (user.isAgeVerified()) {
            sb.append("<div class='overview_text'><div id='verifieduser'>").
                    append(getString(R.string.VerifiedUser)).
                    append("</div></div>");
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

    private StringBuilder createBodyTable(DetailedUser user){
        StringBuilder sb = new StringBuilder();
         if(user.getHeight() != null) {
            createAndAddTableRow(sb, R.string.bodyHeight, String.valueOf(user.getHeight()));
        }
        if(user.getWeight() != null){
            createAndAddTableRow(sb, R.string.bodyWeight, String.valueOf(user.getWeight()));
        }
        if(user.getPhysique() != null){
            createAndAddTableRow(sb, R.string.profile_physique, getString(user.getPhysique().getStringRes()));
        }
        if(user.getEyeColor() != null){
            createAndAddTableRow(sb, R.string.profile_eye_color, getString(user.getEyeColor().getStringResource()));
        }
        if(user.getHairLength() != null){
            createAndAddTableRow(sb, R.string.profile_hair_length, getString(user.getHairLength().getStringResource()));
        }
        if(user.getHairColor() != null){
            createAndAddTableRow(sb, R.string.profile_hair_color, getString(user.getHairColor().getStringResource()));
        }
        if(user.getFacialHair() != null){
            createAndAddTableRow(sb, R.string.profile_facial_hair, getString(user.getFacialHair().getStringResource()));
        }
        if(sb.length() > 0){
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_body));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
    }

    private StringBuilder createRelationshipTable(DetailedUser user){
        StringBuilder sb = new StringBuilder();
        if(user.getRelationshipInformation() != null){
            DetailedUser.RelationshipInformation relationshipInfo = user.getRelationshipInformation();
            if (relationshipInfo.getRelationshipStatus() != null){
                createAndAddTableRow(sb, R.string.RelationshipStatus, getString(relationshipInfo.getRelationshipStatus().getStringResource()));
            }
            addRelationInformation(sb, relationshipInfo);
        }
        if(sb.length() > 0){
            sb.insert(0, createSubsectionHeader(getResources(), R.string.profile_subsectionHeader_getToKnowPartner));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
    }

    private StringBuilder createFriendshipTable(DetailedUser user){
        StringBuilder sb = new StringBuilder();

        if(user.getFriendshipInformation() != null){
            DetailedUser.FriendshipInformation friendshipInfo = user.getFriendshipInformation();
            if (user.getFriendshipInformation().getTargetGender() != null) {
                createAndAddTableRow(sb, R.string.profile_target_friends_gender, getString(friendshipInfo.getTargetGender().getStringResource()));
            }
            addRelationInformation(sb, friendshipInfo);
        }
        if(sb.length() > 0){
            sb.insert(0, createSubsectionHeader(getResources(), R.string.profile_subsectionHeader_getToKnowFriend));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
    }

    private void addRelationInformation(StringBuilder sb, DetailedUser.AbstractRelation relationshipInfo) {
        if (relationshipInfo.getDesiredAgeFrom() != null){
            createAndAddTableRow(sb, R.string.MinimumAge, String.valueOf(relationshipInfo.getDesiredAgeFrom()));
        }
        if (relationshipInfo.getDesiredAgeTill() != null){
            createAndAddTableRow(sb, R.string.MaximumAge, String.valueOf(relationshipInfo.getDesiredAgeTill()));
        }
        if (relationshipInfo.getMaximumDistance() != null){
            createAndAddTableRow(sb, R.string.MaxDistance, String.valueOf(relationshipInfo.getMaximumDistance()));
        }
        if(StringUtility.isNotNullOrEmpty(relationshipInfo.getText())){
            createAndAddSpanningTableRow(sb, relationshipInfo.getText());
        }
    }

    private StringBuilder createBeliefTable(PreviewUser user){
        StringBuilder sb = new StringBuilder();

        if (user.getDrinkerFrequency() != null){
            createAndAddTableRow(sb, R.string.profile_drinker, getString(user.getDrinkerFrequency().getStringResource()));
        }
        if (user.getSmokerFrequency() != null){
            createAndAddTableRow(sb, R.string.profile_smoker, getString(user.getSmokerFrequency().getStringResource()));
        }
        if (user.getReligion() != null){
            createAndAddTableRow(sb, R.string.profile_religion, getString(user.getReligion().getStringResource()));
        }
        if (user.getPolitics() != null){
            createAndAddTableRow(sb, R.string.profile_politics, getString(user.getPolitics().getStringResource()));
        }
        if (user.getVegetarian() != null){
            createAndAddTableRow(sb, R.string.profile_vegetarian, getString(user.getVegetarian().getStringResource()));
        }
        if (user.getWantsKids() != null){
            createAndAddTableRow(sb, R.string.profile_kids_want, getString(user.getWantsKids().getStringResource()));
        }
        if (user.getHasKids() != null){
            createAndAddTableRow(sb, R.string.profile_kids_have, getString(user.getHasKids().getStringResource()));
        }
        if(sb.length() > 0){
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_beliefs));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
    }

    private StringBuilder createChatHomepageTable(DetailedUser user) {
        StringBuilder sb = new StringBuilder();

        if(user.getChatFrequency() != null){
            createAndAddTableRow(sb, R.string.profile_chat_frequency, getString(user.getChatFrequency().getStringResource()));
        }
        if(StringUtility.isNotNullOrEmpty(user.getWhichChats())){
            createAndAddTableRow(sb, R.string.profile_which_chats, user.getWhichChats());
        }
        if(StringUtility.isNotNullOrEmpty(user.getChatNames())){
            createAndAddTableRow(sb, R.string.profile_chat_names, user.getChatNames());
        }
        if(StringUtility.isNotNullOrEmpty(user.getHomepage())){
            createAndAddTableRow(sb, R.string.profile_homepage, user.getHomepage());
        }

        if(sb.length() > 0){
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_ChatContactHomepage));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }

        if(!CollectionUtil.isEmpty(user.getMessengers())){
            StringBuilder innerBuilder = new StringBuilder();

            innerBuilder.append(createSubsectionHeader(getResources(), R.string.profile_subsectionHeader_messengers));
            Collection<DetailedUser.MessengerBean> messengers = user.getMessengers();
            for(DetailedUser.MessengerBean b: messengers){
                innerBuilder.append("<table class='content_tables' style='padding-bottom:1em' >");
                createAndAddTableRow(innerBuilder, R.string.profile_messenger_type, getString(b.getType().getStringRes()));
                createAndAddTableRow(innerBuilder, R.string.profile_messenger_username, b.getUsername());
                innerBuilder.append("</table>\n");
            }

            sb.append(innerBuilder);
        }

        return sb;
    }

    private StringBuilder createOccupationTable(DetailedUser user) {
        StringBuilder sb = new StringBuilder();

        Collection<DetailedUser.Occupation> occupations = user.getOccupations();
        if (!CollectionUtil.isEmpty(occupations)){
            for(DetailedUser.Occupation o : occupations){
                sb.append("<table class='content_tables' style='padding-bottom:1em'>");
                if(o.getOccupationName() != null){
                    createAndAddTableRow(sb, R.string.profile_occupation_name, o.getOccupationName());
                }
                if(o.getOccupationType() != null) {
                    createAndAddTableRow(sb, R.string.profile_occupation_type, getString(o.getOccupationType().getStringRes()));
                }
                if(StringUtility.isNotNullOrEmpty(o.getCompanyName())) {
                    createAndAddTableRow(sb, R.string.profile_occupation_company_name, o.getCompanyName());
                }
                if(StringUtility.isNotNullOrEmpty(o.getSchoolDirection())) {
                    createAndAddTableRow(sb, R.string.profile_occupation_school_direction, o.getSchoolDirection());
                }
                if(StringUtility.isNotNullOrEmpty(o.getSchoolName())) {
                    createAndAddTableRow(sb, R.string.profile_occupation_school_name, o.getSchoolName());
                }
                sb.append("</table>\n");
            }
        }

        if (sb.length() > 0) {
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_occupations));
        }

        return sb;
    }

    private StringBuilder createAboutProfileTable(DetailedUser user){
        StringBuilder sb = new StringBuilder();

        final Date now = new Date();
        if (user.getCreateDate() != null){
            String dateString;
            if (DateUtility.isWithin24Hours(now, user.getCreateDate())){
                dateString = getString(R.string.profile_date_last24h);
            } else {
                dateString = DateUtility.getMediumDateString(user.getCreateDate());
            }
            createAndAddTableRow(sb, R.string.profile_create_date, dateString);
        }
        if (user.getUpdateDate() != null){
            String dateString;
            if (DateUtility.isWithin24Hours(now, user.getUpdateDate())){
                dateString = getString(R.string.profile_date_last24h);
            } else {
                dateString = DateUtility.getMediumDateString(user.getUpdateDate());
            }
            createAndAddTableRow(sb, R.string.profile_last_update, dateString);
        }
        if (user.getLastOnlineDate() != null){
            String dateString;
            if (DateUtility.isWithin24Hours(now, user.getLastOnlineDate())){
                dateString = getString(R.string.profile_date_last24h);
            } else {
                dateString = DateUtility.getMediumDateString(user.getLastOnlineDate());
            }
            createAndAddTableRow(sb, R.string.profile_last_online, dateString);
        }

        if(sb.length() > 0){
            sb.insert(0, createHeader(getResources(), R.string.profile_sectionHeader_AboutProfile));
            sb.insert(0,"<table class='content_tables'>");
            sb.append("</table>\n");
        }
        return sb;
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

    private StringBuilder createSubsectionHeader(final Resources resources, int titleResId) {
        return createSubsectionHeader(resources.getString(titleResId));
    }

    private StringBuilder createSubsectionHeader(String header) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='subsection_header'>");
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
