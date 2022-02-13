
package com.example.shop_it;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


class cartAdapter extends ArrayAdapter<Product> {
    private Context mcontext;
    private int mresource;
    Product[] product;
    public cartAdapter(@NonNull Context context, int resource, Product[] objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        product=objects;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);
        ImageView image= (ImageView) convertView.findViewById(R.id.productImageCV);
        TextView name = (TextView) convertView.findViewById(R.id.productNameCV);
        TextView price = (TextView) convertView.findViewById(R.id.productPriceCV);
        TextView quantity = (TextView) convertView.findViewById(R.id.productQuantityCV);
        image.setImageResource(product[position].image);
        name.setText(product[position].name);
        price.setText(String.valueOf(product[position].price) + " L.E");
        quantity.setText(String.valueOf(product[position].quantity));
        return convertView;
    }
}

public class cartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public cartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static cartFragment newInstance(String param1, String param2) {
        cartFragment fragment = new cartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ListView productsList;
    TextView paymentMethodTxt, totalPriceTxt, deliveryAddressTxt;
    Button confirmOrder;
    databaseHelper dbh;
    Product[] product;
    cartAdapter adapter;
    int customer_id, cart_id;
    boolean submitOrder;
    String paymentMethod, creditcardNumber;
    Cursor customerCursor, cartCursor, cartProductsCursor, productInfoCursor;
    JavaMailAPI javaMailAPI;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        productsList = (ListView) view.findViewById(R.id.cartProductsLV);
        paymentMethodTxt = (TextView) view.findViewById(R.id.paymentMethod);
        totalPriceTxt = (TextView) view.findViewById(R.id.totalPricetxt);
        deliveryAddressTxt = (TextView) view.findViewById(R.id.chooseDeliveryAddress);
        confirmOrder = (Button) view.findViewById(R.id.confirmOrderbtn);
        registerForContextMenu(productsList);
        dbh = new databaseHelper(getActivity());
        Bundle data = getArguments();
        if(data != null){
            customer_id = data.getInt("customer_id");
        }

        loadCart();//load list of products in the cart

        paymentMethodTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentMethod();
            }
        });
        deliveryAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder = true;
                //check cart is not empty
                if(productsList.getCount() == 0) {
                    Toast.makeText(getActivity(), "Can't submit order, your cart is empty", Toast.LENGTH_LONG).show();
                    submitOrder = false;
                }
                //check delivery address is not empty
                if(cartCursor.getString(2).equals(" ")) {
                    Toast.makeText(getActivity(), "Can't submit order, set the delivery address", Toast.LENGTH_LONG).show();
                    submitOrder = false;
                }
                //check payment method is not empty
                if(cartCursor.getString(3).equals(" ")) {
                    Toast.makeText(getActivity(), "Can't submit order, select a payment method", Toast.LENGTH_LONG).show();
                    submitOrder = false;
                }
                //submit order
                if(submitOrder) {
                    dbh.submit_order(cart_id, customer_id);
                    //send a confirmation email
                    customerCursor = dbh.get_customer_data(customer_id);
                    String subject = "Order Confirmation";
                    String message = "Dear " + customerCursor.getString(1) + ", Thank you for shopping with Shop-It, Your order will be shipped soon.";
                    javaMailAPI = new JavaMailAPI(getActivity(), customerCursor.getString(2), subject, message);
                    javaMailAPI.execute();
                    loadCart();
                }
            }
        });
        return view;
    }
    void loadCart() {
        cartCursor = dbh.get_customer_cart(customer_id);
        cart_id = cartCursor.getInt(0);
        cartProductsCursor = dbh.get_cart_products(cart_id);
        if(cartProductsCursor.getCount() == 0)
            Toast.makeText(getActivity(), "Your cart is empty", Toast.LENGTH_LONG).show();

        //0-id 1-name 2-price 3-quantity 4-image 5-category_id
        product = new Product[cartProductsCursor.getCount()];
        for (int i = 0; i < cartProductsCursor.getCount(); i++) {
            productInfoCursor = dbh.get_product_data(cartProductsCursor.getInt(1));
            product[i] = new Product(productInfoCursor.getInt(0), productInfoCursor.getString(1), productInfoCursor.getInt(4), productInfoCursor.getFloat(2), cartProductsCursor.getInt(2));
            cartProductsCursor.moveToNext();
        }
        adapter = new cartAdapter(getActivity(), R.layout.cart_products_list_row, product);
        productsList.setAdapter(adapter);
        totalPriceTxt.setText(cartCursor.getString(1) + " L.E");
        if(!cartCursor.getString(2).equals(" ")) //display delivery address if selected
            deliveryAddressTxt.setText(cartCursor.getString(2));
        if(!cartCursor.getString(3).equals(" ")) //display payment method if selected
            paymentMethodTxt.setText(cartCursor.getString(3));
    }
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.cart_product_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        switch(item.getItemId()){
            case R.id.editQuantityMenuItem: {
                Intent intent = new Intent(getActivity(), oneProductActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("product_id", product[index].id);
                startActivity(intent);
                break;
            }
            case R.id.removeProductMenuItem: {
                dbh.remove_product_from_cart(customer_id, cart_id, product[index].id);
                loadCart();
                break;
            }
            default:
                break;
        }
        return true;
    }
    void getPaymentMethod() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.payment_method_dialog);
        final RadioButton cash = dialog.findViewById(R.id.cashRadio);
        final RadioButton creditcard = dialog.findViewById(R.id.visaRadio);
        final EditText creditcardNumberTxt = dialog.findViewById(R.id.creditCardNumberTxt);
        Button savebtn = dialog.findViewById(R.id.savePaymentInfoBtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cash.isChecked()) {
                    paymentMethod = "cash";
                    dbh.update_cart_payment_method(cart_id, paymentMethod, creditcardNumber);
                }//cash method is selected
                else if (creditcard.isChecked()) {
                    paymentMethod = "credit card";
                    creditcardNumber = creditcardNumberTxt.getText().toString();
                    if (creditcardNumber.length() == 16) {
                        dbh.update_cart_payment_method(cart_id, paymentMethod, creditcardNumber);
                    }
                    else
                        Toast.makeText(getActivity(), "You've entered wrong credit card number", Toast.LENGTH_LONG).show();
                }//credit card method is selected
                else{
                    Toast.makeText(getActivity(), "You haven't selected a payment method", Toast.LENGTH_LONG).show();
                }//no payment method is selected
                paymentMethodTxt.setText(paymentMethod);
                dialog.cancel();
            }
        });
        dialog.show();
    }
}