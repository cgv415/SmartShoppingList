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
package com.example.garrido.listadelacompra.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.example.garrido.listadelacompra.OcrGraphic;
import com.google.android.gms.vision.CameraSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.<p>
 *
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.<p>
 *
 * Associated {@link Graphic} items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 * <ol>
 * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
 * supplied value from the preview scale to the view scale.</li>
 * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.</li>
 * </ol>
 */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> mGraphics = new HashSet<>();

    private TreeMap<Float,String> coordenadas = new TreeMap<>();
    private TreeMap<Float,String> treebloques = new TreeMap<>();
    private TreeMap<Float,Float> pos = new TreeMap<>();
    private ArrayList<ArrayList<Float>> posicionTotal = new ArrayList<>();


    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the {@link Graphic#draw(Canvas)} method to define the
     * graphics element.  Add instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
     */
    public static abstract class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         * <ol>
         * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
         * the supplied value from the preview scale to the view scale.</li>
         * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Returns true if the supplied coordinates are within this graphic.
         */
        public abstract boolean contains(float x, float y);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateX(float x) {
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Removes all graphics from the overlay.
     */
    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
            coordenadas.clear();
            pos.clear();
            posicionTotal.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public void add(T graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);

            OcrGraphic ocrGraphic = (OcrGraphic) graphic;
            pos.putAll(ocrGraphic.getPos());
            posicionTotal.add(ocrGraphic.getPosTotal());
            coordenadas.putAll(ocrGraphic.getTop());
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove(T graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     * @return First graphic containing the point, or null if no text is detected.
     */
    public T getGraphicAtLocation(float rawX, float rawY) {
        synchronized (mLock) {
            // Get the position of this View so the raw location can be offset relative to the view.
            int[] location = new int[2];
            this.getLocationOnScreen(location);
            for (T graphic : mGraphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic;
                }
            }
            return null;
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }

    public TreeMap<Float, Float> getPos() {
        return pos;
    }

    public TreeMap<Float, String> getCoordenadas() {
        return coordenadas;
    }

    public ArrayList<String> getBloques(){
        TreeMap<Float,ArrayList<String>> treebloques;

        ArrayList<ArrayList<Float>> fusion = new ArrayList<>();
        Float oldTop = 0f;
        Float oldBottom = 0f;

        Set<Map.Entry<Float, Float>> entrySet = pos.entrySet();
        Iterator<Map.Entry<Float, Float>> iterator = entrySet.iterator();
        while (iterator.hasNext()){
            Map.Entry<Float, Float> value = iterator.next();
            if(oldTop==0f && oldBottom==0){
                oldTop = value.getKey();
                oldBottom = value.getValue();

                if(iterator.hasNext()){
                    value = iterator.next();
                }
            }

            Float top = value.getKey();
            Float bottom = value.getValue();

            if(top<oldBottom){

                ArrayList<Float> fus = new ArrayList<>();
                fus.add(oldTop);
                fus.add(top);

                boolean add = false;
                for(int i = 0 ; i < fusion.size();i++){
                    ArrayList<Float> arf = fusion.get(i);
                    Float f = arf.get(0);

                    if(oldTop==f){
                        arf.add(top);
                        fusion.set(i,arf);
                        add = true;
                        break;
                    }
                }
                if(!add){
                    fusion.add(fus);
                }

            }else{
                ArrayList<Float> fus = new ArrayList<>();
                fus.add(top);
                fusion.add(fus);
                oldTop = top;
                oldBottom = bottom;
            }

        }

        treebloques = getTreeMap(fusion);


        return getArrayList(treebloques);
    }

    public TreeMap<Float,ArrayList<String>> getTreeMap(ArrayList<ArrayList<Float>> fusion){
        TreeMap<Float,ArrayList<String>> treeFusion = new TreeMap<>();

        for(int i = 0 ; i < fusion.size() ; i++){

            ArrayList<Float> arr = fusion.get(i);
            ArrayList<String> bloque = new ArrayList<>();

            for(int j = 0 ; j < arr.size() ; j++){
                Float f = arr.get(j);
                String text = coordenadas.get(f);
                bloque.add(text);
            }
            treeFusion.put(arr.get(0),bloque);
        }

        return treeFusion;
    }

    public ArrayList<String> getArrayList(TreeMap<Float, ArrayList<String>> treeMap){
        treebloques = new TreeMap<>();
        ArrayList<String> bloques = new ArrayList<>();
        Set<Map.Entry<Float, ArrayList<String>>> entrySet = treeMap.entrySet();
        Iterator<Map.Entry<Float, ArrayList<String>>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Float, ArrayList<String>> bloque = iterator.next();
            Float key = bloque.getKey();
            ArrayList<String> bloquevalue = bloque.getValue();

            String b = "";
            for (String x:
                 bloquevalue) {
                if(b!=""){
                    b+="\t"+x;
                }else{
                    b+=x;
                }

            }
            treebloques.put(key,b);
            bloques.add(b);

        }
        return bloques;
    }

    public String getBloque(Float rawX,Float rawY){
        // Get the position of this View so the raw location can be offset relative to the view.
        int[] location = new int[2];
        this.getLocationOnScreen(location);
        rawY = rawY - location[1];


        ArrayList<Float> topes = new ArrayList<>();
        Float top = -1f;
        Set<Map.Entry<Float, String>> entrySet = treebloques.entrySet();
        Iterator<Map.Entry<Float, String>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Float, String> bloque = iterator.next();
            Float key = bloque.getKey();
            topes.add(key);
        }

        for (Float t :
                topes) {
            Float dif = rawY-t;
            if((top==-1f)||(dif<top && dif > 0)){
                top=t;
            }
        }

        String bloque = treebloques.get(top);

        return bloque;
    }

    public ArrayList<String> obtenerBloques(){

        posicionTotal = sortPosicionTotal();

        ArrayList<String> bloques = new ArrayList<>();
        ArrayList<ArrayList<Float>> fusion = new ArrayList<>();
        ArrayList<Float> fus;

        ArrayList<TreeMap<Float,Float>> arrayXY = new ArrayList<>();

        Float top;
        Float bottom;

        Float oldTop = 0f;
        Float oldBottom = 0f;

        Set<Map.Entry<Float, Float>> entrySet = pos.entrySet();
        Iterator<Map.Entry<Float, Float>> iterator = entrySet.iterator();
        while (iterator.hasNext()){
            fus = new ArrayList<>();
            Map.Entry<Float,Float> entry = iterator.next();
            top = entry.getKey();
            bottom = entry.getValue();

            if(oldTop.equals(0f)&&oldBottom.equals(0f)){
                fus.add(top);
                fusion.add(fus);

                oldTop = top;
                //oldBottom = bottom;
                if(oldBottom < bottom){
                    oldBottom = bottom;
                }

            }else if(top <= oldBottom){
                for(int i = 0 ; i < fusion.size() ; i ++){
                    ArrayList<Float> f = fusion.get(i);
                    if(f.contains(oldTop)){
                        f.add(top);
                        fusion.set(i,f);

                        oldTop = top;
                        if(oldBottom < bottom){
                            oldBottom = bottom;
                        }
                    }
                }
            }else{
                fus.add(top);
                fusion.add(fus);

                oldTop = top;
                oldBottom = bottom;
            }

        }

        TreeMap<Float,Float> xy;

        for (ArrayList<Float> arf :
                fusion) {
            xy = new TreeMap<>();
            for(Float f :
                    arf){
                for (ArrayList<Float> post :
                        posicionTotal) {
                    Float y = post.get(0);
                    if(y.equals(f)){
                        Float x = post.get(2);
                        xy.put(x,y);
                    }
                }
            }
            arrayXY.add(xy);
        }
        for (TreeMap<Float, Float> treeMap :
                arrayXY) {
            String text = "";
            Set<Map.Entry<Float, Float>> entryMap = treeMap.entrySet();
            Iterator<Map.Entry<Float,Float>> itMap = entryMap.iterator();
            while(itMap.hasNext()){
                Map.Entry<Float,Float> entry = itMap.next();
                text += coordenadas.get(entry.getValue());
                if(itMap.hasNext()){
                    text = text.toLowerCase() + "\t";
                }else{
                    bloques.add(text);
                }

            }
        }

        return bloques;
    }

    public ArrayList<ArrayList<Float>> sortPosicionTotal(){
        ArrayList<ArrayList<Float>> tmp = (ArrayList<ArrayList<Float>>) posicionTotal.clone();
        ArrayList<ArrayList<Float>> sort = new ArrayList<>();


        Set<Map.Entry<Float, Float>> entryMap = pos.entrySet();
        Iterator<Map.Entry<Float, Float>> iterator = entryMap.iterator();
        while(iterator.hasNext()){
            Map.Entry<Float, Float> entry = iterator.next();
            Float Y = entry.getKey();

            for(ArrayList<Float> f : tmp){
                if(f.contains(Y)){
                    sort.add(f);
                }
            }
        }

        return sort;
    }

}



//Todo modificado
/**
 private ArrayList<String> arGraphics = new ArrayList<>();

 public void clear() {
    synchronized (mLock) {
        mGraphics.clear();
        arGraphics.clear();
    }
    postInvalidate();
 }

public void add(T graphic) {
    synchronized (mLock) {
        mGraphics.add(graphic);
        OcrGraphic ocrGraphic = (OcrGraphic) graphic;
        arGraphics.add(ocrGraphic.getText());
    }
    postInvalidate();
}
 public ArrayList<String> getArGraphics() {
    return arGraphics;
 }
 */
