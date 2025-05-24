package cuoiky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; 
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private Connection connection;

    public EventDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null for EventDAO");
        }
        this.connection = connection;
    }

    // Create
    public void addEvent(Event event) throws SQLException {
        String query = "INSERT INTO event (pet_name, eventDate, eventType, eventDetails) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getPetName());
            stmt.setDate(2, Date.valueOf(event.getEventDate())); 
            stmt.setString(3, event.getEventType());
            stmt.setString(4, event.getEventDetails());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                event.setEventId(generatedKeys.getInt(1));
            }
        }
    }

    // Read
    public Event getEventById(int id) throws SQLException {
        String query = "SELECT eventId, pet_name, eventDate, eventType, eventDetails FROM event WHERE eventId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Event(rs.getInt("eventId"), rs.getString("pet_name"),
                                 rs.getDate("eventDate").toLocalDate(), rs.getString("eventType"),
                                 rs.getString("eventDetails"));
            }
        }
        return null;
    }

    // Update
    public void updateEvent(Event event) throws SQLException {
        String query = "UPDATE event SET pet_name = ?, eventDate = ?, eventType = ?, eventDetails = ? WHERE eventId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getPetName());
            stmt.setDate(2, Date.valueOf(event.getEventDate()));
            stmt.setString(3, event.getEventType());
            stmt.setString(4, event.getEventDetails());
            stmt.setInt(5, event.getEventId());
            stmt.executeUpdate();
        }
    }

    // Delete
    public void deleteEvent(int eventId) throws SQLException {
        String query = "DELETE FROM event WHERE eventId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }

    // Get all events
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT eventId, pet_name, eventDate, eventType, eventDetails FROM event";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                events.add(new Event(rs.getInt("eventId"), rs.getString("pet_name"),
                                     rs.getDate("eventDate").toLocalDate(), rs.getString("eventType"),
                                     rs.getString("eventDetails")));
            }
        }
        return events;
    }

    // Search events
    public List<Event> searchEvents(String keyword) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT eventId, pet_name, eventDate, eventType, eventDetails FROM event WHERE eventType LIKE ? OR eventDetails LIKE ? OR pet_name LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            stmt.setString(3, searchKeyword); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(rs.getInt("eventId"), rs.getString("pet_name"),
                                     rs.getDate("eventDate").toLocalDate(), rs.getString("eventType"),
                                     rs.getString("eventDetails")));
            }
        }
        return events;
    }
}
