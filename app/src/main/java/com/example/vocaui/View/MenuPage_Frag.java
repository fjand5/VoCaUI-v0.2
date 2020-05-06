package com.example.vocaui.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vocaui.R;
import com.example.vocaui.View.Utils.RenderElement;

public class MenuPage_Frag extends Fragment  {
    private View rootView;
    private ViewGroup mContainer;
    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("fragmentonCreateView","onCreateView: " + name);
        mContainer = container;
        if(rootView==null)
            rootView = inflater.inflate(R.layout.frag_menupage,container,false);
        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden
        && mContainer!=null){


            RenderElement.getInstance().requestState(mContainer.getContext());
        }
        super.onHiddenChanged(hidden);
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
