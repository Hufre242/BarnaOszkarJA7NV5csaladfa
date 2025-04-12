package com.example.barnaoszkarja7nv5csaladfa;


public class FamilyMember {
    private String id;
    private String name;
    private String birthDate;
    private String relationship;

    public FamilyMember() {
        // szükséges a Firestore-hoz
    }

    public FamilyMember(String id, String name, String birthDate, String relationship) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.relationship = relationship;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBirthDate() { return birthDate; }
    public String getRelationship() { return relationship; }
}
