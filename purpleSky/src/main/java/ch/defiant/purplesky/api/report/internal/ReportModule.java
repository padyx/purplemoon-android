package ch.defiant.purplesky.api.report.internal;

import javax.inject.Singleton;

import ch.defiant.purplesky.api.report.IReportAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
@Module (library = true)
public class ReportModule {

    @Provides
    @Singleton
    IReportAdapter provideApiAdapter(){
        return ReportAdapter.INSTANCE;
    }

}
