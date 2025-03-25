package com.example.sqlite0.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.sqlite0.R;
import com.example.sqlite0.activities.LoginActivity;
import com.example.sqlite0.data.UserRepository;
import com.example.sqlite0.models.User;
import com.example.sqlite0.utils.PreferenceUtils;

public class FragmentProfile extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnLogout;
    private UserRepository userRepository;
    private PreferenceUtils preferenceUtils;

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        userRepository = new UserRepository(requireContext());
        preferenceUtils = new PreferenceUtils(requireContext());

        int userId = preferenceUtils.getUserId();
        if (userId == -1) {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return view;
        }

        User user = userRepository.getUserById(userId);
        if (user != null) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
        } else {
            tvUsername.setText("Không tìm thấy thông tin");
            tvEmail.setText("Không tìm thấy thông tin");
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        btnLogout.setOnClickListener(v -> {
            preferenceUtils.clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}