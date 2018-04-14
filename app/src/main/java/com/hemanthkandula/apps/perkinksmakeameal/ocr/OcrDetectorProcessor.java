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
package com.hemanthkandula.apps.perkinksmakeameal.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.cloudoki.demo3.MainActivity;
import com.cloudoki.demo3.OpenCV_now.ColorBlobDetectionActivity;
import com.cloudoki.demo3.ocr.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.security.PublicKey;

import static android.content.Context.MODE_PRIVATE;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    public Context context;
    public String preResult;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, Context context,String preResult) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.context = context;
        this.preResult = preResult;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
            System.out.println(item.getValue());

            System.out.println(item.getBoundingBox());
            System.out.println("aaaaa" );


            if(preResult.toLowerCase().contains(item.getValue().toLowerCase())){
                Intent intent = new Intent (context, ColorBlobDetectionActivity.class);
                System.out.println("bbbb" );
                System.out.println(item.getValue());
                System.out.println(item.getBoundingBox().centerX());


                intent.putExtra("boxX", item.getBoundingBox().centerX());
                intent.putExtra("boxY", item.getBoundingBox().centerY());
                SharedPreferences.Editor editor = context.getSharedPreferences("hi", MODE_PRIVATE).edit();
                editor.putInt("boxX", item.getBoundingBox().centerX());
                editor.putInt("boxY", item.getBoundingBox().centerY());
                editor.apply();

                context.startActivity(intent);
                ((OcrCaptureActivity)(context)).finish();


            }





        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
