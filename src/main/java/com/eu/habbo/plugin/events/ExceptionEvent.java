package com.eu.habbo.plugin.events;

import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.EventListener;

public class ExceptionEvent extends Event {

	boolean printStackTrace = true;
	Exception ex;
	private final Event event;

	public ExceptionEvent(Exception ex, Event event) {
		this.ex = ex;
		this.event = event;
	}
	
	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}
	
	public boolean isPrintStackTrace() {
		return printStackTrace;
	}
	
	public Exception getException() {
		return ex;
	}

	public Event getEvent() {
		return event;
	}
}
