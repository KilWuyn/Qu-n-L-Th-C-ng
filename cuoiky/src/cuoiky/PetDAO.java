package cuoiky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {
    private Connection connection;

    public PetDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null for PetDAO");
        }
        this.connection = connection;
    }

    // Add
    public void addPet(Pet pet) throws SQLException {
    	String query = "INSERT INTO pet (name,owner,species,sex,birth,death) VALUES (?, ?, ?, ?, ?, ?)";
    	try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, pet.getName());
            stmt.setString(2, pet.getOwner());
            stmt.setString(3, pet.getSpecies());
            setNullableString(stmt, 4, pet.getSex()); // Xử lý sex có thể là null hoặc "\N"
            setNullableDateString(stmt, 5, pet.getBirth()); // Xử lý birth
            setNullableDateString(stmt, 6, pet.getDeath()); // Xử lý death
            stmt.executeUpdate();
        }
    }

    // Read
    public Pet getPetByName(String name) throws SQLException {
        String query = "SELECT * FROM pet WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Pet(rs.getString("name"), rs.getString("owner"), rs.getString("species"), rs.getString("sex"), rs.getString("birth"), rs.getString("death"));
            }
        }
        return null;
    }

    // Update
    public void updatePet(Pet pet) throws SQLException {
        String query = "UPDATE pet SET owner = ?, species = ?, sex = ?, birth = ?, death = ? WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, pet.getOwner());
            stmt.setString(2, pet.getSpecies());
            setNullableString(stmt, 3, pet.getSex());
            setNullableDateString(stmt, 4, pet.getBirth());
            setNullableDateString(stmt, 5, pet.getDeath());
            stmt.setString(6, pet.getName()); // Điều kiện WHERE
            stmt.executeUpdate();
        }
    }

    // Delete
    public void deletePet(String name) throws SQLException {
        String query = "DELETE FROM pet WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    // Get all pets
    public List<Pet> getAllPet() throws SQLException {
        List<Pet> pet = new ArrayList<>();
        String query = "SELECT * FROM pet";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
            	String birthStr = rs.getString("birth");
                String deathStr = rs.getString("death");
                pet.add(new Pet(rs.getString("name"), rs.getString("owner"), rs.getString("species"),
                               rs.getString("sex"), birthStr, deathStr));
            }
        }
        return pet;
    }
    private void setNullableString(PreparedStatement stmt, int parameterIndex, String value) throws SQLException {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("\\N")) {
            stmt.setNull(parameterIndex, Types.VARCHAR);
        } else {
            stmt.setString(parameterIndex, value);
        }
    }
    private void setNullableDateString(PreparedStatement stmt, int parameterIndex, String dateString) throws SQLException {
        if (dateString == null || dateString.trim().isEmpty() || dateString.equalsIgnoreCase("\\N")) {
            stmt.setNull(parameterIndex, Types.DATE);
        } else {
            try {
            	stmt.setDate(parameterIndex, java.sql.Date.valueOf(dateString));
            } catch (IllegalArgumentException e) {
            	 System.err.println("Lỗi định dạng ngày '" + dateString + "', sẽ đặt thành NULL. Chi tiết: " + e.getMessage());
                 stmt.setNull(parameterIndex, Types.DATE);
             }
         }
     }
 }
