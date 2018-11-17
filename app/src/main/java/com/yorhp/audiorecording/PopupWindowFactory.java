package com.yorhp.audiorecording;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopupWindowFactory {
    PopupWindow popupWindow;
    public PopupWindowFactory(Context context, View contentView){
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);  
        // 设置popupWindow外部是否可以触摸  
        popupWindow.setOutsideTouchable(true);  
        popupWindow.setContentView(contentView);
        this.popupWindow=popupWindow;
    }

    public void showAtLocation(View view, int location, int x, int y){
        popupWindow.showAtLocation(view,location,x,y);
    }

    public void dismiss(){
        popupWindow.dismiss();
    }

}  