package ch.defiant.purplesky.fragments.photovote;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.photovotes.IPhotoVoteAdapter;
import ch.defiant.purplesky.beans.NoMorePhotoVoteBean;
import ch.defiant.purplesky.beans.PhotoVoteBean;
import ch.defiant.purplesky.broadcast.BroadcastTypes;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.enums.PhotoVoteVerdict;
import ch.defiant.purplesky.enums.UserPictureSize;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;

public class PhotoVoteFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<PhotoVoteBean>> {

    public static final String TAG = PhotoVoteFragment.class.getSimpleName();

    @Inject
    protected IPhotoVoteAdapter photoVoteAdapter;

    private static final String VERDICT = "verdict";
    private PhotoVoteBean m_currentBean = null;

    private ImageView m_imgV;
    private View m_box;

    private TextView m_remainingLbl;
    private Integer m_remaining;

    private ImageView m_pinLbl;
    private AtomicBoolean m_loadFinished = new AtomicBoolean();

    private int maxDisplaySideLengthPx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        maxDisplaySideLengthPx = LayoutUtility.getMaximumDisplaySidePixels(getResources());
        
        View inflated = inflater.inflate(R.layout.photovote, container, false);
        inflated.findViewById(R.id.photovote_voteNeutralNegative).setOnClickListener(new VoteListener(0));
        inflated.findViewById(R.id.photovote_vote1Cute).setOnClickListener(new VoteListener(1));
        inflated.findViewById(R.id.photovote_vote2VeryCute).setOnClickListener(new VoteListener(2));
        inflated.findViewById(R.id.photovote_vote3Stunning).setOnClickListener(new VoteListener(3));
        inflated.findViewById(R.id.photovote_overlay).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doShowPins();
            }
        });
        m_pinLbl = (ImageView) inflated.findViewById(R.id.photovote_pinLbl);
        m_remainingLbl = (TextView) inflated.findViewById(R.id.photovote_remainingLbl);
        m_remainingLbl.setText(R.string.QuestionMark);
        m_imgV = (ImageView) inflated.findViewById(R.id.photovote_cacheableImageView);
        m_box = inflated.findViewById(R.id.photovote_votingBox);
        m_box.setEnabled(false);
        return inflated;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLoaderManager().initLoader(R.id.loader_photovotes_main, null, this);
    }

    private class VoteListener implements OnClickListener {

        private final int m_vote;

        public VoteListener(int vote) {
            m_vote = vote;
        }

        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putInt(VERDICT, m_vote);
            getLoaderManager().restartLoader(R.id.loader_photovotes_main, b, PhotoVoteFragment.this);
        }

    }

    @Override
    public Loader<Holder<PhotoVoteBean>> onCreateLoader(int loaderId, final Bundle args) {
        // Deactivate UI
        m_box.setVisibility(View.INVISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);

        final boolean needsCount = m_remaining == null;
        return new SimpleAsyncLoader<Holder<PhotoVoteBean>>(getActivity()) {

            @Override
            public Holder<PhotoVoteBean> loadInBackground() {
                m_loadFinished.set(false);
                if (m_currentBean != null && args.containsKey(VERDICT)) {
                    PhotoVoteVerdict verdict = PhotoVoteVerdict.values()[args.getInt(VERDICT)];
                    m_currentBean.setVerdict(verdict);
                    if(verdict != PhotoVoteVerdict.NEUTRAL_NEGATIVE) {
                        // Notify that the user voted - need to reload
                        Intent i = new Intent(BroadcastTypes.BROADCAST_PHOTOVOTE);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
                    }
                }
                try {
                    int remaining = 0;
                    if (needsCount) {
                        remaining = photoVoteAdapter.getRemainingPhotoVotes();
                        if (remaining == 0) {
                            return new Holder<PhotoVoteBean>(new NoMorePhotoVoteBean());
                        }
                    }

                    PhotoVoteBean vote = photoVoteAdapter.getNextPhotoVoteAndVote(m_currentBean);
                    if (needsCount) {
                        vote.setVotesRemaining(remaining - 1);
                    } else {
                        vote.setVotesRemaining(m_remaining - 1); // Cached version
                    }
                    return new Holder<>(vote);
                } catch (Exception e) {
                    return new Holder<>(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Holder<PhotoVoteBean>> arg0, Holder<PhotoVoteBean> result) {
        m_loadFinished.set(true);
        PhotoVoteBean voteBean = result.getContainedObject();
        boolean success = voteBean != null;
        boolean nomore = CompareUtility.equals(0, m_remaining) || voteBean instanceof NoMorePhotoVoteBean;

        if (success && nomore) {
            // None left!
            getView().findViewById(R.id.photovote_overlay_nomore).setVisibility(View.VISIBLE);
        }
        if (getView() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            m_box.setVisibility(success ? View.VISIBLE : View.INVISIBLE);

            if (success) {
                m_currentBean = voteBean;
                if (m_currentBean.getVotesRemaining() != null) {
                    m_remaining = m_currentBean.getVotesRemaining();
                }
                // Get image
                URL url = createPictureUrl(m_currentBean);
                if (url == null) {
                    // TODO Handle
                    return;
                }
                m_remainingLbl.setText(getString(R.string.PhotoVote_Xmore, m_remaining));

                Picasso.with(getActivity()).load(url.toString()).placeholder(R.drawable.picture_placeholder).
                        error(R.drawable.no_image).into(m_imgV);
            } else {
                Exception e = result.getException();
                if (e instanceof WrongCredentialsException) {
                    PersistantModel.getInstance().handleWrongCredentials(getActivity());
                } else if (e instanceof IOException) {
                    Toast.makeText(getActivity(), R.string.ErrorOccurred_NoNetwork, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.AnErrorOccurred, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<PhotoVoteBean>> arg0) {
    }

    private URL createPictureUrl(PhotoVoteBean b) {
        if (b == null) {
            return null;
        }
        int max = Math.max(b.getMaxHeight(), b.getMaxWidth());
        UserPictureSize picSize = UserPictureSize.getPictureSizeForPx(Math.min(max, maxDisplaySideLengthPx)); // Use min: Only load what possible
        if (b.getPictureUrlPrefix() == null) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Got no URL for fotovote. Bean was " + b.toString());
            }
        }
        try {
            return new URL(b.getPictureUrlPrefix() + picSize.getAPIValue());
        } catch (MalformedURLException e) {
            Log.w(TAG, "Could not build the URL for the picture in photovote", e);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, b.toString());
            }
            return null;
        }
    }

    private void doShowPins() {
        if (m_currentBean != null && m_loadFinished.get()) {
            final int h = m_imgV.getHeight();
            final int w = m_imgV.getWidth();

            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p.leftMargin = (int) (w * m_currentBean.getPosX());
            p.topMargin = (int) (h * m_currentBean.getPosY());
            m_pinLbl.setLayoutParams(p);

            int tmp = m_remainingLbl.getVisibility();
            m_remainingLbl.setVisibility(m_pinLbl.getVisibility());
            m_pinLbl.setVisibility(tmp);
        }
    }
}
