package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.models.User;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ImageProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.FileUtil;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

// Los metodos y variables que hagan referencia con tomar foto no estan implementadas porque nos daba algunos errores, seran implementados en futuras actualizaciones

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    CircleImageView mCircleImageViewProfle;
    ImageView mImageViewCover;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputTelefono;
    Button mButtonEditProfile;


    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    private final int GALERY_REQUEST_CODE_PROFILE=1;
    private final int GALERY_REQUEST_CODE_COVER=2;
    private final int PHOTO_REQUEST_CODE_PROFILE=5;
    private final int PHOTO_REQUEST_CODE_COVER=6;

    //FOTO 1
    String mAbsolutePhotoPath1;
    String mPhotoPath1;
    File mPhotoFile1;

    File miImageFile1;
    File miImageFile2;
    File miImageFile3;
    File miImageFile4;

    String mUsername ="";
    String mTelefono ="";
    String mImageProfile="";
    String mImageCover="";

    boolean photo1=false;
    boolean photo2=false;

    AlertDialog mDialog;

    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mCircleImageViewBack=findViewById(R.id.circulo);
        mCircleImageViewProfle=findViewById(R.id.circleImageProfile);
        mImageViewCover=findViewById(R.id.imageViewCover);
        mTextInputUsername=findViewById(R.id.text_editProfile_usuario);
        mTextInputTelefono=findViewById(R.id.text_editProfile_telefono);
        mButtonEditProfile=findViewById(R.id.btnEditProfile);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Opcion Disponible");
        options=new CharSequence[]{"Imagen de Galeria"};

        mImageProvider= new ImageProvider();
        mUsersProvider=new UsersProvider();
        mAuthProvider=new AuthProvider();

        mDialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(1);
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
            }
        });


        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
    }

    @Override
    public void onStart() {
        super.onStart();
            ViewedMessageHelper.updateOnline(true,EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,EditProfileActivity.this);
    }

    private void getUser()
    {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             if(documentSnapshot.exists())
             {
                 if(documentSnapshot.contains("username"))
                 {
                     mUsername=documentSnapshot.getString("username");
                     mTextInputUsername.setText(mUsername);
                 }

                 if(documentSnapshot.contains("telefono"))
                 {
                     mTelefono=documentSnapshot.getString("telefono");
                     mTextInputTelefono.setText(mTelefono);
                 }

                 if(documentSnapshot.contains("image_profile"))
                 {
                     mImageProfile=documentSnapshot.getString("image_profile");
                     if(mImageProfile!=null)
                     {
                        if(!mImageProfile.isEmpty())
                        {
                            Picasso.get().load(mImageProfile).into(mCircleImageViewProfle);
                        }
                     }
                 }

                 if(documentSnapshot.contains("image_cover"))
                 {
                     mImageCover=documentSnapshot.getString("image_cover");
                     if(mImageCover!=null)
                     {
                         if(!mImageCover.isEmpty())
                         {
                             Picasso.get().load(mImageCover).into(mImageViewCover);
                         }
                     }
                 }
             }
            }
        });
    }

    private void clickEditProfile() {
        mUsername= mTextInputUsername.getText().toString();
        mTelefono= mTextInputTelefono.getText().toString();

        if(!mUsername.isEmpty() && !mTelefono.isEmpty())
        {
            if(miImageFile1!=null && miImageFile2!=null)
            {
                saveImage2CoverAndProfile(miImageFile1,miImageFile2);
            }

            else if(miImageFile1!=null)
            {
                saveImage(miImageFile1,true);
            }

            else if(miImageFile2!=null)
            {
                saveImage(miImageFile2,false);
            }
            else
            {
                User user=new User();
                user.setUsername(mUsername);
                user.setTelefono(mTelefono);
                user.setId(mAuthProvider.getUid());
            }
        }

        else
        {
            Toast.makeText(this, "INGRESE EL NOMBRE DE USUARIO Y EL TELEFONO", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectOptionImage(int requestCode) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                {
                    openGalery(requestCode);
                }
                // No implementado
                else if(which==1)
                {
                    takePhoto();
                }
            }
        });

        mBuilderSelector.show();

    }

    //No implementado
    private void takePhoto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
        {
            File photoFile=null;
            try {
                photoFile= createPhotoFile();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if(photoFile!=null)
            {
                Uri photoUri= FileProvider.getUriForFile(EditProfileActivity.this, "com.example.bunpuoficial",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,PHOTO_REQUEST_CODE_PROFILE);

            }
        }
        else
        {
            Toast.makeText(this, "No entre al if", Toast.LENGTH_SHORT).show();
        }

    }

    //No implementado

    private File createPhotoFile() throws IOException {
        File storageDir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile= File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        mPhotoPath1="file:"+photoFile.getAbsolutePath();
        mAbsolutePhotoPath1=photoFile.getAbsolutePath();
        return photoFile;
    }

    private void openGalery(int requestCode) {
        Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);
    }

    private void saveImage2CoverAndProfile(File imageFile1,File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this,imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            final String urlProfile=uri1.toString();

                            mImageProvider.save(EditProfileActivity.this,imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful())
                                    {

                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String urlCover=uri2.toString();

                                                User user=new User();
                                                user.setUsername(mUsername);
                                                user.setTelefono(mTelefono);
                                                user.setImageProfile(urlProfile);
                                                user.setImageCover(urlCover);
                                                user.setId(mAuthProvider.getUid());
                                                updateInfo(user);
                                            }
                                        });
                                        Toast.makeText(EditProfileActivity.this, "La imagen2 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(EditProfileActivity.this, "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage(File image, boolean isProfileImage)
    {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this,image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url=uri.toString();
                            User user=new User();
                            user.setUsername(mUsername);
                            user.setTelefono(mTelefono);
                            if(isProfileImage)
                            {
                                user.setImageProfile(url);
                                user.setImageCover(mImageCover);
                            }
                            else
                            {
                                user.setImageCover(url);
                                user.setImageProfile(mImageProfile);
                            }
                            user.setId(mAuthProvider.getUid());
                            updateInfo(user);
                        }
                    });
                    Toast.makeText(EditProfileActivity.this, "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateInfo(User user)
    {
        if(mDialog.isShowing())
        {
            mDialog.show();
        }
        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if(task.isSuccessful())
                {
                    Toast.makeText(EditProfileActivity.this, "LA INFORMACION SE ACTUALIZO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "LA INFORMACION NO SE PUDO ACTUALIZAR", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Seleccion de la imagen desde la galeria

        if(requestCode == GALERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                miImageFile1= FileUtil.from(this,data.getData());
                mCircleImageViewProfle.setImageBitmap(BitmapFactory.decodeFile(miImageFile1.getAbsolutePath()));
                photo1=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(this,"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == GALERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                miImageFile2= FileUtil.from(this,data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(miImageFile2.getAbsolutePath()));
                photo2=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(this,"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        //Seleccion de fotografia
        // No implementado

        if(requestCode==PHOTO_REQUEST_CODE_PROFILE && resultCode==RESULT_OK)
        {
            Picasso.get().load(mPhotoPath1).into(mCircleImageViewProfle);
        }
    }


}