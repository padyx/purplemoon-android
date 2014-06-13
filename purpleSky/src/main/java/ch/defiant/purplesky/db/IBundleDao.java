package ch.defiant.purplesky.db;

import android.os.Bundle;

import ch.defiant.purplesky.core.UserSearchOptions;

/**
 * Stores bundles in the database.
 * @author Patrick Bänziger
 * @since 1.0.1
 */
public interface IBundleDao {

    public void store(Bundle b, String owner );

    public Bundle restore(String owner);
}
