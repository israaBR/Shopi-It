package com.example.shop_it;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

class Product{
    String name;
    int id, image, quantity;
    float price;
    Product(){}
    Product(int pro_id, String pro_name, int pro_image, float pro_price, int pro_quantity){
        id = pro_id;
        name = pro_name;
        image = pro_image;
        price = pro_price;
        quantity = pro_quantity;
    }
}

class productsAdapter extends ArrayAdapter<Product> {
    private Context mcontext;
    private int mresource;
    Product[] product;
    public productsAdapter(@NonNull Context context, int resource, Product[] objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        product=objects;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);
        ImageView image= (ImageView) convertView.findViewById(R.id.productImageView);
        TextView name = (TextView) convertView.findViewById(R.id.productNameView);
        TextView price = (TextView) convertView.findViewById(R.id.productPriceView);
        image.setImageResource(product[position].image);
        name.setText(product[position].name);
        price.setText(String.valueOf(product[position].price) + " L.E");

        return convertView;
    }
}

public class productCategoryActivity extends AppCompatActivity {

    GridView productsGrid;
    productsAdapter adapter;
    databaseHelper dbh;
    Cursor productsCursor;
    int customer_id, category_id;
    Product[] product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productcategory);

        customer_id = getIntent().getExtras().getInt("customer_id");
        category_id = getIntent().getExtras().getInt("category_id");
        dbh = new databaseHelper(this);
        productsCursor = dbh.get_products(category_id);
        //0-id 1-name 2-price 3-quantity 4-image 5-category_id
        product = new Product[productsCursor.getCount()];
        for(int i = 0; i<productsCursor.getCount() ; i++) {
            product[i] = new Product(productsCursor.getInt(0), productsCursor.getString(1), productsCursor.getInt(4), productsCursor.getFloat(2), 0);
            productsCursor.moveToNext();
        }
        productsGrid = (GridView) findViewById(R.id.productsGV);
        adapter = new productsAdapter(this, R.layout.category_products_list_row, product);
        productsGrid.setAdapter(adapter);

        productsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(productCategoryActivity.this, oneProductActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("product_id", product[position].id);
                startActivity(intent);
            }
        });
    }
}
