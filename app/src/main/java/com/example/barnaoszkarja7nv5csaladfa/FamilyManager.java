package com.example.barnaoszkarja7nv5csaladfa;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * A FamilyManager kezeli az összes Firestore műveletet a FamilyMember objektumokhoz.
 * Megvalósítja a CRUD-ot és komplex lekérdezéseket.
 */
public class FamilyManager {
    private final CollectionReference familyRef;

    /**
     * Inicializálja a Firestore kollekció referenciát.
     */
    public FamilyManager() {
        familyRef = FirebaseFirestore.getInstance().collection("family_members");
    }

    /**
     * Új családtag hozzáadása a Firestore-hoz.
     */
    public Task<Void> addMember(FamilyMember member) {
        return familyRef.document(member.getId()).set(member);
    }

    /**
     * Létező családtag frissítése a Firestore-ban.
     */
    public Task<Void> updateMember(FamilyMember member) {
        return familyRef.document(member.getId()).set(member);
    }

    /**
     * Családtag törlése Firestore-ból azonosító alapján.
     */
    public Task<Void> deleteMember(String id) {
        return familyRef.document(id).delete();
    }

    /**
     * Az összes családtag lekérdezése a Firestore-ból.
     */
    public Task<QuerySnapshot> getAllMembers() {
        return familyRef.get();
    }

    /**
     * Komplex lekérdezés 1: Minden gyermek (relationship = "Gyermek").
     */
    public Task<QuerySnapshot> getChildren() {
        return familyRef.whereEqualTo("relationship", "Gyermek").get();
    }

    /**
     * Komplex lekérdezés 2: Minden 2000 után született családtag.
     */
    public Task<QuerySnapshot> getBornAfter2000() {
        return familyRef.whereGreaterThan("birthDate", "2000-01-01").get();
    }

    /**
     * Komplex lekérdezés 3: Minden családtag, akinek a neve 'A'-val kezdődik.
     */
    public Task<QuerySnapshot> getNamesStartingWithA() {
        return familyRef.orderBy("name").startAt("A").endAt("A\uf8ff").get();
    }
} 