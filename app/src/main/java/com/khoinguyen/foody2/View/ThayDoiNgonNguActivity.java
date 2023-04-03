package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.khoinguyen.foody2.R;

import java.util.Locale;

public class ThayDoiNgonNguActivity extends AppCompatActivity implements View.OnClickListener {
    
    TextView vietnamese_textview, english_textview, txtHuy, txtChonNgonNgu;
    CheckBox vietnamese_checkbox, english_checkbox;
    SharedPreferences sharedPreferencesLanguage;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thaydoi_ngonngu);

        vietnamese_textview = findViewById(R.id.vietnamese_textview);
        english_textview = findViewById(R.id.english_textview);
        vietnamese_checkbox = findViewById(R.id.vietnamese_checkbox);
        english_checkbox = findViewById(R.id.english_checkbox);
        txtHuy = findViewById(R.id.txtHuy);
        txtChonNgonNgu = findViewById(R.id.txtChonNgonNgu);

        vietnamese_textview.setOnClickListener(this);
        english_textview.setOnClickListener(this);
        vietnamese_checkbox.setOnClickListener(this);
        english_checkbox.setOnClickListener(this);
        txtHuy.setOnClickListener(this);
        txtChonNgonNgu.setOnClickListener(this);

        sharedPreferencesLanguage = getSharedPreferences("language", MODE_PRIVATE);
        int languageCode = sharedPreferencesLanguage.getInt("selected_language", 0);

        if (languageCode == 0) {
            vietnamese_checkbox.setChecked(true);
        }
        else {
            english_checkbox.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vietnamese_textview:
                vietnamese_checkbox.setChecked(true);
                english_checkbox.setChecked(false);
                break;

            case R.id.vietnamese_checkbox:
                vietnamese_checkbox.setChecked(true);
                english_checkbox.setChecked(false);
                break;

            case R.id.english_textview:
                vietnamese_checkbox.setChecked(false);
                english_checkbox.setChecked(true);
                break;

            case R.id.english_checkbox:
                vietnamese_checkbox.setChecked(false);
                english_checkbox.setChecked(true);
                break;

            case R.id.txtHuy:
                finish();
                break;

            case R.id.txtChonNgonNgu:
                if (vietnamese_checkbox.isChecked()) {
                    saveSelectedLanguage(0);
                    changeLanguage("vi");
                }
                else {
                    saveSelectedLanguage(1);
                    changeLanguage("en");
                }

                break;
        }

    }

    private void changeLanguage (String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;

        // Yêu cầu app cập nhật lại file string
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        // Load lại trang
        Intent intent = new Intent(this, TrangChuActivity.class);
        startActivity(intent);

    }

    private void saveSelectedLanguage(int languageCode) {
        SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
        editor.putInt("selected_language", languageCode);
        editor.commit();
    }
}
