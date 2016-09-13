package com.example.khacc.googlemapdemo15082016;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Bắt đầu sử dụng", "Bấm phím Start để bắt đầu lưu lộ trình", R.drawable.ic_start, Color.rgb(52, 152, 219)));
        addSlide(AppIntroFragment.newInstance("Kết thúc", "Bấm phím lưu để lưu lại lộ trình", R.drawable.ic_save, Color.rgb(46, 204, 113)));
        addSlide(AppIntroFragment.newInstance("Bắt đầu lại", "Phím reset để bắt đầu lại lộ trình", R.drawable.ic_reset, Color.rgb(26, 188, 156)));
        addSlide(AppIntroFragment.newInstance("Xem lộ trình đã lưu", "Chọn lộ trình trong mục đã lưu để xem lại lộ trình đã đi", R.drawable.ic_route, Color.rgb(231, 76, 60)));

        showSkipButton(true);
        setProgressButtonEnabled(true);
    }

    private void loadMainActivity(){
        loadMainActivity();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

}
