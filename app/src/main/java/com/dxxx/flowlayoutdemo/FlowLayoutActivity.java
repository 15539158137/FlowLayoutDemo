package com.dxxx.flowlayoutdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dxxx.flowlayoutdemo.myflowlayout.DataBean;
import com.dxxx.flowlayoutdemo.myflowlayout.Flowlayout;
import com.dxxx.flowlayoutdemo.myflowlayout.OnFlowlayoutItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FlowLayoutActivity extends Activity {
     List<DataBean> all;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flowlayoutactivity);
        //实例化
        final Flowlayout flowlayout = findViewById(R.id.flowlayout);
        all = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MyBean dataBean = new MyBean();
            dataBean.setFlowItemName("数据" + i * i * 10000);
            all.add(dataBean);
        }
        //设置数据源
        flowlayout.setDatas(all);
        flowlayout.setOnFlowlayoutItemClickListener(new OnFlowlayoutItemClickListener() {
            @Override
            public void onItemClick(View view, DataBean dataBean) {
                //子view点击的回调
//点击了子view，返回的是子view改变后的状态
                Log.e("点击信息", dataBean.getFlowItemName() + "点击变为" + dataBean.isFlowItemIsChoosed());
                for (DataBean dataBean1 : all) {
                    Log.e("是否被选中", dataBean1.isFlowItemIsChoosed() + dataBean1.getFlowItemName());
                }
            }
        });


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.clear();
                for (int i = 0; i < 10; i++) {
                    MyBean dataBean = new MyBean();
                    dataBean.setFlowItemName("数据3333" + i * i * 10000);
                    all.add(dataBean);
                }
                //更新数据的方法
                flowlayout.notifyDataChanged();
            }
        });
        flowlayout.setChooseMulite(isChooseMulite);
        final Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChooseMulite=!isChooseMulite;
                if (!isChooseMulite) {
                    button1.setText("单选");
                } else {
                    button1.setText("多选");
                }
                //设置单选和多选的方法
                flowlayout.setChooseMulite(isChooseMulite);
            }
        });
    }

    boolean isChooseMulite = true;
}

class MyBean extends DataBean {

}