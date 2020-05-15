# PullRefreshView
Single PullRefreshView widget for android app 
## How to use
- 添加依赖
``` gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
   implementation 'com.github.PearceXu:PullRefreshView:1.0.8'
}
```
- 使用

``` xml
<?xml version="1.0" encoding="utf-8"?>
<com.library.pullrefresh.PullRefreshView
    android:id="@+id/pull_refresh_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        tools:context=".MainActivity">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello World!"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.library.pullrefresh.PullRefreshView>
```
``` java
public class MainActivity extends AppCompatActivity implements PullRefreshView.PullRefreshListener {
    PullRefreshView mPullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullView = findViewById(R.id.pull_refresh_view);
        mPullView.setListener(this);
    }



    @Override
    public void onPullRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullView.stopLoading();
            }
        },2000);
    }

}

```
 
