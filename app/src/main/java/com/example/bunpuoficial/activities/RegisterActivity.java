package com.example.bunpuoficial.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.bunpuoficial.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.models.User;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView circulo_atras;
    TextInputEditText text_usuario, text_contraseña, text_conf_contraseña, text_email, text_phone;
    TextView mTextViewMap;
    Button mButtonMap;
    Button boton_registrar;
    AuthProvider miAuthProvider;
    UsersProvider miUsersProvider;
    AlertDialog miDialog;
    RadioButton mRadioButtonEmpresa;
    RadioButton mRadioButtonNormal;
    MapsActivity mapsActivity;

    String typeUser = null;
    String valorLocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        circulo_atras = findViewById(R.id.circulo);
        text_usuario = findViewById(R.id.text_reg_usuario);
        text_email = findViewById(R.id.text_reg_correo);
        text_contraseña = findViewById(R.id.text_reg_contraseña);
        text_conf_contraseña = findViewById(R.id.text_reg_confirmar_contraseña);
        text_phone = findViewById(R.id.text_reg_telefono);
        mTextViewMap = findViewById(R.id.textView_reg_map);
        mRadioButtonEmpresa = findViewById(R.id.radioButtonEmpresa);
        mRadioButtonNormal = findViewById(R.id.radioButtonNormal);
        boton_registrar = findViewById(R.id.boton_registrar);
        mButtonMap = findViewById(R.id.button_map);

        miDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        miAuthProvider = new AuthProvider();
        miUsersProvider = new UsersProvider();
        mapsActivity = new MapsActivity();

        mTextViewMap.setText(valorLocalizacion);

        boton_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {registrar();}});

        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this, MapsActivity.class);
                startActivityForResult(intent,2);
            }
        });

        circulo_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void registrar(){

        String nombre_usuario =  text_usuario.getText().toString();
        String email = text_email.getText().toString();
        String contraseña = text_contraseña.getText().toString();
        String conf_contraseña = text_conf_contraseña.getText().toString();
        String telefono=text_phone.getText().toString();
        String localizacion=mTextViewMap.getText().toString();

        if(mRadioButtonEmpresa.isChecked())
        {
            typeUser="Empresa";
        }

        if(mRadioButtonNormal.isChecked())
        {
            typeUser="Normal";
        }

        if(!nombre_usuario.isEmpty() && !email.isEmpty() && !contraseña.isEmpty() && !conf_contraseña.isEmpty() && !telefono.isEmpty() && typeUser!=null && !localizacion.isEmpty()){
            if(isEmailValid(email)) {
                if (contraseña.equals(conf_contraseña)){
                    if(contraseña.length()>=6){
                        createuser(nombre_usuario,email,contraseña,telefono,typeUser,localizacion);
                    }else{
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "La contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(this, "Has insertador todos los campos y el email es valido", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Has insertador todos los campos y el email no es valido", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, nombre_usuario+email+contraseña+conf_contraseña+telefono+localizacion, Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Debes de insertar todos los campos",Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void createuser(String username,String correo,String contraseña, String telefono,String typeUser,String localizacion){
        miDialog.show();
        miAuthProvider.register(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = miAuthProvider.getUid();
                    User user = new User();
                    user.setId(id);
                    user.setEmail(correo);
                    user.setUsername(username);
                    user.setTelefono(telefono);
                    user.setTypeUser(typeUser);
                    user.setLocalizacion(localizacion);
                    user.setTimestamp(new Date().getTime());
                    miUsersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {
                            miDialog.dismiss();
                            if (task.isSuccessful()){
                                    Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this,"El usuario no se almaceno en la base de datos",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    miDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"No se pudo registrar el usuario",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode==2)
            {
                Bundle bundle=data.getExtras();
                mTextViewMap.setText(bundle.getString("localizacion"));
            }
        }
    }
}