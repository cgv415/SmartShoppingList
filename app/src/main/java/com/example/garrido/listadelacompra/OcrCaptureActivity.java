/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.garrido.listadelacompra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.example.garrido.listadelacompra.camera.CameraSource;
import com.example.garrido.listadelacompra.camera.CameraSourcePreview;
import com.example.garrido.listadelacompra.camera.GraphicOverlay;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity {
    private DataBaseManager manager;

    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private Local local;
    private String fecha;
    private String hora;
    private TreeMap<String,Double> productos;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ocr_capture);

        manager = new DataBaseManager(this);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        //mGraphicOverlay.pruebas();
        //ArrayList<String> bloques = mGraphicOverlay.obtenerBloques();
        TreeMap<Producto,Double> productos = new TreeMap<>();
        ArrayList<Producto> arrProductos = new ArrayList<>();
        ArrayList<Double> precios = new ArrayList<>();

        /*Todo Bajar a onTap*/

        //String product = "1 pipas gigantes\t1 12 huevos\n1 cocacola n 33\n1 l semi s/lacto\n1 spray invis m.\n1 copa chocolate\n1 granada granel\tdescripción\n1 bolsa pequena\n1 taquito jamon\n1 libritos lomo\n1 naranja\n1 barra de pan\n1 patata 3 kg\tdescripción\n1 bolsa pequena\n1 taquito jamon\n1 libritos lomo\n1 naranja\n1 barra de pan\n1 patata 3 kg\tsubtotal\t0,02\n1,99\n2.28\n0,46\n0,45\n3,40\n0,95\n1,29\n0,60\n4,50\n1,70\n0,85";
        String product = "1 FOSFOROS GR.\n1 TURRON\n1 TURRON\n1 BOLSA PEQUENA\n1 NATA SPRAY S/L\n1 VELA CIFRA 2\n1 VELA CIFRA 5\n1 BOLLERIA GRANE\tprecio importe\t0,90\n2,50\n2,50\n0,02\n1,50\n0,65\n0,65\n3,83";
        //String productn = "D\n1. PIPAS GIGANTES\n1 12 HUEVOS\n1 COCACOLA N 33\n1 L SEMI S/LACTO\n1 SPRAY INVIS M.\n1 COPA CHOCOLATE\n1 GRANADA GRANEL\nDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\nDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\nPrecio Importe\n0,02\n1,99\n2.28\n0,46\n0,45\n3,40\n0,95\n1,29\n0,60\n4,50\n1,70\n0,85";
        Local local = manager.obtenerLocal("mercadona s.a");

        ArrayList<Producto> p = manager.obtenerLocal_Productos(local);

        StringTokenizer sttoken1 = new StringTokenizer(product,"\t");
        while(sttoken1.hasMoreTokens()){

            String token = sttoken1.nextToken();

            if(token.contains("precio")||token.contains("total")||token.contains("importe")){

                token  = sttoken1.nextToken();
                StringTokenizer sttoken2 = new StringTokenizer(token,"\n");
                while(sttoken2.hasMoreTokens()){
                    try{
                        String dob = sttoken2.nextToken().substring(0,4);
                        dob = dob.replace(",",".");
                        double precio =Double.parseDouble(dob);
                        precios.add(precio);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for(int i = 0 ; i < arrProductos.size();i++){
                    try{
                        productos.put(arrProductos.get(i),precios.get(i));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }else{
                StringTokenizer sttoken2 = new StringTokenizer(token,"\n");
                while(sttoken2.hasMoreTokens()){
                    token = sttoken2.nextToken();
                    Producto producto = new Producto();
                    for(int i = 0; i < p.size(); i++){
                        String nombre = p.get(i).getNombre();

                        if(token.contains(nombre)){
                            producto = p.get(i);
                        }
                    }
                    if(producto.getNombre() != null){
                        arrProductos.add(producto);
                    }else{
                        Producto pr = new Producto(token);
                        arrProductos.add(pr);
                    }
                }
            }



        }
        /*Todo fin*/
        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //todo modificado
        Snackbar.make(mGraphicOverlay, "Presiona sobre los productos comprados",
                Snackbar.LENGTH_LONG)
                .show();
        /*
        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();*/
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        String seleccionado = graphic.getText();
        //todo Modificado añadido
        //MERCADONA S.A.\nMARIA CASARES,AV A DE ACA\nTeiefono 950206192

        //P.V.P. I.V.A. INCLUIDO\n03/09/2018 19:44 OP: 275784\nF.SIMPLIFICADA: 3576-021-229355
       // TreeMap<Float,Float> pos = mGraphicOverlay.getPos();
        //TreeMap<Float,String> coordenadas = mGraphicOverlay.getCoordenadas();

        //ArrayList<String> bloques = mGraphicOverlay.getBloques();
        //seleccionado = mGraphicOverlay.getBloque(rawX,rawY);

        ArrayList<String> bloques = mGraphicOverlay.obtenerBloques();

        fecha = "";
        hora = "";
        local = new Local("");
        for (String bloque :
                bloques) {
            StringTokenizer tokenizer = new StringTokenizer(bloque, "\n");
            String token = "";
            while(tokenizer.hasMoreTokens()){
                token = tokenizer.nextToken();
                if(fecha==""){
                    StringTokenizer tokFechaHora = new StringTokenizer(token," ");
                    String fech = tokFechaHora.nextToken();
                    StringTokenizer tokFecha = new StringTokenizer(fech,"/");
                    if(tokFecha.countTokens()> 1){
                        try{
                            int dia = Integer.parseInt(tokFecha.nextToken());
                            fecha = fech;
                            hora = tokFechaHora.nextToken();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{
                        tokFecha = new StringTokenizer(fech,"-");
                        if(tokFecha.countTokens()> 1){
                            try{
                                int dia = Integer.parseInt(tokFecha.nextToken());
                                fecha = fech;
                                hora = tokFechaHora.nextToken();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            tokFecha = new StringTokenizer(fech, ".");
                            if (tokFecha.countTokens() > 1) {
                                try {
                                    int dia = Integer.parseInt(tokFecha.nextToken());
                                    fecha = fech;
                                    hora = tokFechaHora.nextToken();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if(local.getNombre()==""){
                    local = new Local(token);
                }
            }
        }

        ArrayList<Producto> p = manager.obtenerLocal_Productos(local);
        TreeMap<Producto,Double> productos = new TreeMap<>();
        ArrayList<Producto> arrProductos = new ArrayList<>();
        ArrayList<Double> precios = new ArrayList<>();

        StringTokenizer sttoken1 = new StringTokenizer(seleccionado,"\t");
        while(sttoken1.hasMoreTokens()) {

            String token = sttoken1.nextToken();

            if (token.contains("precio") || token.contains("total") || token.contains("importe")) {

                token = sttoken1.nextToken();
                StringTokenizer sttoken2 = new StringTokenizer(token, "\n");
                while (sttoken2.hasMoreTokens()) {
                    try {
                        String dob = sttoken2.nextToken().substring(0, 4);
                        dob = dob.replace(",", ".");
                        double precio = Double.parseDouble(dob);
                        precios.add(precio);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < arrProductos.size(); i++) {
                    try {
                        productos.put(arrProductos.get(i), precios.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                StringTokenizer sttoken2 = new StringTokenizer(token, "\n");
                while (sttoken2.hasMoreTokens()) {
                    token = sttoken2.nextToken();
                    Producto producto = new Producto();
                    for (int i = 0; i < p.size(); i++) {
                        String nombre = p.get(i).getNombre();

                        if (token.contains(nombre)) {
                            producto = p.get(i);
                        }
                    }
                    if (producto.getNombre() != null) {
                        arrProductos.add(producto);
                    } else {
                        Producto pr = new Producto(token);
                        arrProductos.add(pr);
                    }
                }
            }

        }
        /*

        "D\t1. PIPAS GIGANTES\t1 12 HUEVOS\n1 COCACOLA N 33\n1 L SEMI S/LACTO\n1 SPRAY INVIS M.\n1 COPA CHOCOLATE\n1 GRANADA GRANEL\tDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\tDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\tPrecio Importe\t0,02\n1,99\n2.28\n0,46\n0,45\n3,40\n0,95\n1,29\n0,60\n4,50\n1,70\n0,85"
         */

        //String product = "D\t1. PIPAS GIGANTES\t1 12 HUEVOS\n1 COCACOLA N 33\n1 L SEMI S/LACTO\n1 SPRAY INVIS M.\n1 COPA CHOCOLATE\n1 GRANADA GRANEL\tDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\tDescripción\n1 BOLSA PEQUENA\n1 TAQUITO JAMON\n1 LIBRITOS LOMO\n1 NARANJA\n1 BARRA DE PAN\n1 PATATA 3 KG\tPrecio Importe\t0,02\n1,99\n2.28\n0,46\n0,45\n3,40\n0,95\n1,29\n0,60\n4,50\n1,70\n0,85";
        //String product = "1 FOSFOROS GR.\n1 TURRON\n1 TURRON\n1 BOLSA PEQUENA\n1 NATA SPRAY S/L\n1 VELA CIFRA 2\n1 VELA CIFRA 5\n1 BOLLERIA GRANE\n\t0,90\n2,50\n2,50\n0,02\n1,50\n0,65\n0,65\n3,83";
        //fin añadido

        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Intent data = new Intent();
                data.putExtra(TextBlockObject, text.getValue());
                setResult(CommonStatusCodes.SUCCESS, data);
                finish();
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    public void analizarBloque(String bloque){
        StringTokenizer tokenizer = new StringTokenizer(bloque,"\n");


        if(tokenizer.hasMoreTokens()){
            String line = "";
            line = tokenizer.nextToken();
            Local local = manager.obtenerLocal(line);
            if(local.getId()!="0"){

            }
        }
    }
}
//Todo Modificado añadido
    /*
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        ArrayList<String> bloques = mGraphicOverlay.getArGraphics();
        ...
    }
*/