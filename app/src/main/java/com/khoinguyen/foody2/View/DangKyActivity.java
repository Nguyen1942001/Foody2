package com.khoinguyen.foody2.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutDK1, layoutDK2;
    Button btnDangKy, btnXacNhanOTP;
    EditText edEmailDK, edPasswordDK, edNhapLaiPasswordDK, edSoDienThoaiDK, edHoTenDK, edOTP;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DangKyController dangKyController;
    String storedVerificationId;
    PhoneAuthProvider.ForceResendingToken storedForceResendingToken;
    String sodienthoai;
    ThanhVienModel thanhVienModel;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        layoutDK1 = findViewById(R.id.layoutDK1);
        layoutDK2 = findViewById(R.id.layoutDK2);
        btnDangKy = findViewById(R.id.btnDangKy);
        edEmailDK = findViewById(R.id.edEmailDK);
        edPasswordDK = findViewById(R.id.edPasswordDK);
        edNhapLaiPasswordDK = findViewById(R.id.edNhapLaiPasswordDK);
        edSoDienThoaiDK = findViewById(R.id.edSoDienThoaiDK);
        edHoTenDK = findViewById(R.id.edHoTenDK);
        edOTP = findViewById(R.id.edOTP);
        btnXacNhanOTP = findViewById(R.id.btnXacNhanOTP);

        btnDangKy.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDangKy:

                if (edEmailDK.getText().toString().trim().length() <= 0
                        || edSoDienThoaiDK.getText().toString().trim().length() <= 0
                        || edHoTenDK.getText().toString().trim().length() <= 0
                        || edPasswordDK.getText().toString().trim().length() <= 0) {

                    if (edPasswordDK.getText().toString().compareTo(edNhapLaiPasswordDK.getText().toString()) != 0) {
                        Toast.makeText(this, getString(R.string.thongbaonhaplaimatkhau), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String thongbaoloi = getString(R.string.thongbaoloidangky);
                    Toast.makeText(this, thongbaoloi, Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = edEmailDK.getText().toString();
                sodienthoai = "+84" + edSoDienThoaiDK.getText().toString().substring(1);
                String hoten = edHoTenDK.getText().toString();
                String matkhau = edPasswordDK.getText().toString();
                String nhaplaimatkhau = edNhapLaiPasswordDK.getText().toString();

                // Thông báo chờ khi đang xử lý quá trình đăng ký
                progressDialog.setMessage(getString(R.string.dangxuly));
                // Mở vòng tròn quay quay
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                // Kiểm tra tài khoản đã tồn tại hay chưa
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();

                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                progressDialog.dismiss();
                                Toast.makeText(DangKyActivity.this, getString(R.string.taikhoantontai), Toast.LENGTH_SHORT).show();
                            } else {
                                // Nếu chưa có thì tạo mới
                                registerNewAccount(email, matkhau, sodienthoai);
                                Log.d("kiemtra", sodienthoai);
                            }
                        } else {
                            Toast.makeText(DangKyActivity.this, "Can't get list user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;

            case R.id.btnXacNhanOTP:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential
                        (storedVerificationId, edOTP.getText().toString());
                linkPhoneNumberToAcccount(credential);
                break;
        }
    }


    private void registerNewAccount(String email, String matkhau, String sodienthoai) {
        firebaseAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();

                            thanhVienModel = new ThanhVienModel();
                            thanhVienModel.setHoten(email);
                            thanhVienModel.setHinhanh("user.png");
                            String uid = task.getResult().getUser().getUid();

                            dangKyController = new DangKyController();
                            dangKyController.ThemThongTinThanhVienController(thanhVienModel, uid);
                            Toast.makeText(DangKyActivity.this, getString(R.string.guiemailxacthuc), Toast.LENGTH_SHORT).show();

                            sendOTP(sodienthoai);

                        }


                    });
                }
            }
        });
    }

    private void sendOTP (String sodienthoai) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
                .setPhoneNumber(sodienthoai)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(DangKyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        storedVerificationId = s;
                        storedForceResendingToken = forceResendingToken;

                        layoutDK1.setVisibility(View.GONE);
                        layoutDK2.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void linkPhoneNumberToAcccount (PhoneAuthCredential phoneAuthCredential) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            user.linkWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Liên kết số điện thoại thành công", Toast.LENGTH_LONG).show();
                            thanhVienModel.setSodienthoai(sodienthoai);
                            dangKyController = new DangKyController();
                            dangKyController.ThemThongTinThanhVienController(thanhVienModel, user.getUid());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("mauser", user.getUid());
                            editor.commit();

                            Intent iTrangChu = new Intent(this, TrangChuActivity.class);
                            startActivity(iTrangChu);
                        } else {
                            Toast.makeText(this, "Phone number linking failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }


    }


}
