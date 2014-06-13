package ch.defiant.purplesky.db.internal;

import android.database.sqlite.SQLiteDatabase;

import ch.defiant.purplesky.core.DBHelper;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.db.IBundleDao;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since 1.0.1
 */
@Module(library = true)
public class DatabaseModule {

    @Provides
    public SQLiteDatabase provideDatabase(){
        return DBHelper.fromContext(PurpleSkyApplication.get()).getWritableDatabase();
    }

    @Provides
    public IBundleDao provideBundleDao(SQLiteDatabase db){
        return new BundleDao(db);
    }

}
