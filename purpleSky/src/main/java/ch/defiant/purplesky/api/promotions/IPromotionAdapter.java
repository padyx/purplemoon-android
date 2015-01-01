package ch.defiant.purplesky.api.promotions;

import java.io.IOException;
import java.util.List;

import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * Promotion API Adapter
 *
 * @author Patrick Bänziger
 */
public interface IPromotionAdapter {

    List<Promotion> getActivePromotions() throws IOException, PurpleSkyException;

    Event getEvent(int eventId) throws IOException, PurpleSkyException;

    EventRegistrationResult register(int eventId, Event.RegistrationVisibility visibility)  throws IOException, PurpleSkyException;

 }
