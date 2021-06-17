package com.example.bunpuoficial.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.bunpuoficial.R;

import androidx.annotation.NonNull;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText text_usuario;
    TextInputEditText text_telefono;
    TextView mTextViewMap;
    Button boton_confirmar;
    RadioButton mRadioButtonEmpresa;
    RadioButton mRadioButtonNormal;
    AuthProvider miAuthProvider;
    UsersProvider miUsersProvider;
    AlertDialog miDialog;

    MapsActivity mapsActivity;

    Button mButtonMap;

    String typeUser;
    String fireLocalizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        text_usuario = findViewById(R.id.text_conf_usuario);
        text_telefono = findViewById(R.id.text_reg_telefono);
        mTextViewMap = findViewById(R.id.textView_com_map);
        mRadioButtonEmpresa=findViewById(R.id.radioButtonEmpresaComplete);
        mRadioButtonNormal=findViewById(R.id.radioButtonNormalComplete);
        mButtonMap = findViewById(R.id.button_com_map);
        boton_confirmar = findViewById(R.id.boton_confirmar);

        miAuthProvider = new AuthProvider();
        miUsersProvider =new UsersProvider();
        mapsActivity=new MapsActivity();

        miDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CompleteProfileActivity.this, MapsActivity.class);
                startActivityForResult(intent,2);
            }
        });

        boton_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
    }

    private void registrar(){
        String nombre_usuario =  text_usuario.getText().toString();
        String telefono=text_telefono.getText().toString();
        if(mRadioButtonEmpresa.isChecked()){
            typeUser="Empresa";
        }else{
            typeUser="Normal";
        }
        if(!nombre_usuario.isEmpty() && !telefono.isEmpty()){
            updateuser(nombre_usuario,telefono,typeUser,fireLocalizacion);

        }else{
            Toast.makeText(this,"Todos los campos son obligatorios",Toast.LENGTH_LONG).show();
        }
    }


    private void updateuser(String username, String telefono, String typeUser,String fireLocalizacion){
        String id = miAuthProvider.getUid();
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        user.setTelefono(telefono);
        user.setTypeUser(typeUser);
        user.setLocalizacion(fireLocalizacion);
        user.setTimestamp(new Date().getTime());

        miDialog.show();
        miUsersProvider.updateCompleteProfile(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if (task.isSuccessful()){
                    miDialog.dismiss();
                    Intent intent = new Intent(CompleteProfileActivity.this,HomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CompleteProfileActivity.this,"El usuario no se almaceno en la base de datos",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        miUsersProvider.delete(miAuthProvider.getUid());
        miAuthProvider.delete();
        miAuthProvider.logout();
        Toast.makeText(CompleteProfileActivity.this,"retroceder",Toast.LENGTH_LONG).show();
        super.onBackPressed();
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
                fireLocalizacion=mTextViewMap.getText().toString();
            }
        }
    }
}