package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.khoinguyen.foody2.R;

import java.util.concurrent.TimeUnit;

public class KhoiPhucMatKhauActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutKhoiPhucMK1, layoutKhoiPhucMK2, layoutNhapMatKhauMoi;
    TextView txtDangKyKP;
    Button btnGuiMail, btnGuiOTP, btnXacNhanOTP, btnXacNhanMatKhauMoi;
    EditText edEmailKP, edPhone, edOTP, edMatKhauMoi, edNhapLaiMatKhauMoi;

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

        btnGuiMail.setOnClickListener(this);
        btnGuiOTP.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);
        btnXacNhanMatKhauMoi.setOnClickListener(this);
        txtDangKyKP.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuiEmailKP:
                String email = edEmailKP.getText().toString();
                if (KiemTraEmail(email)) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.thongbaoguimailthanhcong), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(this, getString(R.string.thongbaoemailkhonghople), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btnGuiOTP:
                if (edPhone.getText().toString().trim().length() == 0) {
                    Toast.makeText(KhoiPhucMatKhauActivity.this, getString(R.string.yeucausdt), Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = "+84" + edPhone.getText().toString().substring(1);
                verifyPhoneNumber(phoneNumber);
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
                }
                else {
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

    public void verifyPhoneNumber (String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(KhoiPhucMatKhauActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
                        storedForceResendingToken = forceResendingToken;

                        layoutKhoiPhucMK1.setVisibility(View.GONE);
                        layoutKhoiPhucMK2.setVisibility(View.VISIBLE);
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
                    layoutKhoiPhucMK1.setVisibility(View.GONE);
                    layoutKhoiPhucMK2.setVisibility(View.GONE);

                    layoutNhapMatKhauMoi.setVisibility(View.VISIBLE);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KhoiPhucMatKhauActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
}
