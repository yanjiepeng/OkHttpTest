package mj.zk.com.okhttpdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private TextView tv_show;
    private String downloadUrl = "http://pic4.nipic.com/20090903/2125404_132352014851_2.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = (Button) findViewById(R.id.btn_start);
        tv_show = (TextView) findViewById(R.id.tv_show);


        //创建okhttp对象
        final OkHttpClient client = new OkHttpClient();
        //创建Requeset对象
        final Request request = new Request.Builder().url("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=17780570301").build();

        final Call call = client.newCall(request);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downloadFile(client);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_show.setText("加载失败了。。。请检查网址");
                                    }
                                });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_show.setText(res);
                                    }
                                });
                    }
                });
            }
        });

    }
    //获取文件名
    private String getFileName(String path)
    {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
    //下载文件
    public void downloadFile(OkHttpClient okHttpClient){
        Toast.makeText(MainActivity.this,"开始下载",0).show();
        //创建请求
        Request request = new Request.Builder().url(downloadUrl).tag(this).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is =null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                is = response.body().byteStream(); //获取输出流
                File file  = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/","test.png"); //保存图片到存储器
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1 ){
                    fos.write(buf , 0 , len);
                }
                fos.flush();
                //如果文件下载成功，第一个参数为文件的绝对路径

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                    }
                });
                //关闭流
                fos.close();
                is.close();
            }
        });
    }
}
