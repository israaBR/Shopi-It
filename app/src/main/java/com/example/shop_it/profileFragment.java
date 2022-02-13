package com.example.shop_it;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.content.Intent;;
import android.view.View;
import android.widget.*;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
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


    int customer_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button personalInfoBtn = (Button) view.findViewById(R.id.personalInfoBtnProfile);
        Button changePassBtn = (Button) view.findViewById(R.id.changePassBtnProfile);
        Button securityQuestionsBtn = (Button) view.findViewById(R.id.securityQuestionsBtnProfile);
        Button logoutBtn = (Button) view.findViewById(R.id.logoutBtnProfile);

        Bundle data = getArguments();
        if(data != null)
           customer_id = data.getInt("customer_id");


        personalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), personalInfoActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), changePasswordActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });

        securityQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), resetPasswordQuestionsActivity.class);
                intent.putExtra("customer_id", customer_id);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preference = getActivity().getSharedPreferences("myPreferences", getContext().MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = preference.edit();
                prefEditor.remove("customer_email");
                prefEditor.remove("customer_password");
                prefEditor.remove("customer_id");

                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }
}