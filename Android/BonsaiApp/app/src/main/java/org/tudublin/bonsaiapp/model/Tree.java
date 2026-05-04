package org.tudublin.bonsaiapp.model;

import java.util.Objects;

public class Tree {
    private int id;
    private String nickname;
    private int age;
    private double height;
    private String lastWateredDate;
    private String notes;
    private String imageUrl;
    private String imageData;
    private int speciesId;
    private Species species;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getLastWateredDate() { return lastWateredDate; }
    public void setLastWateredDate(String lastWateredDate) { this.lastWateredDate = lastWateredDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }

    public int getSpeciesId() { return speciesId; }
    public void setSpeciesId(int speciesId) { this.speciesId = speciesId; }

    public Species getSpecies() { return species; }
    public void setSpecies(Species species) { this.species = species; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tree)) return false;
        Tree other = (Tree) o;
        return id == other.id
                && age == other.age
                && Double.compare(height, other.height) == 0
                && speciesId == other.speciesId
                && Objects.equals(nickname, other.nickname)
                && Objects.equals(notes, other.notes)
                && Objects.equals(imageData, other.imageData)
                && Objects.equals(imageUrl, other.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, age, height, notes, imageData, imageUrl, speciesId);
    }
}
