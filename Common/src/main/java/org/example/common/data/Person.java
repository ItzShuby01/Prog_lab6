package org.example.common.data;

import org.example.common.util.LocalDateTimeAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person>, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; // Unique ID, non-nullable, auto-generated
    private String name; // non-null, non-empty string
    private Coordinates coordinates;// non-null
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime creationDate; // non-null, auto-generated
    private Double height; // greater than 0
    private EyeColor eyeColor; // non-null
    private HairColor hairColor; // non-null
    private Country nationality; // nullable
    private Location location; // nullable


    public Person(Integer id, String name, Coordinates coordinates, LocalDateTime creationDate, Double height, EyeColor eyeColor, HairColor hairColor, Country nationality, Location location) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    // JAXB and serialization require a no-arg constructor
    public Person() {
        this.creationDate = LocalDateTime.now();
    }

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public Double getHeight() { return height; }
    public EyeColor getEyeColor() { return eyeColor; }
    public HairColor getHairColor() { return hairColor; }
    public Country getNationality() { return nationality; }
    public Location getLocation() { return location; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public void setHeight(Double height) { this.height = height; }
    public void setEyeColor(EyeColor eyeColor) { this.eyeColor = eyeColor; }
    public void setHairColor(HairColor hairColor) { this.hairColor = hairColor; }
    public void setNationality(Country nationality) { this.nationality = nationality; }
    public void setLocation(Location location) { this.location = location; }


    @Override
    public String toString() {
        String personString = "PERSON DATA \n";
        personString += "id: " + id + "\n";
        personString += "name: " + name + "\n";
        personString += coordinates.toString() + "\n";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedDate = creationDate.format(formatter);

        personString += "creation date: " + formattedDate + "\n";
        personString += "height: " + height + "\n";
        personString += "eye color: " + eyeColor + "\n";
        personString += "hair color: " + hairColor + "\n";
        personString += "Nationality: " + (nationality == null ? "Not specified" : nationality) + "\n";
        personString += "Location: " + (location == null ? "Not specified" : location.toString());
        return personString;
    }

    // Implementing equals and hashCode, important for collection operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id != null ? id.equals(person.id) : person.id == null; // Equality primarily by ID
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Person other) {
        return Double.compare(this.height, other.height);
    }
}
