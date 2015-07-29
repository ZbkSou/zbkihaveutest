package com.bkzhou.pages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.bkzhou.utils.MeasureTextUtil;
import com.bkzhou.utils.Util;
import com.example.bkzhou.zbkihaveutest.R;

/**
 * Created by bkzhou on 15-7-16.
 */
public class Register  extends Activity implements View.OnClickListener{
    public static  final  String TAG = "Register";
    private EditText email,name,password,repeat_password,captcha,tuiguang;
    private LinearLayout captcha_layout;
    private NetworkImageView captcha_image;
    private Button reginer_button;
    //输入的 邮箱 姓名 密码等文本
    private String str_email,str_name,str_password,str_repeat_password,str_captcha;
    //点击注册按钮 符合条件 则注册新用户
    private boolean right_email = false,right_name = false,right_password = false,right_repeat_password = false,right_captcha = false,right_email_isnew = false;

    /**
     *验证码图片是否已经展示
     */
    private boolean isHasShowCaptcha = false;
    AccountModel accountModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register) ;
//        Intent intent  =  getIntent();
//        bundle  =  intent.getBundleExtra(RouteHelper.PAGE_PARAMS);
//        targetDeeplink = bundle.getString(P_NEXT_PAGE_DEEPLINK);
        init();

    }

    /**
     * 初始化控件
     */
    private  void init(){
        //获取注册界面所有控件
        email              	   = (EditText)findViewById(R.id.email);
        name        		   = (EditText)findViewById(R.id.name);
        password               = (EditText)findViewById(R.id.password);
        repeat_password 	   = (EditText)findViewById(R.id.repeat_password);
        tuiguang               = (EditText)findViewById(R.id.tuiguang);
//        验证码的显示
        captcha_layout         = (LinearLayout)findViewById(R.id.captcha_layout);
        captcha                = (EditText)findViewById(R.id.captcha);
        captcha_image          = (NetworkImageView)findViewById(R.id.captcha_image);

        reginer_button               = (Button)findViewById(R.id.reginer_button);

        reginer_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reginer_button :
//                防止连续点击
                if (Util.isFastDoubleClick()){
                        return ;
                }
                str_email = email.getText().toString().trim();
                str_name  = name.getText().toString().trim();
                str_password = password.getText().toString().trim();
                str_repeat_password = repeat_password.getText().toString().trim();
                str_captcha = captcha.getText().toString().trim();

                Log.i(TAG, "str_email init: " + str_email);
                if(isAllEdittextFormatRight()){
                    Log.i(TAG,"isAllEdittextFromatRight return true ");
                    if(isHasShowCaptcha){
                        Log.i(TAG, "str_email : " +str_email);
                        if(str_captcha.isEmpty() || str_captcha == null){
                            captcha.setError("验证码不能为空");
                        }else{//验证图片已经展示
                            checkCaptchaState();
                        }
                    }else {//验证码没有展示,
                        isNeedShowCaptcha();
                    }

                }else{
                    Log.i(TAG,"isAllEdittextFromatRight return false");
                }
        }
    }
    /**
     * 检测所有文本框的输入格式
     */
    private boolean isAllEdittextFormatRight(){
        //邮箱格式判断
        if(str_email.isEmpty() || str_email == null){
            email.setError("邮箱不能为空!");
            right_email = false;
        }else{
            if(!MeasureTextUtil.idEmail(str_email)){
                email.setError("您的邮箱地址不正确!");
                right_email = false;
            }else{
                right_email = true;
            }
        }
        //判断用户名可能出现的错误
        if(str_name.isEmpty() || str_name == null){
            name.setError("姓名不能为空");
            right_name = false;
        }else{
            right_name = true;
        }
        //判断密码可能出现的错误
        if(str_password.isEmpty() || str_password == null){
            password.setError("密码不能为空");
            right_password = false;
        }else{
            if(str_password.length() < 6){
                password.setError("密码长度不能小于6位");
                right_password = false;
            }else{
                right_password = true;
            }
        }
        //判断重复密码可能出现的错误
        if(str_repeat_password.isEmpty() || str_repeat_password == null){
            repeat_password.setError("重复密码不能为空");
            right_repeat_password = false;
        }else{
            if(!str_password.equals(str_repeat_password)){
                repeat_password.setError("两次输入的密码不一致");
                right_repeat_password = false;
            }else{
                right_repeat_password = true;
            }
        }
        if(right_email && right_name && right_password && right_repeat_password){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 检测是否需要显示验证码 如果需要显示图片
     */
    private void isNeedShowCaptcha(){
        accountModel.isNeedCaptcha(null, new JsonResponse() {

            @Override
            public void onSuccess(JSONObject response, JSONArray responseArray) {
                try {
                    //需要注册码的时候  且验证码图片没有展示
                    if(response.getBoolean("need_captcha") && !isHasShowCaptcha){
                        captcha_layout.setVisibility(View.VISIBLE);
                        accountModel.volleyGetCaptchaUrl(new JsonResponse() {

                            @Override
                            public void onSuccess(JSONObject response, JSONArray responseArray) {
                                isHasShowCaptcha = true;
                                try {
                                    captcha_image.setImageUrl(response.getString("url"), BaseApplication.getVolley().getImageLoader());
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {

                            }
                        });
                    }else{
                        //如果不需要验证码 则检测邮箱
                        ValidateEmail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
