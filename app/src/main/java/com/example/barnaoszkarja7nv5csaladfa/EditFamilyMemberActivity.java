package com.example.barnaoszkarja7nv5csaladfa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Családtag adatainak szerkesztésére szolgáló Activity.
 */
public class EditFamilyMemberActivity extends AppCompatActivity {

    private EditText nameInput, birthDateInput, relationshipInput;
    private FirebaseFirestore db;
    private String documentId;
    private ImageView trashAnimation;
    private FamilyManager familyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_family_member);

        nameInput = findViewById(R.id.editName);
        birthDateInput = findViewById(R.id.editBirthDate);
        relationshipInput = findViewById(R.id.editRelationship);
        trashAnimation = findViewById(R.id.trashAnimation);

        db = FirebaseFirestore.getInstance();
        familyManager = new FamilyManager();

        documentId = getIntent().getStringExtra("id");
        nameInput.setText(getIntent().getStringExtra("name"));
        birthDateInput.setText(getIntent().getStringExtra("birthDate"));
        relationshipInput.setText(getIntent().getStringExtra("relationship"));

        findViewById(R.id.updateButton).setOnClickListener(v -> updateMember());
        findViewById(R.id.deleteButton).setOnClickListener(v -> deleteMember());
    }

    /**
     * Családtag adatainak frissítése az adatbázisban.
     */
    private void updateMember() {
        String name = nameInput.getText().toString().trim();
        String birthDate = birthDateInput.getText().toString().trim();
        String relationship = relationshipInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(relationship)) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        FamilyMember updated = new FamilyMember(documentId, name, birthDate, relationship);
        familyManager.updateMember(updated)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Mentve!", Toast.LENGTH_SHORT).show();
                    
                    // Értesítés küldése
                    Intent notificationIntent = new Intent(this, NotificationService.class);
                    notificationIntent.putExtra("member_name", name);
                    startService(notificationIntent);
                    
                    finish();
                });
    }

    /**
     * Családtag törlése animációval és adatbázisból való eltávolítással.
     */
    private void deleteMember() {
        trashAnimation.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake);
        trashAnimation.startAnimation(anim);

        // 500ms után törlés, hogy az animáció lemenjen
        trashAnimation.postDelayed(() -> {
            familyManager.deleteMember(documentId)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Törölve!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }, 500);
    }
}

