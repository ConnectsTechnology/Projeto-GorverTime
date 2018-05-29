package lass.govertime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.Presidentes.DatabaseUtil;
import lass.govertime.Presidentes.FragmentComentario;
import lass.govertime.Presidentes.ListaPresidente;
import lass.govertime.Presidentes.PerfilPolitico;
import lass.govertime.Presidentes.Pessoa;
import lass.govertime.Presidentes.Ranking;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String nome,email;
    private FragmentManager fragmentManager;

    private RecyclerView listaRancking;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mRancking;
    private DatabaseReference mSeguir;
    private boolean clickVotar = false;
    private boolean clickSeguir = false;
    private int base;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setLayout(1);
        base = 1;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Logado();
                } else {
                Logar();
                }
            }
        };

    }

    private void setLayout(int op){
        Object classe = null;
        String id = null;

        if(op == 1){
            classe = new Ranking();
            id = "Ranking";

        }else if(op == 2){
            classe = new ListaPresidente();
            id = "Lista Presidente";

        }else if(op == 3){
            classe = new PerfilUsuario();
            id = "Meu Perfil";
        }
        base = op;
        FrameLayout it = (FrameLayout)findViewById(R.id.container);
        it.removeAllViews();
        getSupportActionBar().setTitle(id);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, (Fragment) classe, id );
        transaction.commitAllowingStateLoss();
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void Logado(){
        String uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nome = dataSnapshot.child("nome").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                setInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Logar(){
        NavigationView navi = (NavigationView)findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);
        Menu menu = navi.getMenu();


        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem login = menu.findItem(R.id.nav_login);

        logout.setVisible(false);
        login.setVisible(true);
    }

    private void setInfo(){
        NavigationView navi = (NavigationView)findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);
        View header = navi.getHeaderView(0);
        Menu menu = navi.getMenu();

        TextView nome2 = (TextView) header.findViewById(R.id.menuNome);
        TextView email2 = (TextView) header.findViewById(R.id.menuEmail);
        System.out.println(nome.toString());
        nome2.setText(nome.toString());
        email2.setText(email.toString());

        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem favoritos = menu.findItem(R.id.nav_favoritos);
        MenuItem perfil = menu.findItem(R.id.nav_perfil);

        favoritos.setVisible(true);
        perfil.setVisible(true);
        logout.setVisible(true);
        login.setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        //Define um texto de ajuda:
        mSearchView.setQueryHint("teste");
        // exemplos de utilização:
        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_login){
            startActivity(new Intent(this, Login.class));
        }else if (id == R.id.nav_feed) {

        }else if (id == R.id.nav_ranking) {
            setLayout(1);
            //pesquisa(2);
        }else if (id == R.id.nav_all) {
            setLayout(2);
        }else if (id == R.id.nav_perfil) {
            setLayout(3);
        } else if (id == R.id.nav_favoritos) {

        } else if (id == R.id.nav_termos) {
            cliqueTermos();
        } else if (id == R.id.nav_logout) {
            mAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (id == R.id.nav_fechar) {
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*private void pesquisa(int op){
        NavigationView navi = (NavigationView)findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);
        Menu menu = navi.getMenu();
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchMenu = (SearchView) menu.findItem(R.id.search).getActionView();
        if(op == 1) {
            search.setVisible(true);
        }else{
            //search.setVisible(false);
            searchMenu.setVisibility(View.INVISIBLE);
        }
    }*/

    private void cliqueTermos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Termos de Uso");
        builder.setMessage("\n\nUSABILIDADE\n\n");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog  alerta = builder.create();
        alerta.show();
        Button positiveButton = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(base == 1) {
            carregar(newText);
        }
        return true;
    }

    public void carregar(String txt2){
        mAuth = FirebaseAuth.getInstance();
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(this);
        Query query;

        listaRancking = (RecyclerView)findViewById(R.id.listaRancking);
        listaRancking.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getApplicationContext());
        ln.setReverseLayout(true);
        ln.setStackFromEnd(true);
        listaRancking.setLayoutManager(ln);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
        mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
        mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
        mRancking.keepSynced(true);
        mSeguir.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        if (txt2.toString().equals("")) {
            query = mDatabase.orderByChild("voto");

        }else {
            query = mDatabase.orderByChild("nome").startAt(txt2).endAt(txt2+"\uf8ff");

        }



        final FirebaseRecyclerAdapter<Pessoa, MainActivity.PresidenteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, MainActivity.PresidenteViewHolder>
                (
                        Pessoa.class,
                        R.layout.item_rancking,
                        MainActivity.PresidenteViewHolder.class,
                        query
                ) {
            @Override
            public void populateViewHolder(final MainActivity.PresidenteViewHolder viewHolder, Pessoa model, int position) {
                final String chave_presidente = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                if(mAuth.getCurrentUser() == null) {
                    // viewHolder.setVoto(model.getVoto());
                }
                viewHolder.setImagem(getApplicationContext(), model.getImagem());
                viewHolder.setBtn(chave_presidente);
                viewHolder.setBtnSeguir(chave_presidente);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent telaPres = new Intent(getApplicationContext(), PerfilPolitico.class);
                        telaPres.putExtra("anuncio_id", chave_presidente);
                        startActivity(telaPres);
                        Intent tt = new Intent(getApplicationContext(), FragmentComentario.class);
                        tt.putExtra("id", chave_presidente);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", chave_presidente);
                        finish();
                    }
                });




                viewHolder.btnVotar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mAuth.getCurrentUser() != null){
                            clickVotar = true;
                            mRancking.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    final String user = mAuth.getCurrentUser().getUid();

                                    mDatabaseUser.child(user).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot data) {
                                            final String teste = (String) data.child("votou").getValue();


                                            if (clickVotar) {
                                                if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                                                    if (teste != null) {
                                                        mRancking.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    }
                                                    clickVotar = false;
                                                } else {

                                                    if (teste == null) {
                                                        mRancking.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).child("voto").setValue(mAuth.getCurrentUser().getUid());
                                                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).child("votou").setValue(mAuth.getCurrentUser().getUid());
                                                    }else {
                                                        Toast.makeText(getApplicationContext(), "Você Já votou!!!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    clickVotar = false;
                                                }

                                            }

                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Vc precisa fazer login para votar...", Toast.LENGTH_SHORT).show();
                        }
                    }


                });





                viewHolder.btnSeguir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() != null){
                            clickSeguir = true;
                            mSeguir.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (clickSeguir) {
                                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                                            mSeguir.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            clickSeguir = false;

                                        } else {

                                            mSeguir.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).child("seguiu").setValue("seguiu");
                                            clickSeguir = false;

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Vc precisa fazer login para seguir o politico...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        listaRancking.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PresidenteViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton btnVotar;
        ImageButton btnVotarRed;
        ImageButton btnSeguir;
        DatabaseReference mRancking;
        DatabaseReference mDatabaseUser;
        DatabaseReference mSeguir;
        DatabaseReference mEleicao;
        FirebaseAuth mAuth;
        TextView voto;
        TextView txt;
        int contador;

        public PresidenteViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            btnVotar = (ImageButton)mView.findViewById(R.id.btnVotar);
            btnVotarRed = (ImageButton)mView.findViewById(R.id.btnVotarRed);
            btnSeguir = (ImageButton)mView.findViewById(R.id.btnSeguir);
            voto = (TextView)mView.findViewById(R.id.votos);
            txt = (TextView)mView.findViewById(R.id.votosText);

            mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
            mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
            mEleicao = FirebaseDatabase.getInstance().getReference().child("Eleicao");
            mAuth = FirebaseAuth.getInstance();
            mRancking.keepSynced(true);

        }

        public void setBtn(final String chave_presidente){

            mRancking.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAuth.getCurrentUser() != null) {

                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                            contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                            btnVotar.setImageResource(R.drawable.ic_votar_red);

                            if (contador < 2) {
                                voto.setText(Integer.toString(contador));
                                txt.setText("voto");
                            } else {
                                voto.setText(Integer.toString(contador));
                                txt.setText("votos");
                            }
                        }else {

                            contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                            btnVotar.setImageResource(R.drawable.ic_votar);

                            if (contador < 2) {
                                voto.setText(Integer.toString(contador));
                                txt.setText("voto");
                            } else {
                                voto.setText(Integer.toString(contador));
                                txt.setText("votos");
                            }
                        }

                    }else {
                        contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                        if (contador < 2) {
                            voto.setText(Integer.toString(contador));
                            txt.setText("voto");
                        } else {
                            voto.setText(Integer.toString(contador));
                            txt.setText("votos");
                        }
                    }
                    mEleicao.child(chave_presidente).child("voto").setValue(voto.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setBtnSeguir(final String chave_presidente){
            if (mAuth.getCurrentUser() != null) {
                mSeguir.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                            btnSeguir.setImageResource(R.drawable.ic_favorito_amarelo);

                        } else {
                            btnSeguir.setImageResource(R.drawable.ic_favorito);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


        }
        public void setImagem(Context context, String imagem) {

            CircleImageView imagem_anuncio = (CircleImageView) mView.findViewById(R.id.imgRancking);
            Picasso.with(context).load(imagem).placeholder(R.drawable.default_avatar).into(imagem_anuncio);

        }

        public void setNome(String nome) {

            TextView textView = (TextView) mView.findViewById(R.id.candidato);
            textView.setText(nome);

        }

    }
}
