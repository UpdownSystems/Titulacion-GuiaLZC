package com.example.guiazlzc;
/*
 * Echo por: Updown Systems
 * Programado por: Jorge Luis Pérez Medina        absoleto
 */
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.guiazlzc.fragment.MyPostsPerfilFragment;
import com.example.guiazlzc.fragment.MyTopPostsFragment;

public class MisPostsActivity extends AppCompatActivity {
    String userId;
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_posts);

        Toolbar toolbar = findViewById(R.id.toolbar_perfil);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Genero mis Tabs
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new MyPostsPerfilFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_publicaciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneje los elementos de la barra de acción haciendo clic aquí La barra de acción
        // maneja automáticamente los clics en el botón Inicio / Arriba, tanto tiempo
        // al especificar una actividad principal en AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.fabNewPost) {
            startActivity(new Intent(MisPostsActivity.this, NewPostActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
