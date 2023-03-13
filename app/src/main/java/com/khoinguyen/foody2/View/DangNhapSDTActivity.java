package com.khoinguyen.foody2.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.khoinguyen.foody2.Controller.DangKyController;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DangNhapSDTActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edSDT, edOTP;
    Button btnGuiMaOTP, btnXacNhanOTP;
    FirebaseAuth firebaseAuth;

    String storedVerificationId;
    PhoneAuthProvider.ForceResendingToken storedForceResendingToken;
    String phoneNumber;

    SharedPreferences sharedPreferences;

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

        btnGuiMaOTP.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuiMaOTP:
                if (edSDT.getText().toString().trim().length() == 0) {
                    Toast.makeText(DangNhapSDTActivity.this, getString(R.string.yeucaunhapsdtdangnhap), Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = "+84" + edSDT.getText().toString().substring(1);
                verifyPhoneNumber();
                break;

            case R.id.btnXacNhanOTP:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential
                        (storedVerificationId, edOTP.getText().toString());
                authenticateUser(credential);

                break;
        }
    }

    public void verifyPhoneNumber () {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(DangNhapSDTActivity.this)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authenticateUser(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(DangNhapSDTActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
                        storedForceResendingToken = forceResendingToken;

                        edSDT.setVisibility(View.GONE);
                        btnGuiMaOTP.setVisibility(View.GONE);

                        edOTP.setVisibility(View.VISIBLE);
                        btnXacNhanOTP.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    public void authenticateUser(PhoneAuthCredential phoneAuthCredential) {

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    ThanhVienModel thanhVienModel = new ThanhVienModel();
                    thanhVienModel.setSodienthoai(phoneNumber);

                    DangKyController dangKyController = new DangKyController();
                    dangKyController.ThemThongTinThanhVienController(thanhVienModel, user.getUid());

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
                Toast.makeText(DangNhapSDTActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


}
