package com.example.barnaoszkarja7nv5csaladfa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<FamilyMember> memberList = new ArrayList<>();
    private FamilyAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.familyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FamilyAdapter(memberList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadFamilyMembers();

        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        findViewById(R.id.addButton).startAnimation(fade);



        findViewById(R.id.addButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddFamilyMemberActivity.class));
        });

    }

    private void loadFamilyMembers() {
        db.collection("family_members")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE", "Hiba:", error);
                        return;
                    }

                    memberList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        FamilyMember member = doc.toObject(FamilyMember.class);
                        memberList.add(member);
                    }
                    adapter.notifyDataSetChanged();
                });
    }


    private static class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.FamilyViewHolder> {
        private final List<FamilyMember> list;

        public FamilyAdapter(List<FamilyMember> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public FamilyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item, parent, false);
            return new FamilyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FamilyViewHolder holder, int position) {
            FamilyMember member = list.get(position);
            holder.name.setText(member.getName());
            holder.birthDate.setText("Született: " + member.getBirthDate());
            holder.relationship.setText("Viszony: " + member.getRelationship());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditFamilyMemberActivity.class);
                intent.putExtra("id", member.getId());
                intent.putExtra("name", member.getName());
                intent.putExtra("birthDate", member.getBirthDate());
                intent.putExtra("relationship", member.getRelationship());
                v.getContext().startActivity(intent);
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class FamilyViewHolder extends RecyclerView.ViewHolder {
            TextView name, birthDate, relationship;

            public FamilyViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.nameTextView);
                birthDate = itemView.findViewById(R.id.birthDateTextView);
                relationship = itemView.findViewById(R.id.relationshipTextView);
            }
        }
    }
}
