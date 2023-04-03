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
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class KhoiPhucMatKhauActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutKhoiPhucMK1, layoutKhoiPhucMK2, layoutNhapMatKhauMoi, khungGuiLaiOTP;;
    TextView txtDangKyKP, tvGuiLaiOTP, tv_countdown;;
    Button btnGuiMail, btnGuiOTP, btnXacNhanOTP, btnXacNhanMatKhauMoi;
    EditText edEmailKP, edPhone, edOTP, edMatKhauMoi, edNhapLaiMatKhauMoi;
    List<ThanhVienModel> thanhVienModelList;
    SharedPreferences sharedPreferences;
    CountDownTimer countDownTimer;

    FirebaseAuth firebaseAuth;
    String storedVerificationId;
    PhoneAuthProvider.ForceResendingToken storedForceResendingToken;
    String phoneNumber;
    FirebaseUser userForgetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_quenmatkhau);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);
        thanhVienModelList = new ArrayList<>();

        layoutKhoiPhucMK1 = findViewById(R.id.layoutKhoiPhucMK1);
        layoutKhoiPhucMK2 = findViewById(R.id.layoutKhoiPhucMK2);
        layoutNhapMatKhauMoi = findViewById(R.id.layoutNhapMatKhauMoi);
        txtDangKyKP = findViewById(R.id.txtDangKyKP);
        btnGuiMail = findViewById(R.id.btnGuiEmailKP);
        edEmailKP = findViewById(R.id.edEmailKP);
        edPhone = findViewById(R.id.edPhone);
        btnGuiOTP = findViewById(R.id.btnGuiOTP);
        edOTP = findViewById(R.id.edOTP);
        btnXacNhanOTP = findViewById(R.id.btnXacNhanOTP);
        btnXacNhanMatKhauMoi = findViewById(R.id.btnXacNhanMatKhauMoi);
        edMatKhauMoi = findViewById(R.id.edMatKhauMoi);
        edNhapLaiMatKhauMoi = findViewById(R.id.edNhapLaiMatKhauMoi);
        khungGuiLaiOTP = findViewById(R.id.khungGuiLaiOTP);
        tvGuiLaiOTP = findViewById(R.id.tvGuiLaiOTP);
        tv_countdown = findViewById(R.id.tv_countdown);

        btnGuiMail.setOnClickListener(this);
        btnGuiOTP.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);
        btnXacNhanMatKhauMoi.setOnClickListener(this);
        txtDangKyKP.setOnClickListener(this);
        tvGuiLaiOTP.setOnClickListener(this);

        LayThongTinUser();

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
        }, 25000);
    }

    private void validateData() {
        edEmailKP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) {
                    edPhone.setEnabled(false);
                    btnGuiOTP.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    edPhone.setEnabled(true);
                    btnGuiOTP.setEnabled(true);
                }
            }
        });


        edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && charSequence.length() < 10) {
                    edEmailKP.setEnabled(false);
                    btnGuiMail.setEnabled(false);
                    btnGuiOTP.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    edEmailKP.setEnabled(true);
                    btnGuiMail.setEnabled(true);
                } else if (editable.length() == 10) {
                    btnGuiOTP.setEnabled(true);
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
                    btnXacNhanOTP.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    btnXacNhanOTP.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuiEmailKP:
                String email = edEmailKP.getText().toString();
                if (KiemTraEmail(email)) {
                    GuiEmailKhoiPhucMK(email);
                } else {
                    Toast.makeText(this, getString(R.string.thongbaoemailkhonghople), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btnGuiOTP:

                // User đã đăng nhập với số điện thoại này trước đó
                if (GuiOTPKhoiPhucMK()) {
                    sendOTP(phoneNumber);

                    // Start the countdown timer
                    startCountdownTimer();
                }

                // Số điện thoại này chưa có trong hệ thống
                else {
                    Toast.makeText(this, getString(R.string.sdtchuadk), Toast.LENGTH_SHORT).show();
                    edPhone.setText("");
                }
                break;

            case R.id.tvGuiLaiOTP:
                reSendOTP();

                // Start the countdown timer
                startCountdownTimer();
                break;


            case R.id.btnXacNhanOTP:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential
                        (storedVerificationId, edOTP.getText().toString());
                authenticateUser(credential);

                break;


            case R.id.btnXacNhanMatKhauMoi:
                if (edMatKhauMoi.getText().toString().trim().length() == 0 ||
                        edNhapLaiMatKhauMoi.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, getString(R.string.yeucaunhapthongtin), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (edMatKhauMoi.getText().toString().trim().
                            compareTo(edNhapLaiMatKhauMoi.getText().toString().trim()) != 0) {
                        Toast.makeText(this, getString(R.string.xacnhanmatkhaumoikhongdung), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String matkhaumoi = edMatKhauMoi.getText().toString().trim();
                updatePassword(matkhaumoi);
                break;


            case R.id.txtDangKyKP:
                Intent iDangKy = new Intent(this, DangKyActivity.class);
                startActivity(iDangKy);
                break;
        }
    }

    private void GuiEmailKhoiPhucMK(String email) {
        // Kiểm tra tài khoản đã tồn tại hay chưa
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();

                    if (signInMethods != null && !signInMethods.isEmpty()) {
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.thongbaoguimailthanhcong), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });

                    } else {
                        // Nếu chưa có thì chuyển sang trang đăng ký
                        Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.emailchuadk), Toast.LENGTH_SHORT).show();
                        edEmailKP.setText("");
                    }
                } else {
                    Toast.makeText(KhoiPhucMatKhauActivity.this, "Can't get list user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean GuiOTPKhoiPhucMK() {
        phoneNumber = "+84" + edPhone.getText().toString().substring(1);

        for (ThanhVienModel value : thanhVienModelList) {
            if (value.getSodienthoai().compareTo(edPhone.getText().toString()) == 0) {
                return true;
            }
        }
        return false;
    }


    public void sendOTP(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(KhoiPhucMatKhauActivity.this, "Error when send OTP", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
//                        storedForceResendingToken = forceResendingToken;

                        layoutKhoiPhucMK1.setVisibility(View.GONE);
                        layoutKhoiPhucMK2.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void reSendOTP () {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
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
                        Toast.makeText(KhoiPhucMatKhauActivity.this, "Error when resending OTP", Toast.LENGTH_LONG).show();
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
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userForgetPassword = authResult.getUser();
                if (userForgetPassword != null) {

                    // SĐT này được đăng nhập với phương thức đăng nhập bằng số điện thoại
                    if (userForgetPassword.getEmail() == null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mauser", userForgetPassword.getUid());
                        editor.commit();

                        Intent iTrangChu = new Intent(KhoiPhucMatKhauActivity.this, TrangChuActivity.class);
                        startActivity(iTrangChu);
                    }

                    // Số điện thoại này được liên kết với 1 tài khoản đăng nhập bằng email và pass
                    else {
                        layoutKhoiPhucMK1.setVisibility(View.GONE);
                        layoutKhoiPhucMK2.setVisibility(View.GONE);

                        layoutNhapMatKhauMoi.setVisibility(View.VISIBLE);
                    }


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.otpkhongdung), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePassword(String matkhaumoi) {
        userForgetPassword.updatePassword(matkhaumoi).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.khoiphucmatkhauthanhcong), Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KhoiPhucMatKhauActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    private boolean KiemTraEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void LayThongTinUser() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");
                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    ThanhVienModel thanhVienModel = valueThanhVien.getValue(ThanhVienModel.class);
                    thanhVienModelList.add(thanhVienModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
