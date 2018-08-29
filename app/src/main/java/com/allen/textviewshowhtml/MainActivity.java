package com.allen.textviewshowhtml;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.allen.textviewshowhtml.util.DataUtil;
import com.allen.textviewshowhtml.util.LinkMovementMethodExt;
import com.allen.textviewshowhtml.util.MImageGetter;
import com.allen.textviewshowhtml.util.MTagHandler;
import com.allen.textviewshowhtml.util.MessageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
        String artAddress = "demo/demo.html";
        String artImg = "demo/imgs/";
        ArtUpdater au = new ArtUpdater(artAddress, artImg);
        au.execute();
    }

    class ArtUpdater extends AsyncTask<Void, Void, Void> {

        public ArtUpdater(String artAddress, String artImg) {
            this.artAdresss = artAddress;
            this.artImg = artImg;
        }

        String artAdresss;
        String artImg;
        String htmlContent;

        @SuppressLint("HandlerLeak")
        @Override
        protected void onPostExecute(Void result) {
            if (htmlContent != null) {

                htmlContent = htmlContent.replace("<img src=\"imgs/", "<br><img src=\"" + artImg);

                htmlContent = htmlContent.replaceAll("<head>([\\s\\S]*)<\\/head>", "");
                if (htmlContent.contains("><p>")) {
                    String regularExpression1 = "(<[^\\/]\\w><p>)";
                    Pattern pat1 = Pattern.compile(regularExpression1);
                    Matcher mat1 = pat1.matcher(htmlContent);
                    if (mat1.find()) {
                        for (int i = 0; i < mat1.groupCount(); i++) {
                            System.out.println(mat1.group(i));
                            String temp = mat1.group(i).replace("<p>", "");
                            htmlContent = htmlContent.replace(mat1.group(i), temp);
                            String tail = temp.replace("<", "</");
                            htmlContent = htmlContent.replace("</p>" + tail, tail);
                            System.out.println(htmlContent);
                        }
                    }

                }

            }
            try {
                mTextView.setText(Html.fromHtml(htmlContent, new MImageGetter(mTextView, MainActivity.this), new MTagHandler()));

                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        int what = msg.what;
                        if (what == 200) {
                            MessageSpan ms = (MessageSpan) msg.obj;
                            Object[] spans = (Object[]) ms.getObj();

                            for (Object span : spans) {
                                if (span instanceof ImageSpan) {
                                    Intent intent = new Intent(MainActivity.this, ShowPicActivity.class);
                                    Bundle bundle = new Bundle();

                                    bundle.putString("picUrl", ((ImageSpan) span).getSource());
                                    intent.putExtras(bundle);

                                    startActivity(intent);

                                }
                            }
                        }
                    }
                };
                mTextView.setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));


            } catch (Throwable e) {
                //progressBar.setVisibility(View.GONE);
                if (e != null) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String htmlString = null;

                htmlString = DataUtil.getFromAssets(MainActivity.this, artAdresss);

                htmlContent = htmlString;

            } catch (Exception e) {
                return null;
            }

            return null;
        }

    }
}
