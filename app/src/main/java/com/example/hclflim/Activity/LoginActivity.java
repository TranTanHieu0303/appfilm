package com.example.hclflim.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hclflim.Object.TaiKhoan;
import com.example.hclflim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class LoginActivity extends  AppCompatActivity {
    EditText msdt, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    TaiKhoan taiKhoan;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        taiKhoan = (TaiKhoan) getIntent().getSerializableExtra("taiKhoan");
        msdt = findViewById(R.id.tvsdt);
        mPassword = findViewById(R.id.tvpassword);
        ccp = findViewById(R.id.cpp_manuoc);
        if(taiKhoan!=null)
        {
            msdt.setText(taiKhoan.getSDT().substring(3,taiKhoan.getSDT().length()));
            mPassword.setText(taiKhoan.getMatKhau());
        }
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.btnLogin);
        mCreateBtn = findViewById(R.id.tvcreateText);
        forgotTextLink=findViewById(R.id.tvForgotPass);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdt = msdt.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(sdt)) {
                    msdt.setError("S??? ??i???n tho???i kh??ng ???????c ????? tr???ng");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password kh??ng ???????c ????? tr???ng");
                    return;
                }

                if (password.length() < 8) {
                    mPassword.setError("M???t kh???u ph???i tr??n 8 k?? t???!");
                    return;
                }
                if (password.length() > 15) {
                    mPassword.setError("M???t kh???u th???p h??n 15 k?? t???!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                sdt = ccp.getSelectedCountryCodeWithPlus()+sdt;
                //d??ng nh???p b???ng firebase
                DatabaseReference datataikhoan = FirebaseDatabase.getInstance().getReference().child("TaiKhoan").child(sdt);

                datataikhoan.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        TaiKhoan tk = new TaiKhoan();
                        tk.setSDT((String) snapshot.child("sdt").getValue());
                        tk.setMatKhau((String) snapshot.child("matKhau").getValue());
                        tk.setTenKT((String) snapshot.child("tenKT").getValue());
                        if (tk != null) {
                            if (password.equals(tk.getMatKhau())) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("Taik",tk);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                                b.setTitle("HCL film th??ng b??o");
                                b.setMessage("S??? ??i???n tho???i ho???c m???t kh???u kh??ng ch??nh x??c\nVui l??ng ki???m tra l???i.");
                                b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog al = b.create();
                                al.show();
                            }
                        }
                        else {
                            AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                            b.setTitle("HCL film th??ng b??o");
                            b.setMessage("S??? ??i???n tho???i ho???c m???t kh???u kh??ng ch??nh x??c\nVui l??ng ki???m tra l???i.");
                            b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog al = b.create();
                            al.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,XacThucMainActivity.class);
                startActivity(intent);
            }
        });
    }
}