package com.khoinguyen.foody2.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoinguyen.foody2.Controller.DangKyController;
import com.khoinguyen.foody2.Model.BinhLuanModel;
import com.khoinguyen.foody2.Model.ChiNhanhQuanAnModel;
import com.khoinguyen.foody2.Model.QuanAnModel;
import com.khoinguyen.foody2.Model.ThanhVienModel;
import com.khoinguyen.foody2.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DangNhapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, FirebaseAuth.AuthStateListener {

    Button btnDangNhapGoogle, btnDangNhapFacebook, btnDangNhap, btnDangNhapSDT;
    TextView txtDangKyMoi, txtQuenMatKhau;
    EditText edEmail, edPassword;
    RadioGroup group_dangonngu;
    RadioButton rd_Vietnam, rd_English;

    GoogleApiClient apiClient;
    GoogleSignInAccount accountGoogle;  // account google sau khi login
    public static final int REQUESTCODE_DANGNHAP_GOOGLE = 99;
    public static int KIEMTRA_PROVIDER_DANGNHAP = 0;
    FirebaseAuth firebaseAuth;
    CallbackManager mCallbackFacebook;
    LoginManager loginManager;
    List<String> permissionFacebook = Arrays.asList("email", "public_profile");

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.layout_dangnhap);

        firebaseAuth = FirebaseAuth.getInstance();

        btnDangNhapGoogle = findViewById(R.id.btnDangNhapGoogle);
        btnDangNhapFacebook = findViewById(R.id.btnDangNhapFacebook);
        btnDangNhapSDT = findViewById(R.id.btnDangNhapSDT);
        txtDangKyMoi = findViewById(R.id.txtDangKyMoi);
        txtQuenMatKhau = findViewById(R.id.txtQuenMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        edEmail = findViewById(R.id.edEmailDN);
        edPassword = findViewById(R.id.edPasswordDN);
        group_dangonngu = findViewById(R.id.group_dangonngu);
        rd_Vietnam = findViewById(R.id.rd_Vietnam);
        rd_English = findViewById(R.id.rd_English);

        btnDangNhapGoogle.setOnClickListener(this);
        btnDangNhapFacebook.setOnClickListener(this);
        txtDangKyMoi.setOnClickListener(this);
        txtQuenMatKhau.setOnClickListener(this);
        btnDangNhap.setOnClickListener(this);
        btnDangNhapSDT.setOnClickListener(this);

        rd_Vietnam.setOnClickListener(this);
        rd_English.setOnClickListener(this);

        // Đăng nhập facebook
        mCallbackFacebook = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        // Đăng nhập google
        TaoClientDangNhapGoogle();

        sharedPreferences = getSharedPreferences("luudangnhap", MODE_PRIVATE);

        sharedPreferencesLanguage = getSharedPreferences("language", MODE_PRIVATE);
        int languageCode = sharedPreferencesLanguage.getInt("selected_language", 0);

        if (languageCode == 0) {
            rd_Vietnam.setChecked(true);
        }
        else {
            rd_English.setChecked(true);
        }
    }

    private void DangNhapFacebook () {
        loginManager.logInWithReadPermissions(this, permissionFacebook);
        loginManager.registerCallback(mCallbackFacebook, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                KIEMTRA_PROVIDER_DANGNHAP = 2;
                String tokenID = loginResult.getAccessToken().getToken();
                ChungThucDangNhapFirebase(tokenID);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }
        });
    }


    // Khởi tạo client cho đăng nhập google
    private void TaoClientDangNhapGoogle () {
        // Chuỗi trong requestIdToken: mở file google-service.json (file tải từ firebase) -> client -> oauth_client -> client_id (client_id thứ 2)
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken("803559942953-q6v6hloo6e5choq65dlt2d8vjejk0748.apps.googleusercontent.com")
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
        firebaseAuth.signOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }

    private void DangNhapGoogle(GoogleApiClient apiClient){
        KIEMTRA_PROVIDER_DANGNHAP = 1;
        // Mở giao diện đăng nhập google
        Intent iDNGoogle = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(iDNGoogle,REQUESTCODE_DANGNHAP_GOOGLE);
    }

    private void ChungThucDangNhapFirebase (String tokenID) {
        if (KIEMTRA_PROVIDER_DANGNHAP == 1) {
            // Lấy chứng chỉ google của tài khoản dựa vào token id của tài khoản
            AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID, null);

            // CHo phép đăng nhập bằng tài khoản google (với chứng chỉ ở trên) và kích hoạt function onAuthStateChanged() cho phép kiểm tra trạng thái đăng nhập
            firebaseAuth.signInWithCredential(authCredential);
        }
        else if (KIEMTRA_PROVIDER_DANGNHAP == 2) {
            AuthCredential authCredential = FacebookAuthProvider.getCredential(tokenID);
            firebaseAuth.signInWithCredential(authCredential);
        }

    }

    // Hàm này xử lý cho hàm startActivityForResult trong function DangNhapGoogle()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Dành cho google
        if (requestCode == REQUESTCODE_DANGNHAP_GOOGLE) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                // Lấy ra được tài khoản đã đăng nhập bằng google
                accountGoogle = signInResult.getSignInAccount();
                String tokenID = accountGoogle.getIdToken();

                // Truyền token id của tài khoản cho function bên dưới
                ChungThucDangNhapFirebase(tokenID);
            }
        }

        // Dành cho facebook
        else {
            // Gọi hàm onSuccess ( nằm ở function onCreate() )
            mCallbackFacebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnDangNhapGoogle:
                DangNhapGoogle(apiClient);
                break;
            case R.id.btnDangNhapFacebook:
                DangNhapFacebook();
                break;
            case R.id.btnDangNhapSDT:
                Intent iDangNhapSDT = new Intent(this, DangNhapSDTActivity.class);
                startActivity(iDangNhapSDT);
                break;
            case R.id.txtDangKyMoi:
                Intent iDangKy = new Intent(this, DangKyActivity.class);
                startActivity(iDangKy);
                break;
            case R.id.txtQuenMatKhau:
                Intent iKhoiPhucMatKhau = new Intent(this, KhoiPhucMatKhauActivity.class);
                startActivity(iKhoiPhucMatKhau);
                break;
            case R.id.btnDangNhap:
                DangNhap();
                break;
            case R.id.rd_Vietnam:
                saveSelectedLanguage(0);
                changeLanguage("vi");
                rd_Vietnam.setChecked(true);
                break;
            case R.id.rd_English:
                saveSelectedLanguage(1);
                changeLanguage("en");
                rd_English.setChecked(true);
                break;
        }

    }

    private void DangNhap () {
        String email = edEmail.getText().toString();
        String matkhau = edPassword.getText().toString();

        // Xác thực đăng nhập thành công sẽ chạy xuống hàm onAuthStateChanged()
        firebaseAuth.signInWithEmailAndPassword(email, matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() == false) {
                    Toast.makeText(DangNhapActivity.this, getString(R.string.thongbaodangnhapthatbai), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Kiểm tra trạng thái đăng nhập của user (cả facebook và google)
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        ThanhVienModel thanhVienModel = new ThanhVienModel();
        DangKyController dangKyController = new DangKyController();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // Kiểm tra tk google và fb đã được đăng nhập trước đó chưa
            checkUserExist(user.getUid());

            // Đăng nhập google
            if (KIEMTRA_PROVIDER_DANGNHAP == 1) {
                if (thanhVienModelExist != null) {
                    Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActivity.class);
                    startActivity(iTrangChu);
                    return;
                }
                thanhVienModel.setHoten("");
                thanhVienModel.setHinhanh("user.png");
                thanhVienModel.setEmail(accountGoogle.getEmail());
                thanhVienModel.setDiachi("");
                thanhVienModel.setSodienthoai("");
                thanhVienModel.setNgaytao(currentDate);

                dangKyController.ThemThongTinThanhVienController(thanhVienModel, user.getUid());
            }

            // Đăng nhập facebook
            else if (KIEMTRA_PROVIDER_DANGNHAP == 2) {
                if (thanhVienModelExist != null) {
                    Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActivity.class);
                    startActivity(iTrangChu);
                    return;
                }

                thanhVienModel.setHoten(user.getDisplayName());
                thanhVienModel.setHinhanh("user.png");
                thanhVienModel.setEmail(user.getEmail());
                thanhVienModel.setDiachi("");
                thanhVienModel.setSodienthoai("");
                thanhVienModel.setNgaytao(currentDate);

                dangKyController.ThemThongTinThanhVienController(thanhVienModel, user.getUid());
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("mauser", user.getUid());
            editor.commit();

            Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActivity.class);
            startActivity(iTrangChu);
        }
    }

    ThanhVienModel thanhVienModelExist = new ThanhVienModel();
    private void checkUserExist(String uid) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataThanhVien = dataSnapshot.child("thanhviens");
                for (DataSnapshot valueThanhVien : dataThanhVien.getChildren()) {
                    if (valueThanhVien.getKey().compareTo(uid) == 0) {
                        thanhVienModelExist = valueThanhVien.getValue(ThanhVienModel.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeLanguage (String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;

        // Yêu cầu app cập nhật lại file string
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        // Load lại trang
        Intent intent = new Intent(this, DangNhapActivity.class);
        startActivity(intent);
    }

    private void saveSelectedLanguage(int languageCode) {
        SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
        editor.putInt("selected_language", languageCode);
        editor.commit();
    }
}
