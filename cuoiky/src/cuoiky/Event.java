package cuoiky;

import java.time.LocalDate;

public class Event {
	 private int eventId;
	    private String petName; 
	    private LocalDate eventDate;
	    private String eventType;
	    private String eventDetails;

	    public Event(int eventId, String petName, LocalDate eventDate, String eventType, String eventDetails) {
	        this.eventId = eventId;
	        this.petName = petName;
	        this.eventDate = eventDate;
	        this.eventType = eventType;
	        this.eventDetails = eventDetails;
	    }

	    public Event(String petName, LocalDate eventDate, String eventType, String eventDetails) {
	        this.petName = petName;
	        this.eventDate = eventDate;
	        this.eventType = eventType;
	        this.eventDetails = eventDetails;
	    }

	    public int getEventId() {
	        return eventId;
	    }

	    public void setEventId(int eventId) {
	        this.eventId = eventId;
	    }

	    public String getPetName() { 
	        return petName;
	    }

	    public void setPetName(String petName) { 
	        this.petName = petName;
	    }

	    public LocalDate getEventDate() {
	        return eventDate;
	    }

	    public void setEventDate(LocalDate eventDate) {
	        this.eventDate = eventDate;
	    }

	    public String getEventType() {
	        return eventType;
	    }

	    public void setEventType(String eventType) {
	        this.eventType = eventType;
	    }

	    public String getEventDetails() {
	        return eventDetails;
	    }

	    public void setEventDetails(String eventDetails) {
	        this.eventDetails = eventDetails;
	    }
}
