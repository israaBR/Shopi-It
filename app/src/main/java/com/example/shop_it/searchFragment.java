package com.example.shop_it;

import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link searchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

class searchProductsAdapter extends ArrayAdapter<Product> {
    private Context mcontext;
    private int mresource;
    Product[] product;
    public searchProductsAdapter(@NonNull Context context, int resource, Product[] objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        product=objects;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);
        ImageView image= (ImageView) convertView.findViewById(R.id.productImageSV);
        TextView name = (TextView) convertView.findViewById(R.id.productNameSV);
        image.setImageResource(product[position].image);
        name.setText(product[position].name);
        return convertView;
    }
}

public class searchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public searchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment searchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static searchFragment newInstance(String param1, String param2) {
        searchFragment fragment = new searchFragment();
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

    SearchView searchBar;
    ImageButton voiceSearchBtn, imageSearchBtn;
    ListView productsList;
    searchProductsAdapter adapter;
    databaseHelper dbh;
    int customer_id;
    Product[] product;
    Cursor productsCursor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = (SearchView) view.findViewById(R.id.searchBar);
        searchBar.setQueryHint("Search for products here...");
        imageSearchBtn = (ImageButton) view.findViewById(R.id.imageSearchBtn);
        voiceSearchBtn = (ImageButton) view.findViewById(R.id.voiceSearchBtn);
        productsList = (ListView) view.findViewById(R.id.searchProductsList);
        dbh = new databaseHelper(getActivity());
        Bundle data = getArguments();
        if (data != null) {
            customer_id = data.getInt("customer_id");
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals(""))
                    loadSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals(""))
                    loadSearchResults(newText);
                return true;
            }
        });
        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), oneProductActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("product_id", product[position].id);
                startActivity(intent);
            }
        });
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String voiceQuery = arrayList.get(0);
                    searchBar.setQuery(voiceQuery, true);
                }

/*
                if(result.getData()!= null){
                IntentResult intentResult = IntentIntegrator.parseActivityResult(0, result.getResultCode(), result.getData());
                if (intentResult != null) {
                    if (intentResult.getContents() != null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // set title
                        alertDialogBuilder.setTitle("Scanning Result");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(intentResult.getContents())
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        // if this button is clicked, close the dialog box
                                        dialog.cancel();

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show the message
                        alertDialog.show();
                        //searchBar.setQuery(intentResult.getContents(), true);
                        Toast.makeText(getActivity(), intentResult.getContents(), Toast.LENGTH_SHORT).show();
                    }
                }
                }
*/

            }
        });
        voiceSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
                activityLauncher.launch(intent);
            }
        });
        imageSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Intent intent = new Intent(getActivity(), scanActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);

 */
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setCaptureActivity(captureActivity.class);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Scanning code..");
                intentIntegrator.initiateScan();

            }
        });
        return view;
    }

    void loadSearchResults(String txt) {
        productsCursor = dbh.search_by_product(txt);
        //0-id 1-name 2-price 3-quantity 4-image 5-category_id
        product = new Product[productsCursor.getCount()];
        for (int i = 0; i < productsCursor.getCount(); i++) {
            product[i] = new Product();
            product[i].id = productsCursor.getInt(0);
            product[i].name = productsCursor.getString(1);
            product[i].image = productsCursor.getInt(4);
            productsCursor.moveToNext();
        }
        adapter = new searchProductsAdapter(getActivity(), R.layout.search_products_list_row, product);
        productsList.setAdapter(adapter);
    }
}
/*requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                searchBar.setQuery(intentResult.getContents(), true);
                loadSearchResults(intentResult.getContents());
                Toast.makeText
                        (getActivity(), intentResult.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity(), "No results", Toast.LENGTH_SHORT).show();
    }
});
*/