package com.khoinguyen.foody2.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.khoinguyen.foody2.Controller.DangKyController;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutDK1, layoutDK2;
    TextView tvGuiLaiOTP, tv_countdown;
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
    CountDownTimer countDownTimer;

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
        tvGuiLaiOTP = findViewById(R.id.tvGuiLaiOTP);
        tv_countdown = findViewById(R.id.tv_countdown);

        btnDangKy.setOnClickListener(this);
        btnXacNhanOTP.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        tvGuiLaiOTP.setOnClickListener(this);

        thanhVienModel = new ThanhVienModel();
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
                linkPhoneNumberToAcccount(credential, edEmailDK.getText().toString());
                break;

            case R.id.tvGuiLaiOTP:
                reSendOTP(sodienthoai);

                // Start the countdown timer
                startCountdownTimer();
                break;
        }
    }


    private void registerNewAccount(String email, String matkhau, String sodienthoai) {
        firebaseAuth.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(DangKyActivity.this, getString(R.string.guiemailxacthuc), Toast.LENGTH_SHORT).show();

                            sendOTP(sodienthoai);

                            // Start the countdown timer
                            startCountdownTimer();
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
                .setTimeout(30L, TimeUnit.SECONDS)
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
//                        storedForceResendingToken = forceResendingToken;

                        layoutDK1.setVisibility(View.GONE);
                        layoutDK2.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public void reSendOTP (String sodienthoai) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
                .setPhoneNumber(sodienthoai)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setForceResendingToken(storedForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        linkPhoneNumberToAcccount(phoneAuthCredential, edEmailDK.getText().toString());
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(DangKyActivity.this, "Error when resending OTP", Toast.LENGTH_LONG).show();
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

    private void linkPhoneNumberToAcccount (PhoneAuthCredential phoneAuthCredential, String email) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            user.linkWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Liên kết số điện thoại thành công", Toast.LENGTH_LONG).show();

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String currentDate = dateFormat.format(calendar.getTime());

                            String uid = user.getUid();

                            thanhVienModel.setHoten(edHoTenDK.getText().toString());
                            thanhVienModel.setHinhanh("user.png");
                            thanhVienModel.setEmail(email);
                            thanhVienModel.setDiachi("");
                            thanhVienModel.setSodienthoai(edSoDienThoaiDK.getText().toString());
                            thanhVienModel.setNgaytao(currentDate);

                            dangKyController = new DangKyController();
                            dangKyController.ThemThongTinThanhVienController(thanhVienModel, uid);

                            Uri photoUri = Uri.parse("user.png");
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(edHoTenDK.getText().toString())
                                    .setPhotoUri(photoUri)
                                    .build();
                            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("mauser", user.getUid());
                                    editor.commit();

                                    Intent iTrangChu = new Intent(DangKyActivity.this, TrangChuActivity.class);
                                    startActivity(iTrangChu);
                                }
                            });


                        } else {
                            Toast.makeText(this, "Phone number linking failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }


    }


}
