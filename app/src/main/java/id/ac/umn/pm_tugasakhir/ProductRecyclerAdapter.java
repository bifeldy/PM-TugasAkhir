package id.ac.umn.pm_tugasakhir;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ProductRecyclerViewHolder> implements Filterable {

    private List<Product> products;
    private List<Product> productsFull;
    private Context recyclerContext;
    private int rowLayout;

    public ProductRecyclerAdapter(Context recyclerContext, int rowLayout, List<Product> products) {
        this.recyclerContext = recyclerContext;
        this.rowLayout = rowLayout;
        this.products = products;
        this.productsFull = new ArrayList<>(products);
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ProductRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(this.recyclerContext);
        View view = inflater.inflate(rowLayout, viewGroup, false);

        return new ProductRecyclerAdapter.ProductRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerAdapter.ProductRecyclerViewHolder productRecyclerViewHolder, int i) {
        final Product product = products.get(i);

        productRecyclerViewHolder.productImage.setImageResource(product.getProductImage());
        productRecyclerViewHolder.productName.setText(product.getProductName());
        productRecyclerViewHolder.productDescription.setText(product.getProductDescription());
        productRecyclerViewHolder.productStock.setText("Stock: " + Integer.toString(product.getProductStock()));
    }

    @Override
    public int getItemCount() {
        return (products != null) ? products.size() : 0;
    }

    @Override
    public Filter getFilter() {
        // Buat Search Gan ..
        return booksFilter;
    }

    private Filter booksFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(productsFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Product item : productsFull) {
                    if(item.getProductName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            products.clear();
            products.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ProductRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productDescription, productCategory, productStock, productPrice;
        private ImageView productImage;

        public ProductRecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productImage = itemView.findViewById(R.id.product_image);
            productStock = itemView.findViewById(R.id.product_stock);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Intent intent = new Intent(recyclerContext, DetailsActivity.class);
                    // Passing ID Buku
                    // intent.putExtra("asin", books.get(position).getAsin());
                    // recyclerContext.startActivity(intent);
                }
            });
        }
    }
}