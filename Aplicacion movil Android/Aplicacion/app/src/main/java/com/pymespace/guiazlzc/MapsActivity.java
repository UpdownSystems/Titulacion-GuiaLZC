package com.pymespace.guiazlzc;
/*
  Echo por: Updown Systems
  Programado por: Jorge Luis Perez Medina
*/
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ZoomControls;

import com.google.android.gms.maps.UiSettings;
import com.pymespace.guiazlzc.models.Post;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    public static final String EXTRA_POST_KEY = "post_key";
    private String mPostKey;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private ArrayList<Marker> mapRealtimeMarker = new ArrayList<>();
    private ArrayList<Marker> realtimeMarkers = new ArrayList<>();
    private FirebaseDatabase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ref = FirebaseDatabase.getInstance();
        //mDatabase = ref.getReference("posts").child(mPostKey);

        //Nuevo agregado
        // Obtener la clave de la publicación del Intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(Marker marker:realtimeMarkers){
                    marker.remove();
                }

                Post mapa = dataSnapshot.getValue(Post.class);
                MarkerOptions markerOptions = new MarkerOptions();

                String lat = mapa.getLatitud();
                String lon = mapa.getLongitud();
                Double la = Double.parseDouble(lat);
                Double lo = Double.parseDouble(lon);


                markerOptions.position(new LatLng(la,lo));
                mapRealtimeMarker.add(mMap.addMarker(markerOptions));

                realtimeMarkers.clear();
                realtimeMarkers.addAll(mapRealtimeMarker);
                mMap.setMyLocationEnabled (true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", "No se puedo obtener los datos de la BD de Firebase :(");
            }
        });

        /*mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(Marker marker:realtimeMarkers){
                    marker.remove();
                }

                String keyy = "mikey";

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for(DataSnapshot snap: snapshot.getChildren()) {
                        Maps mapa = snap.getValue(Maps.class);
                        MarkerOptions markerOptions = new MarkerOptions();

                        double lat = mapa.getLatitud();
                        //Double la = Double.valueOf(lat);


                        double lon = mapa.getLongitud();
                        //Double lo = Double.valueOf(lon);
                    Double latitud = mapa.getLatitud();
                    Double longitud = mapa.getLongitud();


                        markerOptions.position(new LatLng(lat,lon));
                        mapRealtimeMarker.add(mMap.addMarker(markerOptions));
                    }
                }

                realtimeMarkers.clear();
                realtimeMarkers.addAll(mapRealtimeMarker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }
}
