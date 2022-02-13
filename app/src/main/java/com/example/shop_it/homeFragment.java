package com.example.shop_it;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.sql.Struct;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


class Category{
    int id;
    String name;
    int image;
    Category(int cat_id, String cat_name, int cat_image){
        id = cat_id;
        name = cat_name;
        image = cat_image;
    }
}


class categoriesListAdapter extends ArrayAdapter<Category> {
    private Context mcontext;
    private int mresource;
    Category[] category;
    public categoriesListAdapter(@NonNull Context context, int resource, Category[] objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
        category=objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);

        ImageView image = (ImageView) convertView.findViewById(R.id.categoryrowimage);
        TextView name = (TextView) convertView.findViewById(R.id.categoryrowname);
        image.setImageResource(category[position].image);
        name.setText(getItem(position).name);

        return convertView;
    }
}


public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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

    ImageView womenWear, menWear, kidsWear;
    int category_id, customer_id;
    databaseHelper dbh;
    Cursor cursor;
    ListView categoriesList;
    categoriesListAdapter categoriesAdapter;
    Category[] category;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle data = getArguments();
        if(data != null) {
            customer_id = data.getInt("customer_id");
        }
        dbh = new databaseHelper(getActivity());
        cursor = dbh.get_categories();
        category = new Category[cursor.getCount()];
        for(int i=0; i<cursor.getCount(); i++){
            category[i] = new Category(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
            cursor.moveToNext();
        }
        categoriesList = view.findViewById(R.id.categoriesList);
        categoriesAdapter = new categoriesListAdapter(getActivity(), R.layout.categories_list_row, category);
        categoriesList.setAdapter(categoriesAdapter);

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), productCategoryActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("category_id", category[position].id);
                startActivity(intent);
            }
        });
        return view;
    }
}