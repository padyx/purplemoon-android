package ch.defiant.purplesky.constants;

import android.content.res.AssetManager;

import com.google.android.gms.games.GamesMetadata;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import ch.defiant.purplesky.core.PurpleSkyApplication;

public class SecureConstants {

    private static final String CONFIG_FILE = "purplesky.config";

    public static boolean containsValue(String key){
        ensureInitialized();
        return values.get().containsKey(key);
    }

    public static String get(String key){
        ensureInitialized();
        return values.get().get(key);
    }

    private static AtomicReference<Map<String, String>> values = new AtomicReference<Map<String, String>>();

    private static void ensureInitialized(){
        if(values.get() == null){
            initialize();
        }
    }

    private static synchronized void initialize(){
        if(values.get() == null){
            Map<String, String> map = new HashMap<String, String>();
            Properties properties = new Properties();
            InputStream stream = null;
            try {
                stream = PurpleSkyApplication.get().getAssets().open(CONFIG_FILE);
                properties.load(stream);
                for(Map.Entry<Object,Object> e : properties.entrySet()){
                    map.put((String)e.getKey(), (String)e.getValue());
                }
            } catch (FileNotFoundException e ){
                throw new RuntimeException("Could not open config asset", e);
            } catch (IOException e){
                throw new RuntimeException("Could not read config asset", e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
            values.set(map);
        }
    }

}
