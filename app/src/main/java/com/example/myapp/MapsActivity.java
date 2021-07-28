package com.example.myapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsActivity extends Fragment implements OnMapReadyCallback {

    GoogleMap map;

    public MapsActivity() {
        // Required empty public constructor
    }

    public static MapsActivity newInstance(String param1, String param2) {
        MapsActivity fragment = new MapsActivity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_usfragment, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        map = googleMap;


        double[] latitude = new double[]{36.373884, 36.373458, 36.373518, 36.372860, 36.372959, 36.373840, 36.373581, 36.371666, 36.369956, 36.370829};
        double[] longitude = new double[]{127.365274, 127.363476, 127.364477, 127.364286,127.362314, 127.361542, 127.360196, 127.364707, 127.366219, 127.362636};

        // for loop를 통한 n개의 마커 생성
        for (int idx = 0; idx < 10; idx++) {
            // 1. 마커 옵션 설정 (만드는 과정)
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                    .position(new LatLng(latitude[idx], longitude[idx]))
                    .title("킥보드" + idx); // 타이틀.
            map.moveCamera(CameraUpdateFactory.zoomTo(15));

            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.kickkick);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            map.addMarker(markerOptions);
        }
        //정보창 클릭 리스너
        map.setOnInfoWindowClickListener(infoWindowClickListener);

        //마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.373884, 127.365274)));
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            Toast.makeText(getActivity().getApplicationContext(), "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
        }
    };

    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            //Toast.makeText(getActivity().getApplicationContext(), "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity().getApplicationContext(), KickBoard.class);
            startActivity(intent);
            return false;
        }
    };

}