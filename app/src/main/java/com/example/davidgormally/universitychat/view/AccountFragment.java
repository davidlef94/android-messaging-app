package com.example.davidgormally.universitychat.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.AppUserController;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private static final String ARG = "userId";
    private AppUser appUser;

    public static AccountFragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG, userId);

        AccountFragment accountFragment = new AccountFragment();
        accountFragment.setArguments(bundle);

        return accountFragment;
    }

    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = (String)getArguments().getSerializable(ARG);
        AppUserController appUserController = new AppUserController(getContext());
        appUser = appUserController.getAppUser(id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView firstNameTv = (TextView)view.findViewById(R.id.first_name_tv);
        firstNameTv.setText(appUser.getAppUserFirstName());

        TextView lastNameTv = (TextView)view.findViewById(R.id.last_name_tv);
        lastNameTv.setText(appUser.getAppUserLastName());

        TextView emailTv = (TextView)view.findViewById(R.id.email_tv);
        emailTv.setText(appUser.getAppUserEmail());

        FloatingActionButton signOutFab = (FloatingActionButton)view.findViewById(R.id.sign_out_fab);
        signOutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    private void signOut() {
        appUser.setUserSignedIn(false);
        AppUserController appUserController = new AppUserController(getContext());
        appUserController.updateAppUser(appUser);

        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);

        getActivity().finish();

    }

}
