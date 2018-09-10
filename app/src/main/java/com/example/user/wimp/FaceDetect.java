package com.example.user.wimp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;


public class FaceDetect extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    TextView textView;
    ImageButton button;

    private static final String TAG = "opencv";
    public static String currentF = "";
    public static String onCameraS = "";

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat matResult;
    private static boolean south = false;
    private static boolean north = false;
    private static boolean east = false;
    private static boolean west = false;
    private static int degree;

//    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native long loadCascade(String cascadeFileName );
    public native void detect(long cascadeClassifier_face,

                              long cascadeClassifier_eye, long matAddrInput, long matAddrResult);
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    OrientationEventListener orientEventListener;

    private final Semaphore writeLock = new Semaphore(1);

    public void getWriteLock() throws InterruptedException {
        writeLock.acquire();
    }

    public void releaseWriteLock() {
        writeLock.release();
    }

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    private void read_cascade_file(){
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.facedetect);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor(value));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else  read_cascade_file(); //추가
        }
        else  read_cascade_file(); //추가

//        textView = findViewById(R.id.textView);

        orientEventListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int arg0) {
//                textView.setText(arg0+"");
                Log.d(TAG, "onOrientationChanged: 남" + FaceDetect.south + " 동" + FaceDetect.east + " 북" + FaceDetect.north + " 서" + FaceDetect.west + " ");
                if(arg0 > 355 || arg0 < 10)
                {
//                    if(!FaceDetect.west.south) {
                    degree = arg0;
                    if(!FaceDetect.south)
                    {
                        FaceDetect.south = true;
                    }
                    FaceDetect.currentF = "FaceDetect.west.south";
                    Log.d(TAG, "onOrientationChanged: 남!!!!!!!!!!!!!!!");
//                    }
                }
                else if (arg0 > 80 && arg0 < 100)
                {
//                    if(!FaceDetect.west.east) {
                    degree = arg0;
                    if(!FaceDetect.east) {
                        FaceDetect.east = true;
                    }
                    FaceDetect.currentF = "FaceDetect.west.east";
                    Log.d(TAG, "onOrientationChanged: 동!!!!!!!!!!!!!!!");
//                    }
                }
                else if (arg0 > 170 && arg0 < 190)
                {
//                    if(!FaceDetect.west.north) {
                        degree = arg0;
                    if(!FaceDetect.north) {
                        FaceDetect.north = true;
                    }
                    FaceDetect.currentF = "FaceDetect.west.north";
                        Log.d(TAG, "onOrientationChanged: 북!!!!!!!!!!!!!!!");
//                    }
                }
                else if(arg0 > 260 && arg0 < 280)
                {
//                    if(!FaceDetect.west.west) {
                    degree = arg0;
                    if(!FaceDetect.west) {
                        FaceDetect.west = true;
                    }
                    FaceDetect.currentF = "FaceDetect.west.west";
                    Log.d(TAG, "onOrientationChanged: 서!!!!!!!!!!!!!!!");
//                    }
                }
//                if(arg0 > 355 || arg0 < 10) {
////                    if(!FaceDetect.west.south) {
//                        FaceDetect.west.south = true;
//                        FaceDetect.west.east = false;
//                        FaceDetect.west.north = false;
//                        FaceDetect.west.west = false;
//
//                        FaceDetect.west.currentF = "FaceDetect.west.south";
//                        Log.d(TAG, "onOrientationChanged: 남!!!!!!!!!!!!!!!");
////                    }
//                }else if(arg0 > 80 && arg0 < 100){
////                    if(!FaceDetect.west.east) {
//                        FaceDetect.west.south = false;
//                        FaceDetect.west.east = true;
//                        FaceDetect.west.north = false;
//                        FaceDetect.west.west = false;
//                        FaceDetect.west.currentF = "FaceDetect.west.east";
//                        Log.d(TAG, "onOrientationChanged: 동!!!!!!!!!!!!!!!");
////                    }
//                }else if(arg0 > 170 && arg0 < 190){
////                    if(!FaceDetect.west.north) {
//                        FaceDetect.west.south = false;
//                        FaceDetect.west.east = false;
//                        FaceDetect.west.north = true;
//                        FaceDetect.west.west = false;
//                        FaceDetect.west.currentF = "FaceDetect.west.north";
//                        Log.d(TAG, "onOrientationChanged: 북!!!!!!!!!!!!!!!");
////                    }
//                }else if(arg0 > 260 && arg0 < 280){
////                    if(!FaceDetect.west.west) {
//                        FaceDetect.west.south = false;
//                        FaceDetect.west.east = false;
//                        FaceDetect.west.north = false;
//                        FaceDetect.west.west = true;
//                        FaceDetect.west.currentF = "FaceDetect.west.west";
//                        Log.d(TAG, "onOrientationChanged: 서!!!!!!!!!!!!!!!");
////                    }
//                }
            }
        };

        if (orientEventListener.canDetectOrientation()) {
            Toast.makeText(this, "Can DetectOrientation",
                    Toast.LENGTH_LONG).show();
            orientEventListener.enable();
        } else {
            Toast.makeText(this, "Can't DetectOrientation",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    getWriteLock();

                    File path = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM) + "/Screenshots/");
//                    path.mkdirs();
                    File file = new File(path, "image.png");

                    String filename = file.toString();

                    Imgproc.cvtColor(matInput, matInput, Imgproc.COLOR_BGR2RGB, 4);
                    boolean ret  = Imgcodecs.imwrite( filename, matInput);
//                    boolean ret  = Imgcodecs.imwrite( filename, matResult);
                    if ( ret ) Log.d("캡쳐버튼누를때야????", "SUCCESS");
                    else Log.d("fail", "FAIL");


                    Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(file));
                    sendBroadcast(mediaScanIntent);

                    Log.d("이미지 주소인거 맞냐?", Uri.fromFile(file).toString());

                    Intent i = new Intent(FaceDetect.this, Mypage.class);
                    i.putExtra("profile", Uri.fromFile(file).toString());
                    startActivity(i);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                releaseWriteLock();

            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
//        matInput =  new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
//        matInput.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        try {
            getWriteLock();

            matInput = inputFrame.rgba();

            //가상화면을 인식되는 화면방향으로 회전
            //portrait일때 인식위한 가상화면 회전
//            Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//            if (!FaceDetect.west.onCameraS.equals(FaceDetect.west.currentF)){
////            {
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                FaceDetect.west.onCameraS = FaceDetect.west.currentF;
//                if (degree > 355 || degree < 10) {
//                    Log.d("이 각도는", "남 => matInput");
//                    Log.d("각도 in", "남쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
//                    //                Core.flip(matInput, matInput, 1);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    //                Core.flip(matInput, matInput, -1);
////                                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                                        Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.south = false;
//                } else if (degree > 80 && degree < 100) {
//                    Log.d("이 각도는", "동 => matInput");
//                    Log.d("각도 in", "동쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
//                    //                Core.flip(matInput, matInput, 1);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    //                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.east = false;
//                } else if (degree > 170 && degree < 190) {
//                    Log.d("이 각도는", "북 => matInput");
//                    Log.d("각도 in", "북쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
//
//                    //                Core.flip(matInput, matInput, 1);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.north = false;
//                } else if (degree > 260 && degree < 280) {
//                    Log.d("이 각도는", "서 => matInput");
//                    Log.d("각도 in", "서쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
//                    //                Core.flip(matInput, matInput, 1);
//                    //                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    //                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    //                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.west = false;
//                }
//            }
//            Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
            //틱상태
            if(degree>355 || degree<10) {
                Log.d("각도 in", "남쪽 "+"동"+FaceDetect.east+"서"+FaceDetect.west+"남"+FaceDetect.south+"북"+FaceDetect.north);
//                Core.flip(matInput, matInput, 1);
                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.flip(matInput, matInput, -1);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_180);
                FaceDetect.south = false;
            }else if(degree>80 && degree<100){
                Log.d("각도 in", "동쪽 "+"동"+FaceDetect.east+"서"+FaceDetect.west+"남"+FaceDetect.south+"북"+FaceDetect.north);
//                Core.flip(matInput, matInput, 1);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
                Core.rotate(matInput, matInput, Core.ROTATE_180);
                FaceDetect.east = false;
            }else if(degree>170 && degree<190){
                Log.d("각도 in", "북쪽 "+"동"+FaceDetect.east+"서"+FaceDetect.west+"남"+FaceDetect.south+"북"+FaceDetect.north);

//                Core.flip(matInput, matInput, 1);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
                FaceDetect.north = false;
            }else if(degree>260 && degree<280){
                Log.d("각도 in", "서쪽 "+"동"+FaceDetect.east+"서"+FaceDetect.west+"남"+FaceDetect.south+"북"+FaceDetect.north);
//                Core.flip(matInput, matInput, 1);
//                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
                FaceDetect.west = false;
            }


//            if(degree>355 || degree<10) {
//                Log.d("각도 in", "남쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.flip(matInput, matInput, -1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_180);
//                FaceDetect.west.south = false;
//            }else if(degree>80 && degree<100){
//                Log.d("각도 in", "동쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matInput, matInput, Core.ROTATE_180);
//                FaceDetect.west.east = false;
//            }else if(degree>170 && degree<190){
//                Log.d("각도 in", "북쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
//
////                Core.flip(matInput, matInput, 1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                FaceDetect.west.north = false;
//            }else if(degree>260 && degree<280){
//                Log.d("각도 in", "서쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                FaceDetect.west.west = false;
//            }
//            if(!FaceDetect.west.onCameraS.equals(FaceDetect.west.currentF))
//            {
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                FaceDetect.west.onCameraS = FaceDetect.west.currentF;
//
//                if(FaceDetect.west.south) {
//                    Log.d("각도 in", "남쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.flip(matInput, matInput, -1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.south = false;
//                }else if(FaceDetect.west.east){
//                    Log.d("각도 in", "동쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.east = false;
//                }else if(FaceDetect.west.north){
//                    Log.d("각도 in", "북쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
//
////                Core.flip(matInput, matInput, 1);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.north = false;
//                }else if(FaceDetect.west.west){
//                    Log.d("각도 in", "서쪽 "+"동"+FaceDetect.west.east+"서"+FaceDetect.west.west+"남"+FaceDetect.west.south+"북"+FaceDetect.west.north);
////                Core.flip(matInput, matInput, 1);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
////                    Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matInput, matInput, Core.ROTATE_180);
//                    FaceDetect.west.west = false;
//                }
//
//            }

            if ( matResult == null )
                matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
            Log.d("aa","aaa");
            Core.flip(matInput, matInput, 1);

            detect(cascadeClassifier_face,cascadeClassifier_eye, matInput.getNativeObjAddr(),
                    matResult.getNativeObjAddr());

            if (degree > 355 || degree < 10) {
                Log.d("이 각도는", "남 => matResult");
                Log.d("각도 in", "남쪽 " + "동" + FaceDetect.east + "서" + FaceDetect.west + "남" + FaceDetect.south + "북" + FaceDetect.north);
//                Core.flip(matResult, matResult, 1);
//                Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.flip(matResult, matResult, -1);
//                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
//                Core.rotate(matResult, matResult, Core.ROTATE_180);
                Log.d("매트릭스 값", matResult.width() + " 가로 " + matResult.height() + " 세로");
                FaceDetect.south = false;
            } else if (degree > 80 && degree < 100) {
                Log.d("이 각도는", "동 => matResult");
                Log.d("각도 in", "동쪽 " + "동" + FaceDetect.east + "서" + FaceDetect.west + "남" + FaceDetect.south + "북" + FaceDetect.north);
//                Core.flip(matResult, matResult, 1);
                Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
//            Core.rotate(matResult, matResult, Core.ROTATE_180);
                Log.d("매트릭스 값", matResult.width() + " 가로 " + matResult.height() + " 세로");
                FaceDetect.east = false;
            } else if (degree > 170 && degree < 190) {
                Log.d("이 각도는", "북 => matResult");
                Log.d("각도 in", "북쪽 " + "동" + FaceDetect.east + "서" + FaceDetect.west + "남" + FaceDetect.south + "북" + FaceDetect.north);

//                Core.flip(matResult, matResult, 1);
//                Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
                Core.rotate(matResult, matResult, Core.ROTATE_180);
                Log.d("매트릭스 값", matResult.width() + " 가로 " + matResult.height() + " 세로");
                FaceDetect.north = false;
            } else if (degree > 260 && degree < 280) {
                Log.d("이 각도는", "서 => matResult");
                Log.d("각도 in", "서쪽 " + "동" + FaceDetect.east + "서" + FaceDetect.west + "남" + FaceDetect.south + "북" + FaceDetect.north);
//                Core.flip(matResult, matResult, 1);
                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
//            Core.rotate(matResult, matResult, Core.ROTATE_180);
                Log.d("매트릭스 값", matResult.width() + " 가로 " + matResult.height() + " 세로");
                FaceDetect.west = false;
            }

//            if (!FaceDetect.west.onCameraS.equals(FaceDetect.west.currentF)) {
////            {
//////                Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);
//                FaceDetect.west.onCameraS = FaceDetect.west.currentF;
//                if (degree > 355 || degree < 10) {
//                    Log.d("이 각도는", "남 => matResult");
//                    Log.d("각도 in", "남쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
////                Core.flip(matResult, matResult, 1);
////                Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.flip(matResult, matResult, -1);
////                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
////                Core.rotate(matResult, matResult, Core.ROTATE_180);
//                    FaceDetect.west.south = false;
//                } else if (degree > 80 && degree < 100) {
//                    Log.d("이 각도는", "동 => matResult");
//                    Log.d("각도 in", "동쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
////                Core.flip(matResult, matResult, 1);
//                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
////            Core.rotate(matResult, matResult, Core.ROTATE_180);
//                    FaceDetect.west.east = false;
//                } else if (degree > 170 && degree < 190) {
//                    Log.d("이 각도는", "북 => matResult");
//                    Log.d("각도 in", "북쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
//
////                Core.flip(matResult, matResult, 1);
////                Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////                Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
//                    Core.rotate(matResult, matResult, Core.ROTATE_180);
//                    FaceDetect.west.north = false;
//                } else if (degree > 260 && degree < 280) {
//                    Log.d("이 각도는", "서 => matResult");
//                    Log.d("각도 in", "서쪽 " + "동" + FaceDetect.west.east + "서" + FaceDetect.west.west + "남" + FaceDetect.west.south + "북" + FaceDetect.west.north);
////                Core.flip(matResult, matResult, 1);
//                    Core.rotate(matResult, matResult, Core.ROTATE_90_CLOCKWISE);
////                    Core.rotate(matResult, matResult, Core.ROTATE_90_COUNTERCLOCKWISE);
////            Core.rotate(matResult, matResult, Core.ROTATE_180);
//                    FaceDetect.west.west = false;
//                }
//            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        releaseWriteLock();

        return matResult;
    }



    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
//    String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS  = {"android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    // 기존 코드 주석처리

                    // if (!cameraPermissionAccepted)

                    //    showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");


                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    }else
                    {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( FaceDetect.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


}
