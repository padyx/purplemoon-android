package ch.defiant.purplesky.db.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.defiant.purplesky.constants.DatabaseConstants;
import ch.defiant.purplesky.db.IBundleDao;
import ch.defiant.purplesky.db.IDatabaseProvider;

import ch.defiant.purplesky.constants.DatabaseConstants.BundleStoreTable;

/**
 * Implementation to store bundles in the database.
 * @author Patrick Bänziger
 * @since 1.0.1
 */
@Singleton
class BundleDao implements IBundleDao {

    private static final String TAG = BundleDao.class.getSimpleName();

    private static final String TYPE_CHAR = "C";
    private static final String TYPE_INT = "I";
    private static final String TYPE_FLOAT = "F";
    private static final String TYPE_DOUBLE= "D";

    private final IDatabaseProvider dbProvider;

    @Inject
    public BundleDao(IDatabaseProvider db){
        this.dbProvider = db;
    }

    @Override
    public void store(Bundle b, String owner) {
        if(b==null){
            return;
        }
        SQLiteDatabase db = dbProvider.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(BundleStoreTable.TABLE_BUNDLESTORE, BundleStoreTable.BUNDLESTORE_OWNER + "=?", new String[]{owner});

            Set<String> keys = b.keySet();
            for(String k : keys) {
                Object value = b.get(k);
                if(value == null){
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put(BundleStoreTable.BUNDLESTORE_OWNER, owner);
                values.put(BundleStoreTable.BUNDLESTORE_KEY, k);
                if(value instanceof String){
                    values.put(BundleStoreTable.BUNDLESTORE_TYPE, TYPE_CHAR);
                    values.put(BundleStoreTable.BUNDLESTORE_CVALUE, (String)value);
                } else if (value instanceof Integer) {
                    values.put(BundleStoreTable.BUNDLESTORE_TYPE, TYPE_INT);
                    values.put(BundleStoreTable.BUNDLESTORE_NVALUE, (Integer) value);
                } else if (value instanceof  Float) {
                    values.put(BundleStoreTable.BUNDLESTORE_TYPE, TYPE_FLOAT);
                    values.put(BundleStoreTable.BUNDLESTORE_FVALUE, (Float) value);
                } else if (value instanceof Double) {
                    values.put(BundleStoreTable.BUNDLESTORE_TYPE, TYPE_DOUBLE);
                    values.put(BundleStoreTable.BUNDLESTORE_FVALUE, (Double) value);
                } else {
                    Log.w(TAG, "Could not store object from bundle. Unsupported type: " + value.getClass().getCanonicalName());
                }
                db.insert(BundleStoreTable.TABLE_BUNDLESTORE, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public Bundle restore(String owner) {
        Cursor cursor = null;
        SQLiteDatabase db = dbProvider.getReadableDatabase();
        try {
            cursor = db.query(false, BundleStoreTable.TABLE_BUNDLESTORE,
                    new String[]{
                            BundleStoreTable.BUNDLESTORE_KEY,      // 0
                            BundleStoreTable.BUNDLESTORE_TYPE,     // 1
                            BundleStoreTable.BUNDLESTORE_CVALUE,   // 2
                            BundleStoreTable.BUNDLESTORE_NVALUE,   // 3
                            BundleStoreTable.BUNDLESTORE_FVALUE},  // 4
                    BundleStoreTable.BUNDLESTORE_OWNER + "=?",
                    new String[]{owner}, null, null, null, null
            );
            Bundle b = new Bundle();
            while (cursor.moveToNext()) {
                String key = cursor.getString(0);
                String type = cursor.getString(1);
                if (TYPE_CHAR.equals(type)) {
                    b.putString(key, cursor.getString(2));
                } else if (TYPE_INT.equals(type)) {
                    b.putInt(key, cursor.getInt(3));
                } else if (TYPE_FLOAT.equals(type)) {
                    b.putFloat(key, cursor.getFloat(4));
                } else if (TYPE_DOUBLE.equals(type)) {
                    b.putDouble(key, cursor.getDouble(4));
                }
            }
            return b;
        } finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
    }

}
