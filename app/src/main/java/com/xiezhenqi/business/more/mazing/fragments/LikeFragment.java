package com.xiezhenqi.business.more.mazing.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiezhenqi.R;
import com.xiezhenqi.XZQApplication;
import com.xiezhenqi.business.more.mazing.adapters.RVFragmentAdapter;
import com.xiezhenqi.utils.LogUtils;
import com.xiezhenqi.utils.RecyclerViewUtils;
import com.xiezhenqi.widget.smarttablayout.SmartTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TastyFragment
 * Created by sean on 2016/12/2.
 */

public class LikeFragment extends MainFragment implements
        SmartTabLayout.TabProvider,
        SmartTabLayout.OnTabClickListener {

    @Bind(R.id.vp_like)
    ViewPager vpLike;
    private SmartTabLayout stl;
    private RVFragmentAdapter adapter;
    private SparseArray<String> sparseArray = new SparseArray<>();

    @Override
    protected int getLayoutId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return R.layout.fragment_like;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this, getView());
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View tabView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_custom_tab_view, container, false);
        TextView tabTitleView = (TextView) tabView.findViewById(R.id.tv_tab);
        TextView tabCount = (TextView) tabView.findViewById(R.id.tv_count);
        String pageTitle = adapter.getPageTitle(position).toString();
        tabTitleView.setText(pageTitle);
        tabCount.setText("5");
        sparseArray.put(position, pageTitle);
        return tabView;
    }

    @Override
    public void bindTitleView(View title) {
        if (title instanceof SmartTabLayout) {
            stl = (SmartTabLayout) title;
            stl.setOnTabClickListener(this);
            stl.setCustomTabView(this);
            stl.setViewPager(vpLike);
        } else {
            LogUtils.w("WISH", "根布局不是SmartTabLayout");
        }
    }


    public static CharSequence getPageTitle(Context context) {
        return "喜欢";
    }

    public static Drawable getNormalDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_main_manager_normal);
    }

    public static Drawable getSelectedDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.ic_main_manager_selected);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onTabClicked(View v, int position) {
        String tabName = sparseArray.get(position);
        if (v.isSelected()) {
            View child = vpLike.getChildAt(vpLike.getCurrentItem());
            if (child instanceof RecyclerView)
                RecyclerViewUtils.scrollToTopWithAnimation((RecyclerView) child);
            RVFragments fragment = adapter.getFragmentsByTabName(tabName);
            XZQApplication.sendLocalBroadcast("update");
            fragment.refreshData();
        }
    }

    @Override
    public void onTabSelected(View v, int position) {
        TextView textView = (TextView) v.findViewById(R.id.tv_tab);
        String tabName = textView.getText().toString();
        XZQApplication.sendLocalBroadcast(new Intent().putExtra("title", tabName).setAction("updateTab"));
    }

    @Override
    protected void loadData() {
        adapter = new RVFragmentAdapter(getFragmentManager(), "喜欢");
        vpLike.setAdapter(adapter);
        if (stl != null)
            stl.setViewPager(vpLike);
        XZQApplication.sendLocalBroadcast(new Intent().putExtra("title", sparseArray.get(0)).setAction("updateTab"));
    }
}
