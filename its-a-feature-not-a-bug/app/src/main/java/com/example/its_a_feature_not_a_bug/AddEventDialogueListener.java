package com.example.its_a_feature_not_a_bug;

/**
 * Interface definition for a callback to be invoked when an event is added.
 * Classes that wish to handle the event of adding a new event should implement this interface.
 */
public interface AddEventDialogueListener {
    /**
     * Called when a new event is added.
     * @param event The new event that was added.
     */
    void addEvent(Event event);
}
