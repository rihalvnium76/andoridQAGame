package com.example.lab1.ui.level;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.lab1.GameActivity;
import com.example.lab1.Module.UICtrl;
import com.example.lab1.R;

public class LevelFragment extends Fragment {
    private LevelViewModel homeViewModel;

    private Button[] btnLevel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(LevelViewModel.class);
        View root = inflater.inflate(R.layout.fragment_level, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        btnLevel = new Button[] {
            (Button)(root.getRootView().findViewById(R.id.btnLevel0)),
            (Button)(root.getRootView().findViewById(R.id.btnLevel1)),
            (Button)(root.getRootView().findViewById(R.id.btnLevel2)),
            (Button)(root.getRootView().findViewById(R.id.btnLevel3)),
            (Button)(root.getRootView().findViewById(R.id.btnLevel4))
        };
        for(int i=0; i<btnLevel.length; ++i)
            btnLevel[i].setOnClickListener(new LevelButtonClickListener(i));

        return root;
    }

    private class LevelButtonClickListener implements View.OnClickListener {
        private int levelID;

        public LevelButtonClickListener(int levelID) {
            this.levelID = levelID;
        }

        @Override
        public void onClick(View v) {
            UICtrl.currentLevelID = levelID;
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        }
    }
}
