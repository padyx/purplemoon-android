package ch.defiant.purplesky.enums;

import android.content.res.Resources;

import ch.defiant.purplesky.R;

public enum Sexuality {

    /**
     * Sexuality 'heterosexual' for a male.
     */
    HETEROSEXUAL,
    /**
     * Sexuality 'homosexual'
     */
    HOMOSEXUAL,
    /**
     * Sexuality 'bisexual'
     */
    BISEXUAL;


    public String getLocalizedString(Resources r, Gender g) {
        switch (this){
            case HETEROSEXUAL:
                return r.getString(R.string.SexualityHetero);
            case HOMOSEXUAL:
                switch(g){
                    case MALE:
                        return r.getString(R.string.SexualityGay);
                    case FEMALE:
                        return r.getString(R.string.SexualityLesbian);
                }
            case BISEXUAL:
                return r.getString(R.string.SexualityBisexual);
        }
        throw new IllegalArgumentException("Unknown sexuality/gender combination: "+this+"/"+g);
    }

}
