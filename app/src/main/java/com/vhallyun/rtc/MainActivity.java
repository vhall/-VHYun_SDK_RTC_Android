package com.vhallyun.rtc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vhall.framework.VhallSDK;



import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Created by Hank on 2017/12/8.
 */
public class MainActivity extends Activity {

    TextView tv_appid;
    Button mBtnConfig;
    private static final String TAG = "VHLivePusher";
    private static final int REQUEST_INTERACTIVE = 2;
    private static final String KEY_INAV_ID = "inavId";
    private static final String KEY_LSS_ID = "lssId";
    private static final String KEY_TOKEN = "token";
    SharedPreferences sp;
    EditText edtRtcId, edtLssId, edtToken;
    private String token;
    private String rtcId;
    private String lssId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_appid = this.findViewById(R.id.tv_appid);
        tv_appid.setText(VhallSDK.getInstance().getAPP_ID());
        edtLssId = findViewById(R.id.edt_lss_id);
        edtRtcId = findViewById(R.id.edt_inav_id);
        edtToken = findViewById(R.id.edt_token);
        token = sp.getString(KEY_TOKEN, "");
        rtcId = sp.getString(KEY_INAV_ID, "");
        lssId = sp.getString(KEY_LSS_ID, "");
        edtToken.setText(token);
        edtRtcId.setText(rtcId);
        edtLssId.setText(lssId);
    }


    public void showInteractive(View view) {
        rtcId = edtRtcId.getText().toString().trim();
        token = edtToken.getText().toString().trim();
        lssId = edtLssId.getText().toString().trim();
        if (TextUtils.isEmpty(rtcId) || TextUtils.isEmpty(token)) {
            return;
        }
        sp.edit().putString(KEY_INAV_ID, rtcId).putString(KEY_TOKEN, token).commit();

        if (getPushPermission(REQUEST_INTERACTIVE)) {
            Intent intent = new Intent(this, InteractiveActivity.class);
            intent.putExtra("channelid", rtcId);
            intent.putExtra("token", token);
            if (!TextUtils.isEmpty(lssId)) {
                intent.putExtra("broadCastId", lssId);
            }
            startActivity(intent);
        }
    }


    private boolean getPushPermission(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        Log.e(TAG, "CAMERA:" + checkSelfPermission(CAMERA) + " MIC:" + checkSelfPermission(RECORD_AUDIO));
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{CAMERA, RECORD_AUDIO}, requestCode);
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INTERACTIVE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "get REQUEST_PUSH permission success");
                Intent intent = new Intent(this, InteractiveActivity.class);
                intent.putExtra("channelid", rtcId);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        }
    }
}
