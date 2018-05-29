package lass.govertime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class PerfilUsuario extends Fragment{
    private CircleImageView imgPerfil;
    private EditText edtNome;
    private Button btneditar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private Uri imgUri = null;
    private StorageReference mStorage;

    private static final int IMAGEM_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_editar_perfil, container, false);

        mStorage = FirebaseStorage.getInstance().getReference().child("Imagem_User");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        imgPerfil = (CircleImageView)view.findViewById(R.id.imgPerfil);
        edtNome = (EditText)view.findViewById(R.id.nomePerfil);
        btneditar = (Button)view.findViewById(R.id.btnEditar);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getActivity());
        btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarPerfil();
            }
        });
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CarregarIMG = new Intent();
                CarregarIMG.setAction(Intent.ACTION_GET_CONTENT);
                CarregarIMG.setType("image/*");
                startActivityForResult(CarregarIMG, IMAGEM_REQUEST);
            }
        });
        return view;
    }


    private void editarPerfil() {

        final String nome = edtNome.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(nome) && imgUri != null){

            mProgress.setTitle("Editando perfil");
            mProgress.setMessage("Finalizando edição do perfil...");
            mProgress.show();

            StorageReference pastaIMG = mStorage.child(imgUri.getLastPathSegment());

            pastaIMG.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imgCel = taskSnapshot.getDownloadUrl().toString();
                    mDatabase.child(user_id).child("nome").setValue(nome);
                    mDatabase.child(user_id).child("imagem").setValue(imgCel);
                    mDatabase.child(user_id).child("email").setValue(mAuth.getCurrentUser().getEmail());
                    mDatabase.child(user_id).child("uid").setValue(mAuth.getCurrentUser().getUid());

                    mProgress.dismiss();
                    /*Intent inicio = new Intent(EditarPerfil.this, MainActivity.class);
                    inicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(inicio);*/

                }
            });


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGEM_REQUEST && resultCode == RESULT_OK){

            imgUri = data.getData();
            imgPerfil.setImageURI(imgUri);
        }
    }
}
