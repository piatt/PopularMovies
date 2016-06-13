package com.piatt.udacity.popularmovies.event;

import com.piatt.udacity.popularmovies.model.MessageType;

import lombok.Getter;
import lombok.Setter;

public class MovieMessageEvent {
    @Getter @Setter private MessageType messageType;

    public MovieMessageEvent(MessageType messageType) {
        setMessageType(messageType);
    }
}