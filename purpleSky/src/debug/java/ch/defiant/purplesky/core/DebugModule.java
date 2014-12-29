package ch.defiant.purplesky.core;

import dagger.Module;

/**
 * @author Patrick Bänziger
 */
@Module(
        addsTo = PurpleSkyModule.class,
        overrides = true
)
public class DebugModule {

}
