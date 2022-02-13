package com.example.shop_it;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class oneProductActivity extends AppCompatActivity {
    ImageView productImage;
    TextView productName, productPrice, productCategory, productQuantity;
    Button addToCartBtn;
    ImageButton plus, minus;
    databaseHelper dbh;
    int customer_id, product_id, cart_id, maxProductQuantity;
    float product_price;
    Cursor productCursor, cartCursor, productCartCursor;
    String categoryName;
    boolean productExists;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oneproduct);

        productImage = (ImageView) findViewById(R.id.productImageOP);
        productName = (TextView) findViewById(R.id.productNameOP);
        productPrice = (TextView) findViewById(R.id.productPriceOP);
        productCategory = (TextView) findViewById(R.id.productCategoryOP);
        productQuantity = (TextView) findViewById(R.id.productQuantityOP);
        addToCartBtn = (Button) findViewById(R.id.addToCartBtnOP);
        plus = (ImageButton) findViewById(R.id.quantityPlusBtn);
        minus = (ImageButton) findViewById(R.id.quantityMinusBtn);
        dbh = new databaseHelper(this);
        customer_id = getIntent().getExtras().getInt("customer_id");
        product_id = getIntent().getExtras().getInt("product_id");
        //get product info from database
        productCursor = dbh.get_product_data(product_id);
        //set product info and category name in their views
        //0-id, 1-name, 2-price, 3-quantity, 4-image, 5-category
        productName.setText(productCursor.getString(1));
        product_price = Float.parseFloat(productCursor.getString(2));
        maxProductQuantity = productCursor.getInt(3);
        productImage.setImageResource(productCursor.getInt(4));
        //get category name from database
        categoryName = "";
        categoryName = dbh.get_category_name(productCursor.getInt(5));
        productCategory.setText(categoryName);
        //check if this product already exists in the customer cart
        cartCursor = dbh.get_customer_cart(customer_id);
        cart_id = cartCursor.getInt(0);
        productCartCursor = dbh.get_cart_product_data(cart_id, product_id);
        if(productCartCursor.getCount() != 0){
            productExists = true;
            productPrice.setText(productCartCursor.getString(3) + " L.E");
            productQuantity.setText(productCartCursor.getString(2));
        }
        else {
            productExists = false;
            productPrice.setText(product_price + " L.E");
            productQuantity.setText("1");
        }
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(productQuantity.getText().toString());
                if(q < maxProductQuantity)
                    q++;
                productQuantity.setText(String.valueOf(q));
                productPrice.setText(String.valueOf( q * product_price) + " L.E");
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(productQuantity.getText().toString());
                if(q > 1)
                    q--;
                productQuantity.setText(String.valueOf(q));
                productPrice.setText(String.valueOf( q * product_price) + " L.E");
            }
        });
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int product_quantity= Integer.parseInt(productQuantity.getText().toString());
                float product_price= productCursor.getFloat(2);
                if(productExists)
                    dbh.remove_product_from_cart(customer_id, cart_id, product_id);
                dbh.add_product_to_cart(customer_id, product_id, product_quantity, product_price);
                Toast.makeText(getApplicationContext(), "Product added to cart", Toast.LENGTH_LONG).show();
            }
        });
    }
}
