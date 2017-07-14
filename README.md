# DragChooseDemo
## 效果图
![image](https://github.com/xuezj/DragChooseDemo/blob/master/demo.gif)

## Attributes属性（布局文件中的自定义属性）

|     变量名    |  类型  |  说明   |
| :-------------: |:-------------:| :-----:|
| background_color | color | 背景中圆形及矩形连接线的颜色 |
| border_color      | color  |   背景中圆形及矩形连接线描边（不设置默认为白色） |
| enabled | reference |    选中时的图标 |
| focused | reference |    长按拖动时的图标 |
| text_default_color | color |  文字颜色 |
| text_select_color | color |  文字选中后的颜色 |
| text_size | dimension |  文字大小（如12sp） |
| radius | integer |  背景圆形的半径 |
| counts | integer |  按钮个数（最少2个，最多8个，超出或少于，自动选择边界值） |

半径、文字大小、按钮个数注意配合使用，以达到最佳效果 

## 方法
|     方法名    |  说明   |
| :-------------:| :-----:|
| setTextData | 设置文字（文字个数大于按钮最大个数时自动移除后面文字） |
| addOnChooseItemListener |   添加选中后回调监听（初始化后默认选中） |
| setdefaultSelectedItem |    设置默认选中项（下标从0开始，大于最大个数后默认选中0） |

## 使用
布局文件中的使用
```xml
<com.xuezj.dragchooselibrary.view.DragChooseView
        android:id="@+id/my_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        choose:background_color="#efeff4"
        choose:border_color="#ffcfcfd3"
        choose:enabled="@mipmap/sliderwifion"
        choose:focused="@mipmap/sliderwifioff"
        choose:text_size="12sp"
        choose:counts="5"
        choose:radius="20" />
 
```
代码中调用
```Java
dragChooseView =(DragChooseView)findViewById(R.id.my_view);

dragChooseView.setTextData("自定义","单选","双选","全选","sss","ddd");
dragChooseView.addOnChooseItemListener(new DragChooseView.OnChooseItemListener() {
       @Override
       public void chooseItem(int index, String text) {
             Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
       }
});
```
