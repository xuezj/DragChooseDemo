package com.xuezj.dragchoosedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xuezj.dragchooselibrary.view.DragChooseView;

public class MainActivity extends AppCompatActivity {
    private DragChooseView dragChooseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragChooseView =(DragChooseView)findViewById(R.id.my_view);
        dragChooseView.setTextData("自定义","单选","双选","全选","sss","ddd");
        dragChooseView.addOnChooseItemListener(new DragChooseView.OnChooseItemListener() {
            @Override
            public void chooseItem(int index, String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
