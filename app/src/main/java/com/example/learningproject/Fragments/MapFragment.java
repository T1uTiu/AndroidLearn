package com.example.learningproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.learningproject.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    MapView mapView;
    View rootView;
    public MapFragment() {
        // Required empty public constructor
    }
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        TencentMap tencentMap = mapView.getMap();
        LatLng position = new LatLng(22.249942,113.534341);
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newLatLngZoom(
                        position,
                        17
                );
        tencentMap.moveCamera(cameraSigma);
        Marker centerMarker = tencentMap.addMarker(new MarkerOptions(position).title("暨南大学珠海校区"));
        centerMarker.setClickable(true);
        tencentMap.setOnMarkerClickListener(marker -> {
            if(marker.getId().equals(centerMarker.getId())) {
                System.out.println("暨南大学珠海校区");
                Toast.makeText(rootView.getContext(), "暨南大学珠海校区", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = new MapView(rootView.getContext());
        // 把mapView添加到rootView中
        ((ViewGroup)rootView).addView(mapView);

        return rootView;
    }
}