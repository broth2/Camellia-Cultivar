package com.camellia.models.specimens;

import com.camellia.models.characteristics.CharacteristicValueDTO;

import java.util.Set;

public class SpecimenDto {
    private long specimenId;
    private String owner;

    private Set<String> photos;
    private String address;
    private double latitude;
    private double longitude;
    private String garden;
    private Set<CharacteristicValueDTO> characteristicValues;
    private SpecimenType specimenType;
    private String photoUrl;

    public long getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(long specimenId) {
        this.specimenId = specimenId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<String> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<String> photos) {
        this.photos = photos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGarden() {
        return garden;
    }

    public void setGarden(String garden) {
        this.garden = garden;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Set<CharacteristicValueDTO> getCharacteristicValues() {
        return characteristicValues;
    }

    public void setCharacteristicValues(Set<CharacteristicValueDTO> characteristicValues) {
        this.characteristicValues = characteristicValues;
    }

    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }
}
