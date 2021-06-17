package com.example.bunpuoficial.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ImageProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

// Los metodos y variables que hagan referencia con tomar foto no estan implementadas porque nos daba algunos errores, seran implementados en futuras actualizaciones

public class ProductoFragment extends Fragment {

    View view;
    Spinner categorias;
    ImageView miImageviewProduct1;
    ImageView miImageviewProduct2;
    ImageView miImageviewProduct3;
    ImageView miImageviewProduct4;

    File miImageFile1;
    File miImageFile2;
    File miImageFile3;
    File miImageFile4;

    Button btn_producto_publicar;

    ImageProvider mImageProvider;
    ImageProvider mImageProvider2;
    ImageProvider mImageProvider3;
    ImageProvider mImageProvider4;

    ProductProvider mProductProvider;
    AuthProvider mAuthProvider;

    CardView cardViewImage2;
    CardView cardViewImage3;
    CardView cardViewImage4;

    TextInputEditText text_producto_nombreProducto;
    TextInputEditText text_producto_precio;
    Spinner spinner_categoria;
    TextInputEditText text_producto_descripcion;
    String mCategoria;
    String mProductName;
    String mPrice;
    String mDescription;
    AlertDialog mDialog;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    private final int GALERY_REQUEST_CODE_1=1;
    private final int GALERY_REQUEST_CODE_2=2;
    private final int GALERY_REQUEST_CODE_3=3;
    private final int GALERY_REQUEST_CODE_4=4;
    private final int PHOTO_REQUEST_CODE_1=5;
    private final int PHOTO_REQUEST_CODE_2=6;
    private final int PHOTO_REQUEST_CODE_3=7;
    private final int PHOTO_REQUEST_CODE_4=8;

    //FOTO 1
    // No implementado
    String mAbsolutePhotoPath1;
    String mPhotoPath1;
    File mPhotoFile1;

    boolean photo1=false;
    boolean photo2=false;
    boolean photo3=false;
    boolean photo4=false;

    public ProductoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view =inflater.inflate(R.layout.fragment_producto, container, false);
        categorias = view.findViewById(R.id.spinner_categoria);
        cardViewImage2=view.findViewById(R.id.cardViewImage2);
        cardViewImage3=view.findViewById(R.id.cardViewImage3);
        cardViewImage4=view.findViewById(R.id.cardViewImage4);

        final String [] arCategorias = new String[]{"Carniceria","Pescaderia","Fruteria","Panaderia"};
        ArrayAdapter <String> stringArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1,arCategorias);
        categorias.setAdapter(stringArrayAdapter);

        //Cada dato
        mProductProvider=new ProductProvider();
        mAuthProvider=new AuthProvider();

        mDialog= new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        // Sirve para crear un menu donde el usuario pueda selecionar una imagen de la galeria o sino puede tomar una foto

        mBuilderSelector = new AlertDialog.Builder(getContext());
        mBuilderSelector.setTitle("Opcion Disponible");
        options=new CharSequence[]{"Imagen de Galeria"};

        text_producto_nombreProducto=view.findViewById(R.id.text_producto_nombreProducto);
        text_producto_precio=view.findViewById(R.id.text_producto_precio);
        spinner_categoria=view.findViewById(R.id.spinner_categoria);
        text_producto_descripcion=view.findViewById(R.id.text_producto_descripcion);

        spinner_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obtenemos la categoria seleccionada
                mCategoria=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Boton publicar producto

        mImageProvider=new ImageProvider();
        mImageProvider2=new ImageProvider();
        mImageProvider3=new ImageProvider();
        mImageProvider4=new ImageProvider();

        btn_producto_publicar=view.findViewById(R.id.btn_producto_publicar);
        btn_producto_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProducto();
            }
        });

        //Image view
        miImageviewProduct1 = view.findViewById(R.id.imageViewProducto1);
        miImageviewProduct1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(1);
            }
        });

        miImageviewProduct2 = view.findViewById(R.id.imageViewProducto2);
        miImageviewProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
            }
        });

        miImageviewProduct3 = view.findViewById(R.id.imageViewProducto3);
        miImageviewProduct3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(3);
            }
        });

        miImageviewProduct4 = view.findViewById(R.id.imageViewProducto4);
        miImageviewProduct4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(4);
            }
        });

        return view;
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
    // No implementado
    private void takePhoto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null)
        {
            Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();
            File photoFile=null;
            try {
                photoFile= createPhotoFile();
            }
            catch (Exception e)
            {
                Toast.makeText(getContext(), "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if(photoFile!=null)
            {
                Uri photoUri= FileProvider.getUriForFile(getContext(), "com.example.bunpuoficial",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,PHOTO_REQUEST_CODE_1);

            }
        }
        else
        {
            Toast.makeText(getContext(), "No entre al if", Toast.LENGTH_SHORT).show();
        }

    }

    // No implementado
    private File createPhotoFile() throws IOException {
        File storageDir= getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile= File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );

        mPhotoPath1="file:"+photoFile.getAbsolutePath();
        mAbsolutePhotoPath1=photoFile.getAbsolutePath();
        return photoFile;
    }

    private void clickProducto() {
        mProductName=text_producto_nombreProducto.getText().toString();
        mPrice=text_producto_precio.getText().toString();
        mDescription=text_producto_descripcion.getText().toString();

        if (!mProductName.isEmpty() && !mPrice.isEmpty() && !mDescription.isEmpty())
        {
            if(miImageFile1!=null)
            {
                if(photo1==true && photo2==false && photo3==false && photo4==false)
                {
                    saveImage(miImageFile1);
                }

                else if(photo1==true && photo2==true && photo3==false && photo4==false)
                {
                    saveImage2(miImageFile1,miImageFile2);
                }

                else if (photo1==true && photo2==true && photo3==true && photo4==false)
                {
                    saveImage3(miImageFile1,miImageFile2,miImageFile3);
                }

                else if(photo1==true && photo2==true && photo3==true && photo4==true)
                {
                    saveImage4(miImageFile1,miImageFile2,miImageFile3,miImageFile4);
                }
            }

            else
            {
                Toast.makeText(getContext(), "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(File imageFile1) {
        mDialog.show();
        mImageProvider.save(getContext(),imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            final String url1=uri1.toString();

                            Product product=new Product();
                            product.setImage1(url1);
                            product.setProductName(mProductName);
                            product.setPrice(mPrice);
                            product.setDescription(mDescription);
                            product.setCategory(mCategoria);
                            product.setIdUser(mAuthProvider.getUid());
                            product.setTimestamp(new Date().getTime());

                            mProductProvider.save(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    mDialog.dismiss();
                                    if(taskSave.isSuccessful())
                                    {
                                        clearForm();
                                        Toast.makeText(getContext(), "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        mDialog.dismiss();
                                        Toast.makeText(getContext(), "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }
                    });
                    Toast.makeText(getContext(), "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(getContext(), "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage2(File imageFile1,File imageFile2) {
        mDialog.show();
        mImageProvider.save(getContext(),imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            final String url1=uri1.toString();

                            mImageProvider.save(getContext(),imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful())
                                    {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String url2=uri2.toString();

                                                Product product=new Product();
                                                product.setImage1(url1);
                                                product.setImage2(url2);
                                                product.setProductName(mProductName);
                                                product.setPrice(mPrice);
                                                product.setDescription(mDescription);
                                                product.setCategory(mCategoria);
                                                product.setIdUser(mAuthProvider.getUid());
                                                product.setTimestamp(new Date().getTime());

                                                mProductProvider.save(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful())
                                                        {
                                                            clearForm();
                                                            Toast.makeText(getContext(), "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            mDialog.dismiss();
                                                            Toast.makeText(getContext(), "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                            }
                                        });
                                        Toast.makeText(getContext(), "La imagen2 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        mDialog.dismiss();
                                        Toast.makeText(getContext(), "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(getContext(), "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(getContext(), "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage3(File imageFile1,File imageFile2,File imageFile3) {
        mDialog.show();
        mImageProvider.save(getContext(),imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            final String url1=uri1.toString();

                            mImageProvider.save(getContext(),imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful())
                                    {

                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String url2=uri2.toString();

                                                mImageProvider.save(getContext(),imageFile3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage3) {
                                                        if(taskImage3.isSuccessful())
                                                        {
                                                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri3) {
                                                                    final String url3=uri3.toString();

                                                                    Product product=new Product();
                                                                    product.setImage1(url1);
                                                                    product.setImage2(url2);
                                                                    product.setImage3(url3);
                                                                    product.setProductName(mProductName);
                                                                    product.setPrice(mPrice);
                                                                    product.setDescription(mDescription);
                                                                    product.setCategory(mCategoria);
                                                                    product.setIdUser(mAuthProvider.getUid());
                                                                    product.setTimestamp(new Date().getTime());

                                                                    mProductProvider.save(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> taskSave) {
                                                                            mDialog.dismiss();
                                                                            if(taskSave.isSuccessful())
                                                                            {
                                                                                clearForm();
                                                                                Toast.makeText(getContext(), "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else
                                                                            {
                                                                                mDialog.dismiss();
                                                                                Toast.makeText(getContext(), "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });


                                                                }
                                                            });
                                                            Toast.makeText(getContext(), "La imagen3 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            mDialog.dismiss();
                                                            Toast.makeText(getContext(), "La imagen numero 3 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        Toast.makeText(getContext(), "La imagen2 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        mDialog.dismiss();
                                        Toast.makeText(getContext(), "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(getContext(), "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(getContext(), "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage4(File imageFile1,File imageFile2,File imageFile3,File imageFile4) {
        mDialog.show();
        mImageProvider.save(getContext(),imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage1) {
                if(taskImage1.isSuccessful())
                {

                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            final String url1=uri1.toString();

                            mImageProvider.save(getContext(),imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful())
                                    {

                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String url2=uri2.toString();

                                                mImageProvider.save(getContext(),imageFile3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage3) {
                                                        if(taskImage3.isSuccessful())
                                                        {
                                                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri3) {
                                                                    final String url3=uri3.toString();

                                                                    mImageProvider.save(getContext(),imageFile4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage4) {
                                                                            if(taskImage4.isSuccessful())
                                                                            {
                                                                                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri4) {
                                                                                        final String url4=uri4.toString();

                                                                                        Product product=new Product();
                                                                                        product.setImage1(url1);
                                                                                        product.setImage2(url2);
                                                                                        product.setImage3(url3);
                                                                                        product.setImage4(url4);
                                                                                        product.setProductName(mProductName);
                                                                                        product.setPrice(mPrice);
                                                                                        product.setDescription(mDescription);
                                                                                        product.setCategory(mCategoria);
                                                                                        product.setIdUser(mAuthProvider.getUid());
                                                                                        product.setTimestamp(new Date().getTime());

                                                                                        mProductProvider.save(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> taskSave) {
                                                                                                mDialog.dismiss();
                                                                                                if(taskSave.isSuccessful())
                                                                                                {
                                                                                                    clearForm();
                                                                                                    Toast.makeText(getContext(), "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                                else
                                                                                                {
                                                                                                    mDialog.dismiss();
                                                                                                    Toast.makeText(getContext(), "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                                Toast.makeText(getContext(), "La imagen4 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else
                                                                            {
                                                                                mDialog.dismiss();
                                                                                Toast.makeText(getContext(), "La imagen numero 4 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                            Toast.makeText(getContext(), "La imagen3 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            mDialog.dismiss();
                                                            Toast.makeText(getContext(), "La imagen numero 3 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        Toast.makeText(getContext(), "La imagen2 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        mDialog.dismiss();
                                        Toast.makeText(getContext(), "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(getContext(), "La imagen1 se almaceno correctamennte", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(getContext(), "Hubo error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearForm() {
        text_producto_nombreProducto.setText("");
        text_producto_precio.setText("");
        text_producto_descripcion.setText("");
        miImageviewProduct1.setImageResource(R.drawable.ic_camra_color_primary);
        miImageviewProduct2.setImageResource(R.drawable.ic_camra_color_primary);
        miImageviewProduct3.setImageResource(R.drawable.ic_camra_color_primary);
        miImageviewProduct4.setImageResource(R.drawable.ic_camra_color_primary);
        mProductName="";
        mPrice="";
        mDescription="";
        mCategoria="";
        miImageFile1=null;
        miImageFile2=null;
        miImageFile3=null;
        miImageFile4=null;
    }

    private void openGalery(int requestCode) {
        Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Seleccion de la imagen desde la galeria

        if(requestCode == GALERY_REQUEST_CODE_1 && resultCode == RESULT_OK){
            try {
                miImageFile1= FileUtil.from(getContext(),data.getData());
                if(miImageFile1!=null)
                {
                    miImageviewProduct1.setImageBitmap(BitmapFactory.decodeFile(miImageFile1.getAbsolutePath()));
                    cardViewImage2.setVisibility(View.VISIBLE);
                    miImageviewProduct2.setVisibility(View.VISIBLE);
                }
                photo1=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(view.getContext(),"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == GALERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                miImageFile2= FileUtil.from(getContext(),data.getData());
                if(miImageFile2!=null)
                {
                    miImageviewProduct2.setImageBitmap(BitmapFactory.decodeFile(miImageFile2.getAbsolutePath()));
                    cardViewImage3.setVisibility(View.VISIBLE);
                    miImageviewProduct3.setVisibility(View.VISIBLE);
                }
                photo2=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(view.getContext(),"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == GALERY_REQUEST_CODE_3 && resultCode == RESULT_OK){
            try {
                miImageFile3= FileUtil.from(getContext(),data.getData());
                if(miImageFile3!=null)
                {
                    miImageviewProduct3.setImageBitmap(BitmapFactory.decodeFile(miImageFile3.getAbsolutePath()));
                    cardViewImage4.setVisibility(View.VISIBLE);
                    miImageviewProduct4.setVisibility(View.VISIBLE);
                }
                photo3=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(view.getContext(),"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }

        if(requestCode == GALERY_REQUEST_CODE_4 && resultCode == RESULT_OK){
            try {
                miImageFile4= FileUtil.from(getContext(),data.getData());
                miImageviewProduct4.setImageBitmap(BitmapFactory.decodeFile(miImageFile4.getAbsolutePath()));
                photo4=true;
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error "+e.getMessage());
                Toast.makeText(view.getContext(),"Se produjo un error " +e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        //Seleccion de fotografia
        //  No implementado

        if(requestCode==PHOTO_REQUEST_CODE_1 && resultCode==RESULT_OK)
        {
            Toast.makeText(getContext(), "Llego", Toast.LENGTH_SHORT).show();
            Picasso.get().load(mPhotoPath1).into(miImageviewProduct1);
        }
    }
}