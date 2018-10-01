package com.android.example.accessguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity implements PlaceListAdapter.OnItemClickListener {
    /*
    private static final int REQUEST_SIGN_IN = 1;
    */
    private static final int REQUEST_ADD_PLACE = 2;
    private static final int REQUEST_DELETE_PLACE = 3;
    private PlaceDetailItems PlaceDetailItems;
    private PlaceListAdapter PlaceListAdapter;
    private RecyclerView RecyclerViewPlaceList;
    private ImageView ImageViewWheelchairRamp;
    private ImageView ImageViewPoweredByGoogle;
    private static final String EXTRA_PLACE_ITEM = "placeItem";
    private static final String EXTRA_PLACE_DETAIL_ITEM = "placeDetailItem";
    private PlaceItem placeItemToDelete = null;
    private CountingIdlingResource IdlingResource;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /*
            case REQUEST_SIGN_IN:
                if (resultCode != RESULT_OK) startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder().build(), REQUEST_SIGN_IN);
                break;
            */
            case REQUEST_ADD_PLACE:
                if (resultCode == RESULT_OK) {
                    /*
                    Place place = PlacePicker.getPlace(this, data);
                    String id = place.getId();
                    */
                    String id = "ChIJaTv4NzuHT4YRwZv7HIPeKUk";
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(
                            "users/" + FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).push();
                    reference.setValue(new PlaceItem(reference.getKey(), id));
                    break;
                }
            case REQUEST_DELETE_PLACE:
                if (resultCode == RESULT_OK) placeItemToDelete
                        = data.getParcelableExtra(EXTRA_PLACE_ITEM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerViewPlaceList = findViewById(R.id.recycler_view_place_list);
        ImageViewWheelchairRamp = findViewById(R.id.image_view_wheelchair_ramp);
        ImageViewPoweredByGoogle = findViewById(R.id.image_view_powered_by_google);
        PlaceDetailItems = new PlaceDetailItems();
        PlaceDetailItems.setClient(Places.getGeoDataClient(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if ((user != null) && (placeItemToDelete != null)) {
            FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/"
                    + placeItemToDelete.getPlaceItemId()).removeValue();
            placeItemToDelete = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            /*
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                    REQUEST_SIGN_IN);
            */
            FirebaseAuth.getInstance().signInWithEmailAndPassword("testuser@android.com",
                    "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    IdlingResource.decrement();
                }
            });
        } else {
            Query query = FirebaseDatabase.getInstance().getReference("users/"
                    + user.getUid());
            FirebaseRecyclerOptions<PlaceItem> options = new FirebaseRecyclerOptions
                    .Builder<PlaceItem>().setQuery(query, PlaceItem.class).build();
            PlaceListAdapter = new PlaceListAdapter(options);
            PlaceDetailItems.setAdapter(PlaceListAdapter);
            PlaceListAdapter.setRecyclerViewPlaceList(RecyclerViewPlaceList);
            PlaceListAdapter.setImageViewWheelchairRamp(ImageViewWheelchairRamp);
            PlaceListAdapter.setImageViewPoweredByGoogle(ImageViewPoweredByGoogle);
            PlaceListAdapter.setPlaceDetailItems(PlaceDetailItems);
            PlaceListAdapter.setListener(this);
            RecyclerViewPlaceList.setAdapter(PlaceListAdapter);
            RecyclerViewPlaceList.setLayoutManager(new LinearLayoutManager(this));
            PlaceListAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (PlaceListAdapter != null) PlaceListAdapter.stopListening();
    }

    public void onFabAddClick(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                Intent intent = new PlacePicker.IntentBuilder().build(this);
                startActivityForResult(intent, REQUEST_ADD_PLACE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void onItemClicked(PlaceItem placeItem) {
        Intent intent = new Intent(this, DetailActivity.class);
        PlaceDetailItem placeDetailItem = PlaceDetailItems.getItem(placeItem.getPlaceId());
        intent.putExtra(EXTRA_PLACE_ITEM, placeItem);
        intent.putExtra(EXTRA_PLACE_DETAIL_ITEM, placeDetailItem);
        startActivityForResult(intent, REQUEST_DELETE_PLACE);
    }

    public void setIdlingResource(CountingIdlingResource resource) {
        IdlingResource = resource;
    }
}
