package ch.defiant.purplesky.core;

/**
 * @author Patrick Bänziger
 */
final class Modules {

    public static Object[] list(PurpleSkyApplication appContext){
        return new Object[] {
            new PurpleSkyModule(appContext)
        };
    }

    private Modules() {
        // No instances.
    }

}
