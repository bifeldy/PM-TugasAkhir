package id.ac.umn.pm_tugasakhir;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    View v;
    RecyclerView recyclerView;
    private List<Product> products;

    public ProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.product_layout, container, false);

        products = new ArrayList<>();
        products.add(new Product("Socket Pipa", R.drawable.socket_pipa, "Sambungan pipa 1/2 inch", "Pralon", 12, 12000));
        products.add(new Product("Lampu", R.drawable.lampu, "Putih", "Lampu Kecil", 23, 23000));
        products.add(new Product("Antena", R.drawable.antena, "Antena Indoor", "Antena", 34, 34000));
        products.add(new Product("Kipas Angin", R.drawable.kipas, "Kipas Stan", "Cosmos", 45, 45000));

        recyclerView = v.findViewById(R.id.product_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(new ProductRecyclerAdapter(getActivity(), R.layout.product, products));

        return v;
    }

}
