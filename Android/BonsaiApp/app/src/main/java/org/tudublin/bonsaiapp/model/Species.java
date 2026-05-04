package org.tudublin.bonsaiapp.model;

import java.util.Objects;

public class Species {
    private int id;
    private String name;
    private String originCountry;
    private String description;
    private String difficultyLevel;
    private String imageUrl;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Species)) return false;
        Species other = (Species) o;
        return id == other.id
                && Objects.equals(name, other.name)
                && Objects.equals(originCountry, other.originCountry)
                && Objects.equals(difficultyLevel, other.difficultyLevel)
                && Objects.equals(imageUrl, other.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, originCountry, difficultyLevel, imageUrl);
    }
}
