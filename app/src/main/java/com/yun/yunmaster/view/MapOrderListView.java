package com.yun.yunmaster.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.utils.LocationManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/4/2.
 */

public class MapOrderListView extends RelativeLayout {
    @BindView(R.id.mapView)
    MapView mapView;
    private Context mContext;
    private ArrayList<OrderItem> orderList = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private InfoWindow mInfoWindow;

    public MapOrderListView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MapOrderListView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.map_order_list_view, this);
        ButterKnife.bind(this);

        mapView.getMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapView.getMap().hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mapView.getMap().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showMarkerFire(marker);
                return false;
            }
        });
    }

    public void setOrderList(List<OrderItem> list) {
        this.orderList.clear();
        this.orderList.addAll(list);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(this.orderList.size() == 0){
            mapView.getMap().hideInfoWindow();
            for (int i = 0; i < markerList.size(); i++){
                Marker marker = markerList.get(i);
                marker.remove();
            }
            LocationManager.getLocation(new LocationManager.LocationListener() {
                @Override
                public void onLocationSuccess(BDLocation location) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(myLocation));
                }

                @Override
                public void onLocationFail() {

                }
            });
        }
        else {
            for (int i = 0; i < this.orderList.size(); i++) {
                OrderItem orderItem = orderList.get(i);
                addMarker(orderItem, false);
                double lat = orderItem.detail_address.lat;
                double lng = orderItem.detail_address.lng;
                builder.include(new LatLng(lat, lng));
            }
            LatLngBounds bounds = builder.build();
            mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds));
        }
    }

    public void receiveNewOrder(OrderItem orderItem) {
        addOrder(orderItem);
    }

    public void addOrder(OrderItem order) {
        this.orderList.add(0, order);
        addMarker(order, true);
    }

    private void addMarker(final OrderItem orderPickInfo, boolean isNew) {
        LatLng point = new LatLng(orderPickInfo.detail_address.lat, orderPickInfo.detail_address.lng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.order_pin);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        Marker marker = (Marker) mapView.getMap().addOverlay(option);
        markerList.add(marker);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", orderPickInfo);
        marker.setExtraInfo(bundle);
        if (isNew) {
            showMarkerFire(marker);
        }
    }

    private void showMarkerFire(Marker marker) {
        final OrderItem order = (OrderItem) marker.getExtraInfo().getSerializable("order");
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT);
        MarkerFireView fireView = new MarkerFireView(mContext);
        fireView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailActivity.intentTo(mContext, order.oid);
            }
        });
        fireView.setLayoutParams(layoutParams);
        fireView.setOrder(order);
        LatLng point = marker.getPosition();
        mInfoWindow = new InfoWindow(fireView, point, -47);
        mapView.getMap().showInfoWindow(mInfoWindow);
        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(point));
    }
}
