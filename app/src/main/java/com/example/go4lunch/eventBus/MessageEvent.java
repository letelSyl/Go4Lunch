package com.example.go4lunch.eventBus;

import com.google.android.libraries.places.api.model.Place;

public class MessageEvent {

    public final Place message;

    public MessageEvent(Place message){
        this.message = message;
    }
}
