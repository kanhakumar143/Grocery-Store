package com.kanhakumar.modokan.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kanhakumar.modokan.R;
import com.kanhakumar.modokan.adapter.HomeAdapter;
import com.kanhakumar.modokan.adapter.PopularAdapter;
import com.kanhakumar.modokan.adapter.RecommendedAdapter;
import com.kanhakumar.modokan.adapter.ViewAllAdapter;
import com.kanhakumar.modokan.model.HomeCategory;
import com.kanhakumar.modokan.model.PopularModel;
import com.kanhakumar.modokan.model.RecommendedModel;
import com.kanhakumar.modokan.model.ViewAllModel;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class HomeFragment extends Fragment {

    ScrollView scrollView;
    ProgressBar progressBar;

    RecyclerView popularRec, homeCatRec, recommended_rec;
    FirebaseFirestore db;

//    Popular Items
    List<PopularModel> popularModelList;
    PopularAdapter popularAdapter;
//    Home Category
    List<HomeCategory> categoryList;
    HomeAdapter homeAdapter;
//    Recommendation
    List<RecommendedModel> recommendedModelList;
    RecommendedAdapter recommendedAdapter;
//    Search View
    EditText search_box;
    private List<ViewAllModel> viewAllModelList;
    private RecyclerView recyclerViewSearch;
    private ViewAllAdapter viewAllAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        popularRec = root.findViewById(R.id.pop_rec);
        homeCatRec = root.findViewById(R.id.explore_rec);
        recommended_rec = root.findViewById(R.id.recommended_rec);
        scrollView = root.findViewById(R.id.scroll_view);
        progressBar = root.findViewById(R.id.progressbar);

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

// Popular Items
        popularRec.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL, false));
        popularModelList = new ArrayList<>();
        popularAdapter = new PopularAdapter(getActivity(), popularModelList);
        popularRec.setAdapter(popularAdapter);

        db.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                                popularAdapter.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);

                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//  Home Category
        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL, false));
        categoryList = new ArrayList<>();
        homeAdapter = new HomeAdapter(getActivity(), categoryList);
        homeCatRec.setAdapter(homeAdapter);

        db.collection("HomeCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                HomeCategory homeCategory = document.toObject(HomeCategory.class);
                                categoryList.add(homeCategory);
                                homeAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//   Recommendation
        recommended_rec.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL, false));
        recommendedModelList = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(getActivity(), recommendedModelList);
        recommended_rec.setAdapter(recommendedAdapter);

        db.collection("Recommended")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                RecommendedModel recommendedModel = document.toObject(RecommendedModel.class);
                                recommendedModelList.add(recommendedModel);
                                recommendedAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//   Search View
        recyclerViewSearch = root.findViewById(R.id.search_rec);
        search_box = root.findViewById(R.id.search_box);
        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(getContext(),viewAllModelList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(viewAllAdapter);
        recyclerViewSearch.setHasFixedSize(true);

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().isEmpty()){
                    viewAllModelList.clear();
                    viewAllAdapter.notifyDataSetChanged();
                }else {
                    searchProduct(s.toString());
                }

            }
        });

        return root;
    }

    private void searchProduct(String type) {

        if (!type.isEmpty()){
            db.collection("AllProducts").whereEqualTo("type", type).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null){
                                viewAllModelList.clear();
                                viewAllAdapter.notifyDataSetChanged();

                                for (DocumentSnapshot doc: task.getResult().getDocuments()){
                                    ViewAllModel viewAllModel = doc.toObject(ViewAllModel.class);
                                    viewAllModelList.add(viewAllModel);
                                    viewAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

    }
}