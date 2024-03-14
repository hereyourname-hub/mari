package com.example.bodyboost;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WaterActivity extends AppCompatActivity {

    private static final String WATER_AMOUNT_KEY = "water_amount";
    private static final int DAILY_GOAL = 2000; // Дневная цель потребления воды (в мл)
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    private TextView water;
    private EditText editTextNumber;
    private TextView textView2;
    private TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);



        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBar);

        int consumedWaterAmount = sharedPreferences.getInt(WATER_AMOUNT_KEY, 0);
        updateUI(consumedWaterAmount);



        // Инициализируем элементы интерфейса
        water = findViewById(R.id.water);
        editTextNumber = findViewById(R.id.editTextNumber);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        // Устанавливаем обработчики событий на textView2 (добавление) и textView3 (вычитание)
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater();
            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractWater();
            }
        });
    }

    // Метод для добавления воды
    private void addWater() {
        String value = editTextNumber.getText().toString();
        if (!value.isEmpty()) {
            int amountToAdd = Integer.parseInt(value);
            int currentAmount = getCurrentWaterAmount();
            int newAmount = currentAmount + amountToAdd;
            water.setText(String.valueOf(newAmount));
        }
    }

    // Метод для вычитания воды
    private void subtractWater() {
        String value = editTextNumber.getText().toString();
        if (!value.isEmpty()) {
            int amountToSubtract = Integer.parseInt(value);
            int currentAmount = getCurrentWaterAmount();
            int newAmount = currentAmount - amountToSubtract;
            if (newAmount < 0) {
                newAmount = 0; // чтобы избежать отрицательных значений
            }
            water.setText(String.valueOf(newAmount));
        }
    }

    // Метод для получения текущего количества воды
    private int getCurrentWaterAmount() {
        String currentValue = water.getText().toString();
        if (!currentValue.isEmpty()) {
            return Integer.parseInt(currentValue);
        }
        return 0;
    }


    private void updateWaterAmount(int waterAmount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WATER_AMOUNT_KEY, waterAmount);
        editor.apply();

        // Рассчитываем процент заполнения прогресс-бара
        float percentage = (waterAmount / (float) DAILY_GOAL) * 100;

        // Устанавливаем новое значение прогресса прогресс-бара
        progressBar.setProgress((int) percentage);

        // Обновляем UI
        updateUI(waterAmount);
    }

    private void updateUI(int waterAmount) {
        int percentage = (int) ((waterAmount / (float) DAILY_GOAL) * 100);

        TextView textViewPercentage = findViewById(R.id.textViewPercentage);
        TextView textViewAmount = findViewById(R.id.tvConsumedWater);

        textViewPercentage.setText(percentage + "%");
        textViewAmount.setText("Amount: " + waterAmount + "ml");

        // Обновление прогресса ProgressBar
        progressBar.setProgress(percentage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.water_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.water_del) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete all data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Обнуление данных
                        updateWaterAmount(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Отмена действия удаления
                        dialog.dismiss();
                    }
                });
        // Создание диалогового окна и его отображение
        builder.create().show();
    }
}
