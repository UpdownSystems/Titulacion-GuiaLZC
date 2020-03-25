package com.pymespace.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina
 */
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pymespace.guiazlzc.adapters.categoriaAdapter;
import com.pymespace.guiazlzc.fragment.RecentPostsFragment;
import com.pymespace.guiazlzc.models.User;
import com.pymespace.guiazlzc.models.categorias;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class navigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // *** RecyclerView rv;
    private ArrayList<categorias> categoriasList = new ArrayList<>();
    private categoriaAdapter madapter;
    private TextView txtNegocioView, txtEmailView;
    private String userId;

    // Firebase
    private DatabaseReference dtbLocation;
    private DatabaseReference db,db2;
    private FirebaseDatabase refa;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private MenuItem btnPerfil;

    private Query mQuery;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtNegocioView =  navigationView.getHeaderView(0).findViewById(R.id.textNegocio);
        txtEmailView = navigationView.getHeaderView(0).findViewById(R.id.textEmail);

        mAuth = FirebaseAuth.getInstance();

        LinearLayout empty_page = (LinearLayout)findViewById(R.id.view_empty);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);

        // *** rv = (RecyclerView)findViewById(R.id.reciclerView);
        // *** rv.setLayoutManager(new LinearLayoutManager(this));

        // Query para obtener  ordenar las categorias
        /* *** Query dtbLocation = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("categoria");
        dtbLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoriasList.removeAll(categoriasList);

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String texto = snapshot.child("categoria").getValue().toString();
                    categoriasList.add(new categorias(texto));
                }
                madapter = new categoriaAdapter(categoriasList, R.layout.categorias_view);
                rv.setAdapter(madapter);
                madapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        // Genero mis Tabs
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentPostsFragment()
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_recent),
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        // Configure ViewPager con el adaptador de secciones.
        mViewPager = findViewById(R.id.containerPerfil);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabsPerfil);
        tabLayout.setupWithViewPager(mViewPager);

        //
        refa = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Obtener datos de la BD y Setearlos en el Navigation Drawer
        db = refa.getReference("usuarios").child(user.getUid());
        db2 = refa.getReference("usuarios").child(user.getUid()).child("sesion");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarios = dataSnapshot.getValue(User.class);
                //DatabaseReference sesion1=db2;
                String sesion=null;
                if(usuarios!=null)
                {
                    sesion=usuarios.sesion;
                }

                if(usuarios == null) {
                    txtNegocioView.setText("Para continuar debes registrarte");
                    txtEmailView.setText(null);
                } else {
                    if(sesion.equals("1"))
                    {
                        txtNegocioView.setText(usuarios.nombreNegocio);
                        txtEmailView.setText(usuarios.email);

                        Log.d("perfil", "nombre_negocio:" + usuarios.nombreNegocio);
                        Log.d("perfil", "email:" + usuarios.email);
                    }
                    else if(sesion.equals("0"))
                    {
                        txtNegocioView.setText("La suscripcion a caducado");
                        txtEmailView.setText(null);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });


        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);*/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("posts").startAt(searchText).orderByChild("categoria").endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder> (
                Model.class,
                R.layout.content_navigation_drawer,
                ViewHolder.class,
                firebaseSearchQuery) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }
        };
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarios = dataSnapshot.getValue(User.class);
                String sesion=null;
                if(usuarios!=null)
                {
                    sesion=usuarios.sesion;
                }

                if(usuarios == null) {

                    navigationView.getMenu().setGroupVisible(R.id.group_anonymous, true);
                    navigationView.getMenu().setGroupVisible(R.id.group_admin, false);

                } else {
                    if(sesion.equals("1"))
                    {
                        navigationView.getMenu().setGroupVisible(R.id.group_anonymous, false);
                        navigationView.getMenu().setGroupVisible(R.id.group_admin, true);
                    }
                    else
                    {
                        navigationView.getMenu().setGroupVisible(R.id.group_anonymous, true);
                        navigationView.getMenu().setGroupVisible(R.id.group_admin, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos  :(");
            }
        });

       // MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView)searchItem.getActionView();

       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("categoria").startAt(s).endAt(s + "\uf8ff");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("categoria").startAt(s).endAt(s + "\uf8ff");
                return false;
            }
        });*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneje los elementos de la barra de acción haciendo clic aquí La barra de acción
        // maneja automáticamente los clics en el botón Inicio / Arriba, tanto tiempo
        // al especificar una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        /*if (id == R.id.action_search) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*public void onCheckAuth(){
        userId = user.getUid();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarios = dataSnapshot.getValue(User.class);

                if(usuarios == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(navigationDrawer.this);
                    builder.setMessage("Tiene que iniciar sesion para continuar").setTitle("Inicia sesion");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    startActivity(new Intent(navigationDrawer.this, perfil.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Haga clic aquí para ver los elementos de la vista de navegación.
        int id = item.getItemId();

        if (id == R.id.home) {

        } else if (id == R.id.nav_posted) {
            startActivity(new Intent(navigationDrawer.this, TabsMisNegociosActivity.class));
        }
        else if (id == R.id.nav_cuenta ) {
            //onCheckAuth();
            startActivity(new Intent(navigationDrawer.this, perfil.class));
        } else if(id == R.id.nav_exit_app) {
            mAuth.getInstance().signOut();
            startActivity(new Intent(navigationDrawer.this, MainActivity.class));
            finish();

            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

