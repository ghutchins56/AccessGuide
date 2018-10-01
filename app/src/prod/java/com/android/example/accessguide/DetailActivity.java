package com.android.example.accessguide;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        WheelmapTask.WheelmapTaskListener, Response.Listener<JSONObject> {
    private PlaceItem PlaceItem;
    private PlaceDetailItem PlaceDetailItem;
    private static final String EXTRA_PLACE_ITEM = "placeItem";
    private static final String EXTRA_PLACE_DETAIL_ITEM = "placeDetailItem";
    private static final String EXTRA_PLACE_NAME = "placeName";
    private static final String EXTRA_ACCESS = "access";
    private static final String KEY_MAPVIEW_BUNDLE = "keyMapViewBundle";
    private MapView MapView;
    private static final String WHEELMAP_BASE_URL = "https://wheelmap.org/api/nodes?api_key=";
    private TextView TextViewAccess;
    private RecyclerView RecyclerViewReviewList;
    private ReviewListAdapter ReviewListAdapter;
    private String Access;
    private EditText EditTextReview;
    private Review Review;
    private FloatingActionButton FabEdit;
    private FloatingActionButton FabSave;
    private FirebaseUser User;

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference("reviews/"
                + PlaceItem.getPlaceId());
        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class).build();
        ReviewListAdapter = new ReviewListAdapter(options);
        RecyclerViewReviewList.setAdapter(ReviewListAdapter);
        RecyclerViewReviewList.setLayoutManager(new LinearLayoutManager(this));
        ReviewListAdapter.startListening();
        MapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ReviewListAdapter.stopListening();
        MapView.onStop();
    }

    @Override
    protected void onDestroy() {
        MapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = outState.getBundle(KEY_MAPVIEW_BUNDLE);
        if (bundle == null) {
            bundle = new Bundle();
            outState.putBundle(KEY_MAPVIEW_BUNDLE, bundle);
        }
        MapView.onSaveInstanceState(bundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        MapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapView.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        PlaceItem = intent.getParcelableExtra(EXTRA_PLACE_ITEM);
        PlaceDetailItem = intent.getParcelableExtra(EXTRA_PLACE_DETAIL_ITEM);
        Bundle bundle = null;
        if (savedInstanceState != null) bundle = savedInstanceState.getBundle(KEY_MAPVIEW_BUNDLE);
        MapView = findViewById(R.id.map_view);
        MapView.onCreate(bundle);
        MapView.getMapAsync(this);
        TextView textViewName = findViewById(R.id.text_view_name);
        TextView textViewAddress = findViewById(R.id.text_view_address);
        TextViewAccess = findViewById(R.id.text_view_access);
        EditTextReview = findViewById(R.id.edit_text_review);
        FabEdit = findViewById(R.id.fab_edit);
        FabSave = findViewById(R.id.fab_save);
        RecyclerViewReviewList = findViewById(R.id.recycler_view_review_list);
        textViewName.setText(PlaceDetailItem.getName());
        textViewAddress.setText(PlaceDetailItem.getAddress());
        User = FirebaseAuth.getInstance().getCurrentUser();
        RequestQueue queue = Volley.newRequestQueue(this);
        final LatLng latLng = PlaceDetailItem.getLatLng();
        String url = WHEELMAP_BASE_URL + BuildConfig.WheelmapApiKey + "&bbox="
                + (latLng.longitude - .001) + "," + (latLng.latitude - .001) + ","
                + (latLng.longitude + .001) + "," + (latLng.latitude + .001);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setAccess("");
                updateAppWidgets();
            }
        });
        queue.add(request);
    }

    public void onFabDeleteClick(View view) {
        PlaceDetailItem = null;
        updateAppWidgets();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLACE_ITEM, PlaceItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(PlaceDetailItem.getLatLng())
                .title(PlaceDetailItem.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(PlaceDetailItem.getLatLng()));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void updateAppWidgets() {
        Intent intent = new Intent(this, AppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(
                new ComponentName(getApplication(), AppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        if (PlaceDetailItem == null) {
            intent.putExtra(EXTRA_PLACE_NAME, "");
            intent.putExtra(EXTRA_ACCESS, "");
        } else {
            intent.putExtra(EXTRA_PLACE_NAME, PlaceDetailItem.getName());
            intent.putExtra(EXTRA_ACCESS, Access);
        }
        sendBroadcast(intent);
    }

    public void onFabEditClick(View view) {
        Review = ReviewListAdapter.getReview(User);
        if (Review == null) EditTextReview.setText("");
        else EditTextReview.setText(Review.getText());
        RecyclerViewReviewList.setVisibility(View.INVISIBLE);
        FabEdit.setVisibility(View.INVISIBLE);
        EditTextReview.setVisibility(View.VISIBLE);
        FabSave.setVisibility(View.VISIBLE);
    }

    public void onFabSaveClick(View view) {
        String text = EditTextReview.getText().toString();
        RecyclerViewReviewList.setVisibility(View.VISIBLE);
        FabEdit.setVisibility(View.VISIBLE);
        EditTextReview.setVisibility(View.INVISIBLE);
        FabSave.setVisibility(View.INVISIBLE);
        if (text.equals("")) {
            Review = null;
        } else {
            if (Review == null) Review = new Review(User.getUid(), text);
            else Review.setText(text);
        }
        FirebaseDatabase.getInstance().getReference("reviews/" + PlaceItem.getPlaceId() + "/"
                + User.getUid()).setValue(Review);
    }

    public void setAccess(String status) {
        switch (status) {
            case "":
                Access = getString(R.string.string_data_not_available);
                break;
            case "yes":
                Access = getString(R.string.string_wheelchair_access);
                Access += getString(R.string.string_yes);
                break;
            case "limited":
                Access = getString(R.string.string_wheelchair_access);
                Access += getString(R.string.string_limited);
                break;
            case "no":
                Access = getString(R.string.string_wheelchair_access);
                Access += getString(R.string.string_no);
                break;
            default:
                Access = getString(R.string.string_wheelchair_access);
                Access += getString(R.string.string_unknown);
        }
        TextViewAccess.setText(Access);
        updateAppWidgets();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray nodes = response.getJSONArray("nodes");
            if (nodes.length() == 0) {
                setAccess("");
            } else {
                WheelmapTask task = new WheelmapTask();
                task.setLatLng(PlaceDetailItem.getLatLng());
                task.setListener(this);
                task.execute(nodes);
            }
        } catch (JSONException e) {
            setAccess("");
        }
        updateAppWidgets();
    }
}
