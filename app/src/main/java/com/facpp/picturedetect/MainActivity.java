package com.facpp.picturedetect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * A simple demo, get a picture form your phone<br />
 * Use the facepp api to detect<br />
 * Find all face on the picture, and mark them out.
 * @author moon5ckq
 */
public class MainActivity extends Activity {

    final private static String TAG = "MainActivity";
    final private int PICTURE_CHOOSE = 1;

    private ImageView imageView = null;
    private Bitmap img = null;
    private Button buttonDetect = null;
    private TextView textView = null;

    public static Context context;
    static TextView nameText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        nameText = (TextView)findViewById(R.id.name);





        Button button = (Button)this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                //get a picture form your phone
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
            }
        });

        textView = (TextView)this.findViewById(R.id.textView1);

        buttonDetect = (Button)this.findViewById(R.id.button2);
        buttonDetect.setVisibility(View.INVISIBLE);
        buttonDetect.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                textView.setText("Waiting ...");

                FaceppDetect faceppDetect = new FaceppDetect();
                faceppDetect.setDetectCallback(new DetectCallback() {

                    public void detectResult(FaceppResult rst) {
                        //Log.v(TAG, rst.toString());
                        //use the red paint
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

                        //create a new canvas
                        Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawBitmap(img, new Matrix(), null);
                        try {
                            //find out all faces
                            int count =0;
                            try {
                                count = rst.get("face").getCount();
                            } catch (FaceppParseException e1) {
                                e1.printStackTrace();
                            }
                            for (int facenum = 0; facenum < Math.min(count,1); ++facenum) {
                                FaceppResult face = null;
                                try {
                                    face = rst.get("face").get(facenum);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                FaceppResult position = null;
                                try {
                                    position = face.get("position");
                                } catch (FaceppParseException e1) {
                                    e1.printStackTrace();
                                }
                                // xy position attributes
                                String[] paxy = {"center", "mouth_right", "mouth_left", "eye_right", "eye_left", "nose"};
                                String[] laxy = {"contour_chin_x", "contour_chin_y", "contour_left1_x", "contour_left1_y", "contour_left2_x", "contour_left2_y", "contour_left3_x", "contour_left3_y", "contour_left4_x", "contour_left4_y", "contour_left5_x", "contour_left5_y", "contour_left6_x", "contour_left6_y", "contour_left7_x", "contour_left7_y", "contour_left8_x", "contour_left8_y", "contour_left9_x", "contour_left9_y", "contour_right1_x", "contour_right1_y", "contour_right2_x", "contour_right2_y", "contour_right3_x", "contour_right3_y", "contour_right4_x", "contour_right4_y", "contour_right5_x", "contour_right5_y", "contour_right6_x", "contour_right6_y", "contour_right7_x", "contour_right7_y", "contour_right8_x", "contour_right8_y", "contour_right9_x", "contour_right9_y", "left_eye_bottom_x", "left_eye_bottom_y", "left_eye_center_x", "left_eye_center_y", "left_eye_left_corner_x", "left_eye_left_corner_y", "left_eye_lower_left_quarter_x", "left_eye_lower_left_quarter_y", "left_eye_lower_right_quarter_x", "left_eye_lower_right_quarter_y", "left_eye_pupil_x", "left_eye_pupil_y", "left_eye_right_corner_x", "left_eye_right_corner_y", "left_eye_top_x", "left_eye_top_y", "left_eye_upper_left_quarter_x", "left_eye_upper_left_quarter_y", "left_eye_upper_right_quarter_x", "left_eye_upper_right_quarter_y", "left_eyebrow_left_corner_x", "left_eyebrow_left_corner_y", "left_eyebrow_lower_left_quarter_x", "left_eyebrow_lower_left_quarter_y", "left_eyebrow_lower_middle_x", "left_eyebrow_lower_middle_y", "left_eyebrow_lower_right_quarter_x", "left_eyebrow_lower_right_quarter_y", "left_eyebrow_right_corner_x", "left_eyebrow_right_corner_y", "left_eyebrow_upper_left_quarter_x", "left_eyebrow_upper_left_quarter_y", "left_eyebrow_upper_middle_x", "left_eyebrow_upper_middle_y", "left_eyebrow_upper_right_quarter_x", "left_eyebrow_upper_right_quarter_y", "mouth_left_corner_x", "mouth_left_corner_y", "mouth_lower_lip_bottom_x", "mouth_lower_lip_bottom_y", "mouth_lower_lip_left_contour1_x", "mouth_lower_lip_left_contour1_y", "mouth_lower_lip_left_contour2_x", "mouth_lower_lip_left_contour2_y", "mouth_lower_lip_left_contour3_x", "mouth_lower_lip_left_contour3_y", "mouth_lower_lip_right_contour1_x", "mouth_lower_lip_right_contour1_y", "mouth_lower_lip_right_contour2_x", "mouth_lower_lip_right_contour2_y", "mouth_lower_lip_right_contour3_x", "mouth_lower_lip_right_contour3_y", "mouth_lower_lip_top_x", "mouth_lower_lip_top_y", "mouth_right_corner_x", "mouth_right_corner_y", "mouth_upper_lip_bottom_x", "mouth_upper_lip_bottom_y", "mouth_upper_lip_left_contour1_x", "mouth_upper_lip_left_contour1_y", "mouth_upper_lip_left_contour2_x", "mouth_upper_lip_left_contour2_y", "mouth_upper_lip_left_contour3_x", "mouth_upper_lip_left_contour3_y", "mouth_upper_lip_right_contour1_x", "mouth_upper_lip_right_contour1_y", "mouth_upper_lip_right_contour2_x", "mouth_upper_lip_right_contour2_y", "mouth_upper_lip_right_contour3_x", "mouth_upper_lip_right_contour3_y", "mouth_upper_lip_top_x", "mouth_upper_lip_top_y", "nose_contour_left1_x", "nose_contour_left1_y", "nose_contour_left2_x", "nose_contour_left2_y", "nose_contour_left3_x", "nose_contour_left3_y", "nose_contour_lower_middle_x", "nose_contour_lower_middle_y", "nose_contour_right1_x", "nose_contour_right1_y", "nose_contour_right2_x", "nose_contour_right2_y", "nose_contour_right3_x", "nose_contour_right3_y", "nose_left_x", "nose_left_y", "nose_right_x", "nose_right_y", "nose_tip_x", "nose_tip_y", "right_eye_bottom_x", "right_eye_bottom_y", "right_eye_center_x", "right_eye_center_y", "right_eye_left_corner_x", "right_eye_left_corner_y", "right_eye_lower_left_quarter_x", "right_eye_lower_left_quarter_y", "right_eye_lower_right_quarter_x", "right_eye_lower_right_quarter_y", "right_eye_pupil_x", "right_eye_pupil_y", "right_eye_right_corner_x", "right_eye_right_corner_y", "right_eye_top_x", "right_eye_top_y", "right_eye_upper_left_quarter_x", "right_eye_upper_left_quarter_y", "right_eye_upper_right_quarter_x", "right_eye_upper_right_quarter_y", "right_eyebrow_left_corner_x", "right_eyebrow_left_corner_y", "right_eyebrow_lower_left_quarter_x", "right_eyebrow_lower_left_quarter_y", "right_eyebrow_lower_middle_x", "right_eyebrow_lower_middle_y", "right_eyebrow_lower_right_quarter_x", "right_eyebrow_lower_right_quarter_y", "right_eyebrow_right_corner_x", "right_eyebrow_right_corner_y", "right_eyebrow_upper_left_quarter_x", "right_eyebrow_upper_left_quarter_y", "right_eyebrow_upper_middle_x", "right_eyebrow_upper_middle_y", "right_eyebrow_upper_right_quarter_x", "right_eyebrow_upper_right_quarter_y"};
                                double[] ct = new double[paxy.length*2+laxy.length*2];
                                for(int i2 = 0; i2 < paxy.length; i2++) {
                                    try {
                                        ct[2*i2] = Double.parseDouble(position.get(paxy[i2]).get("x").toString());
                                    } catch (FaceppParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        ct[2*i2+1] = Double.parseDouble(position.get(paxy[i2]).get("y").toString());
                                    } catch (FaceppParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }


                                HttpClient client = new DefaultHttpClient();
                                String getURL = "https://faceplusplus-faceplusplus.p.mashape.com/detection/landmark";
                                HttpPost post = new HttpPost(getURL);
                                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                                // Request parameters and other properties.
                                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                                //    System.out.println(face.get("face_id"));
                                try {
                                    params.add(new BasicNameValuePair("face_id", face.get("face_id").toString()));
                                } catch (FaceppParseException e1) {
                                    e1.printStackTrace();
                                }
                                params.add(new BasicNameValuePair("api_key", "59086b3014c7885411efb77898c367c5"));
                                params.add(new BasicNameValuePair("api_secret", "Wk9glK0FCAMlpFb7esh8IPqP_M8rHANJ"));
                                post.setHeader("X-Mashape-Key", "eRE4dzZ0DNmshCi7AC5XYKuH3VtHp17rIcYjsnjIM8mJ9sYfdY");
                                post.setHeader("Accept", "application/json");
                                for(int index=0; index < params.size(); index++) {
                                    try {
                                        entity.addPart(params.get(index).getName(), new StringBody(params.get(index).getValue()));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    // entity.addPart(params.get(index).getName(), new FileBody(new File (params.get(index).getValue())));
                                }
                                post.setEntity(entity);
                                HttpResponse responsePost = null;
                                try {
                                    responsePost = client.execute(post);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                HttpEntity resEntityGet = ((org.apache.http.HttpResponse) responsePost).getEntity();
                                String res = null;
                                try {
                                    res = EntityUtils.toString(resEntityGet);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //     System.out.println(res);
                                res = res.replaceAll(",", " ");
                                res = res.replaceAll("[0-9]*([a-z]|_)+[0-9]*", " ");
                                res = res.replaceAll("\"", " ");
                                res = res.replaceAll("(\\{|\\}|\\[|\\]|:)", " ");
                                //     System.out.println(res);
                                Scanner sss = new Scanner(res);
                                int kk = 0;
                                while(sss.hasNextDouble()) {
                                    ct[2*paxy.length + kk] = sss.nextDouble();
                                    kk++;
                                }
                                int nn = 2*paxy.length + 2*laxy.length;
                                for(int i = 2; i < nn; i += 2) {
                                    ct[i] -= ct[0];
                                    ct[i+1] -= ct[1];
                                }
                                double maxDistHere = 0;
                                for(int i = 2; i < nn; i += 2) {
                                    double cd = Math.sqrt(ct[i]*ct[i] + ct[i+1]*ct[i+1]);
                                    if(cd > maxDistHere) maxDistHere = cd;
                                }
                                for(int i = 2; i < nn; i++) {
                                    ct[i] /= maxDistHere;
                                }

                                double[] cc = new double[nn-2];
                                System.arraycopy(ct,2,cc,0,nn-2);
                                int m = nn-2;

                                Scanner s = new Scanner(getResources().getString(R.string.landmark_input));
                                String header = s.nextLine();
                                Log.v("tag1", "header: "+header);
                                String[] hs = header.split("\\^");
                                int n = hs.length;
                                m = n-4;
                                double[][] c = new double[300][m];
                                String[] names = new String[300];
                                String[]  urls = new String[300];
                                int j = 0;
                                while(s.hasNextLine()) {

                                    String ss = s.nextLine();
                                    Log.v("tag1", "ss  "+ss);
                                    String[] as = ss.split("\\^");
                                    names[j] = as[0];
                                    urls[j] = as[1];
                                    double[] coord = new double[n];
                                    double cx = Double.parseDouble(as[2]);
                                    double cy = Double.parseDouble(as[3]);
                                    for(int i = 4; i < n; i += 2) {
                                        double x = Double.parseDouble(as[i]);
                                        double y = Double.parseDouble(as[i+1]);
                                        x = x - cx;
                                        y = y - cy;
                                        x/=2.0;
                                        coord[i] = x;
                                        coord[i+1] = y;
                                    }
                                    double maxDist = 0;
                                    for(int i = 4; i < n; i += 2) {
                                        double cd = Math.sqrt(coord[i]*coord[i] + coord[i+1]*coord[i+1]);
                                        if(cd > maxDist) maxDist = cd;
                                    }
                                    for(int i = 4; i < n; i++) {
                                        coord[i] /= maxDist;
                                    }
                                    System.arraycopy(coord, 4, c[j], 0, n-4);

                                    j++;
                                }
                                n = j; // Number of faces.

                                class Item implements Comparable {
                                    double dist;
                                    int mi, mj;
                                    public Item(double d, int i, int j) {
                                        dist = d;
                                        mi = i;
                                        mj = j;
                                    }
                                    public int compareTo(Object other) {
                                        Item oi = (Item)other;
                                        if(oi.dist < dist) return 1;
                                        else if (oi.dist == dist) return 0;
                                        else return -1;
                                    }
                                }
                                ArrayList<Item> aa = new ArrayList<Item>();
                                String[] aw = getResources().getString(R.string.weights).split("\n");
                                Log.v("tag1", "words "+n);
                                for(j = 0; j < paxy.length+laxy.length; j++) {
                                    double currd = 0;
                                    for(int k = 0; k < m; k += 2) {
                                        currd += (0.7+Math.random())*Math.sqrt((cc[k]-c[j][k])*(cc[k] - c[j][k]) + (cc[k+1]-c[j][k+1])*(cc[k+1]-c[j][k+1]));
                                    }
                                    aa.add(new Item(currd, -1, j));
                                }

                                Log.v("tag1", "About to loop "+aa.size());
                                Collections.sort(aa);
                                for(int i = 0; i < aa.size(); i++) {
                                    Item t = aa.get(i);

                                    //Log.v("tag1", t.dist + " " + urls[t.mj]);
                                    //Log.v("tag1", t.dist + " " + names[t.mj]);
                                }
                                setImage3(urls[aa.get(0).mj]);
                                setName3(names[aa.get(0).mj]);


                                float x, y, w, h;
                                //get the center point
                                x = (float)rst.get("face").get(facenum).get("position").get("center").get("x").toDouble().doubleValue();
                                y = (float)rst.get("face").get(facenum).get("position").get("center").get("y").toDouble().doubleValue();

                                //get face size
                                w = (float)rst.get("face").get(facenum).get("position").get("width").toDouble().doubleValue();
                                h = (float)rst.get("face").get(facenum).get("position").get("height").toDouble().doubleValue();
                                //change percent value to the real size
                                x = x / 100 * img.getWidth();
                                w = w / 100 * img.getWidth() * 0.7f;
                                y = y / 100 * img.getHeight();
                                h = h / 100 * img.getHeight() * 0.7f;

                                //draw the box to mark it out
                                canvas.drawLine(x - w, y - h, x - w, y + h, paint);
                                canvas.drawLine(x - w, y - h, x + w, y - h, paint);
                                canvas.drawLine(x + w, y + h, x - w, y + h, paint);
                                canvas.drawLine(x + w, y + h, x + w, y - h, paint);

                            }
                            //save new image
                            img = bitmap;

                            final int count2 = count;
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    //show the image
                                    imageView.setImageBitmap(img);
                                    textView.setText("Finished, "+ count2 + " faces.");
                                }
                            });
                        } catch (FaceppParseException e) {
                            e.printStackTrace();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    textView.setText("Error.");
                                }
                            });
                        }
                    }
                });
                faceppDetect.detect(img);


            }


        });

        imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(img);


    }

    void setImage2(String filename){
        // Display other image
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        int number = getResources().getIdentifier(filename,"drawable",getPackageName());
        say("Test "+number);
        imageView.setImageResource(number);

    }

    void setImage3(String imageUrl){
        new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                .execute(imageUrl);
    }

    void setName3(final String name){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                nameText.setText(name);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //the image picker callback
        if (requestCode == PICTURE_CHOOSE) {
            if (intent != null) {
                //The Android api ~~~
                //Log.d(TAG, "idButSelPic Photopicker: " + intent.getDataString());
                Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(ImageColumns.DATA);
                String fileSrc = cursor.getString(idx);
                //Log.d(TAG, "Picture:" + fileSrc);

                //just read size
                Options options = new Options();
                options.inJustDecodeBounds = true;
                img = BitmapFactory.decodeFile(fileSrc, options);

                //scale size to read
                options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / 1024f, (double)options.outHeight / 1024f)));
                options.inJustDecodeBounds = false;
                img = BitmapFactory.decodeFile(fileSrc, options);
                textView.setText("<== Compare my face!");

                img = rotateImage(img, fileSrc);

                imageView.setImageBitmap(img);
                buttonDetect.setVisibility(View.VISIBLE);
            }
            else {
                Log.d(TAG, "idButSelPic Photopicker canceled");
            }
        }
    }


    Bitmap rotateImage(Bitmap img, String filePath){
        // Detect angle taken
        int rotate = 0;
        try {

            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            //Log.i("RotateImage", "Exif orientation: " + orientation);
            //Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Rotate
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(img,img.getWidth(),img.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

        return rotatedBitmap;
    }

    private class FaceppDetect {
        DetectCallback callback = null;

        public void setDetectCallback(DetectCallback detectCallback) {
            callback = detectCallback;
        }

        public void detect(final Bitmap image) {

            new Thread(new Runnable() {

                public void run() {
                    HttpRequests httpRequests = new HttpRequests("59086b3014c7885411efb77898c367c5", "Wk9glK0FCAMlpFb7esh8IPqP_M8rHANJ");
                    httpRequests.setWebSite(false, false); // United States, Not Debug.
                    //Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
                    //Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());

                    imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] array = stream.toByteArray();

                    try {
                        //detect
                        FaceppResult result = httpRequests.detectionDetect(new PostParameters().setImg(array));
                        //finished , then call the callback function
                        if (callback != null) {
                            callback.detectResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                textView.setText("Network error.");
                            }
                        });
                    }

                }
            }).start();
        }
    }

    interface DetectCallback {
        void detectResult(FaceppResult rst);
    }


    public static void say(String words){
        Toast.makeText(context, words, Toast.LENGTH_LONG).show();
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            Log.v("tag1","in Constructor");
        }

        protected Bitmap doInBackground(String... urls) {
            Log.v("tag1","Starting async task");
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                Log.v("tag1","in Exception");
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.v("tag1", "In onPostExecute");
            Matrix matrix = new Matrix();
            matrix.setScale(2, 2);
            Bitmap newBitmap = Bitmap.createScaledBitmap(result,result.getWidth(),result.getHeight(),true);
            Bitmap scaled = Bitmap.createBitmap(newBitmap , 0, 0, newBitmap .getWidth(), newBitmap .getHeight(), matrix, true);
            bmImage.setImageBitmap(scaled);
        }
    }
}
