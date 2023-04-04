package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.khoinguyen.foody2.Controller.DangKyController;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DangNhapSDTActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edSDT, edOTP;
    Button btnGuiMaOTP, btnXacNhanOTP;
    LinearLayout khungGuiLaiOTP;
    TextView tvGuiLaiOTP, tv_countdown;
    FirebaseAuth firebaseAuth;

    String storedVerificationId;
    PhoneAuthProvider.ForceResendingToken storedForceResendingToken;
    String phoneNumber;

    SharedPreferences sharedPreferences;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangnhap_sodienthoai);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        edSDT = findViewById(R.id.edSDT);
        btnGuiMaOTP = findViewById(R.id.btnGuiMaOTP);
        edOTP = findViewById(R.id.edOTP);
        btnXacNhanOTP = findViewById(R.id.btnXacNhanOTP);
        khungGuiLaiOTP = findViewById(R.id.khungGuiLaiOTP);
        tvGuiLaiOTP = findViewById(R.id.tvGuiLaiOTP);
        tv_countdown = findViewById(R.id.tv_countdown);

        btnGuiMaOTP.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);
        tvGuiLaiOTP.setOnClickListener(this);

        validateData();

    }

    private void startCountdownTimer() {
        // Disable the "Resend OTP" button during the countdown
        tvGuiLaiOTP.setEnabled(false);
        tvGuiLaiOTP.setTextColor(getColor(R.color.transparentBackgroundDangNhap));

        // Start the countdown timer for 60 seconds
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                tv_countdown.setText(String.format(Locale.getDefault(), getString(R.string.thoigianguilai) + ": %02d" + "s", seconds));
            }

            @Override
            public void onFinish() {
                // Enable the "Resend OTP" button when the timer finishes
                tvGuiLaiOTP.setEnabled(true);
                tv_countdown.setText("");

                tvGuiLaiOTP.setTextColor(Color.parseColor("#0CD1EA"));
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownTimer.start();
                Log.d("kiemtra", "waiting");
            }
        }, 30000);
    }

    private void validateData () {
        edSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 10) {
                    btnGuiMaOTP.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 10) {
                    btnGuiMaOTP.setVisibility(View.VISIBLE);
                }
            }
        });


        edOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 6) {
                    btnXacNhanOTP.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    btnXacNhanOTP.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuiMaOTP:
                phoneNumber = "+84" + edSDT.getText().toString().substring(1);
                sendOTP();

                // Start the countdown timer
                startCountdownTimer();
                break;

            case R.id.btnXacNhanOTP:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential
                        (storedVerificationId, edOTP.getText().toString());
                authenticateUser(credential);

                break;

            case R.id.tvGuiLaiOTP:
                reSendOTP();

                // Start the countdown timer
                startCountdownTimer();
                break;
        }
    }

    public void sendOTP () {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(DangNhapSDTActivity.this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(DangNhapSDTActivity.this, "Error when sending OTP", Toast.LENGTH_LONG).show();
                        edSDT.setVisibility(View.GONE);
                        btnGuiMaOTP.setVisibility(View.GONE);

                        edOTP.setVisibility(View.VISIBLE);
                        khungGuiLaiOTP.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
//                        storedForceResendingToken = forceResendingToken;

                        edSDT.setVisibility(View.GONE);
                        btnGuiMaOTP.setVisibility(View.GONE);

                        edOTP.setVisibility(View.VISIBLE);
                        khungGuiLaiOTP.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void reSendOTP () {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(DangNhapSDTActivity.this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setForceResendingToken(storedForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(DangNhapSDTActivity.this, "Error when resending OTP", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
                        storedForceResendingToken = forceResendingToken;
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authenticateUser(PhoneAuthCredential phoneAuthCredential) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    ThanhVienModel thanhVienModel = new ThanhVienModel();

                    // Số điện thoại này đã được đăng nhập trước đó
                    if (user.getDisplayName() == null && user.getPhotoUrl() == null) {
                        thanhVienModel.setSodienthoai("0" + phoneNumber.substring(3));
                        thanhVienModel.setHoten("");
                        thanhVienModel.setHinhanh("user.png");
                        thanhVienModel.setEmail("");
                        thanhVienModel.setDiachi("");
                        thanhVienModel.setNgaytao(currentDate);

                        DangKyController dangKyController = new DangKyController();
                        dangKyController.ThemThongTinThanhVienController(thanhVienModel, user.getUid());
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("mauser", user.getUid());
                    editor.commit();

                    Intent iTrangChu = new Intent(DangNhapSDTActivity.this, TrangChuActivity.class);
                    startActivity(iTrangChu);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DangNhapSDTActivity.this, getString(R.string.otpkhongdung), Toast.LENGTH_LONG).show();
            }
        });


    }


}
