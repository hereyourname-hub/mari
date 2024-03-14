package com.example.bodyboost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvWaterAmount, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupMenuNavigation();

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email1);
        tvWaterAmount = findViewById(R.id.water_prof);

        SharedData sharedData = SharedData.getInstance();

        // Получаем значение totalCalories из разделяемого класса
        int totalCaloriesValue = sharedData.getTotalCalories();

        // Находим элемент food_prof и устанавливаем полученное значение
        TextView foodProfTextView = findViewById(R.id.food_prof);
        foodProfTextView.setText(totalCaloriesValue + " ");



        // Получаем сохраненное значение consumedWater из SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int tvConsumedWater = preferences.getInt("water_amount", 0);
        tvWaterAmount.setText(tvConsumedWater + " ml");

        // Получаем сохраненное значение previousSleepDuration из SharedPreferences
        String previousSleepDuration = preferences.getString("previousSleepDuration", "");

        // Если previousSleepDuration не установлено (т.е., равно ""), устанавливаем значение по умолчанию "0"
        if (previousSleepDuration.equals("")) {
            previousSleepDuration = "0";
        }

        // Находим элемент sleep_prof и устанавливаем полученное значение
        TextView sleepProfTextView = findViewById(R.id.sleep_prof);
        sleepProfTextView.setText(previousSleepDuration);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email1);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    tvName.setText("Hello! " + user.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_statistic) {
            // Открываем активити для статистики
            startActivity(new Intent(ProfileActivity.this, StatisticActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            // Открываем активити для выхода
            startActivity(new Intent(ProfileActivity.this, LogoutActivity.class));
            return true;
        }
        return false;
    }




    private void setupMenuNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    // Открываем активити HomeActivity
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.fit) {
                    // Открываем активити FitActivity
                    startActivity(new Intent(ProfileActivity.this, FitActivity.class));
                    return true;
                } else if (id == R.id.profile) {
                    // Открываем активити ProfileActivity
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
        // Установим элемент "profile" как выбранный
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.profile);
        menuItem.setChecked(true);

    }
}
