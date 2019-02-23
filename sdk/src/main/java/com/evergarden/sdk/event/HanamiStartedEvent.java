package com.evergarden.sdk.event;

public class HanamiStartedEvent implements HanamiInternaleEvent {
	
	private int timestamp;
	
	public HanamiStartedEvent(int timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
}
