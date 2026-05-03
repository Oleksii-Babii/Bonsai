package org.tudublin.bonsaiapp.model;

public class Tree {
    private int id;
    private String nickname;
    private int age;
    private double height;
    private String lastWateredDate;
    private String notes;
    private String imageUrl;
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

    public int getSpeciesId() { return speciesId; }
    public void setSpeciesId(int speciesId) { this.speciesId = speciesId; }

    public Species getSpecies() { return species; }
    public void setSpecies(Species species) { this.species = species; }
}
