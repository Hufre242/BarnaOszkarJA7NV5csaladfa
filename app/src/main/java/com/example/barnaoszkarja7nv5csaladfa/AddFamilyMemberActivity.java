package com.example.barnaoszkarja7nv5csaladfa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.UUID;

public class AddFamilyMemberActivity extends AppCompatActivity {

    private EditText nameInput, birthDateInput;
    private Spinner relationshipSpinner;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);

        nameInput = findViewById(R.id.nameEditText);
        birthDateInput = findViewById(R.id.birthDateEditText);
        relationshipSpinner = findViewById(R.id.relationshipSpinner);

        db = FirebaseFirestore.getInstance();

        // Spinner feltöltése
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.relationship_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationshipSpinner.setAdapter(adapter);

        // Dátumválasztó felugró ablak
        birthDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                String dateString = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                birthDateInput.setText(dateString);
            }, year, month, day).show();
        });

        findViewById(R.id.saveButton).setOnClickListener(v -> saveMember());
    }

    private void saveMember() {
        String name = nameInput.getText().toString().trim();
        String birthDate = birthDateInput.getText().toString().trim();
        String relationship = relationshipSpinner.getSelectedItem().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(relationship)) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();
        FamilyMember newMember = new FamilyMember(id, name, birthDate, relationship);

        db.collection("family_members")
                .document(id)
                .set(newMember)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Családtag elmentve!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddFamilyMemberActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
