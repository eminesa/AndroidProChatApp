package com.example.eminesa.androidprochatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "71443";
    static final String OUTH_KEY = "UaPAmJSRZ5QGrE4";
    static final String OUTH_SECRET = "Uu339j9NQHF4n88";
    static final String ACOUNT_KEY = "JvRicfkjQKsyKsohT9We";

    static final int REQUEST_CODE = 1000;

    Button btnLogin, btnSignUp;
    EditText edtUser, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();

        initializeFramework();

        btnLogin = (Button) findViewById(R.id.main_login_button);
        btnSignUp = (Button) findViewById(R.id.main_sign_up_button);

        edtPassword = (EditText) findViewById(R.id.pass_edit_text);
        edtUser = (EditText) findViewById(R.id.login_edit_text);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();

                QBUser qbUser = new QBUser(user, password);

                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() { // Kullanıcınin belirlenen şifre ile giriş yapılmasını sağlayan kod bloğu
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        Toast.makeText(getBaseContext(), "Login succesufully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, ChatsDialogsActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish(); // Close login activity after  logged

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "" + e.getErrors(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void requestRuntimePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{

                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;

        }
    }

    private void initializeFramework() {

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, OUTH_KEY, OUTH_SECRET); // verileri kontrol eder
        QBSettings.getInstance().setAccountKey(ACOUNT_KEY);

    }

}
