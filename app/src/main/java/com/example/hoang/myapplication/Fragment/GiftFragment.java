package com.example.hoang.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hoang.myapplication.Adapter.GiftArrayAdapter;
import com.example.hoang.myapplication.Adapter.MyArrayAdapter;
import com.example.hoang.myapplication.InstanceVariants;
import com.example.hoang.myapplication.Model.Gift;
import com.example.hoang.myapplication.R;
import com.example.hoang.myapplication.UI.InformationDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.Map;

public class GiftFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView list;
    private GiftArrayAdapter adapter;
    private ArrayList<Gift> giftArrayList = new ArrayList<>();
    private FirebaseUser user;
    private ConstraintLayout groupNoGift;
    private TextView txtGiftno;

    public GiftFragment() {
    }

    public static GiftFragment newInstance(int sectionNumber) {
        GiftFragment fragment = new GiftFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gift, container, false);
        return rootView;
    }

    private void loadGiftData() {
        final CatLoadingView mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);
        mView.show(getActivity().getSupportFragmentManager(), "Loading your data");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        DatabaseReference refGift = FirebaseDatabase.getInstance().getReference().child(InstanceVariants.CHILD_GIFT);
        refGift.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    giftArrayList.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) issue.getValue();
                        Gift gift = new Gift();

                        if (map.get("gifId") != null) gift.setGifId(map.get("gifId").toString());
                        if (map.get("gifName") != null)
                            gift.setGifName(map.get("gifName").toString());
                        if (map.get("giftContent") != null)
                            gift.setGiftContent(map.get("giftContent").toString());
                        if (map.get("giftTime") != null)
                            gift.setGiftTime((long) map.get("giftTime"));
                        if (map.get("giftLink") != null)
                            gift.setGiftLink(map.get("giftLink").toString());
                        if (map.get("imgUrl") != null) gift.setImgUrl(map.get("imgUrl").toString());
                        if (map.get("gifExpire") != null)
                            gift.setGifExpire((long) map.get("gifExpire"));
                        giftArrayList.add(gift);
                    }
                    adapter.notifyDataSetChanged();
                    groupNoGift.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    txtGiftno.setVisibility(View.VISIBLE);
                    mView.dismiss();
                } else {
                    groupNoGift.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    txtGiftno.setVisibility(View.GONE);
                    mView.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        list = (ListView) getView().findViewById(R.id.list_gift);
        adapter = new GiftArrayAdapter(getActivity(), R.layout.item_gift, giftArrayList);
        groupNoGift = (ConstraintLayout) getView().findViewById(R.id.groupNoGift);
        txtGiftno = (TextView) getView().findViewById(R.id.txtGiftn);

        list.setAdapter(adapter);
        loadGiftData();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), InformationDetailActivity.class);
                intent.putExtra("package", giftArrayList.get(position));
                startActivity(intent);
            }
        });

    }

}