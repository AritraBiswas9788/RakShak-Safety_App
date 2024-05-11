/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.rakshak_accidentsafetyapp.posedetector

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.example.rakshak_accidentsafetyapp.Activity.CameraXLivePreviewActivity
import com.example.rakshak_accidentsafetyapp.DataEvent
import com.example.rakshak_accidentsafetyapp.GraphicOverlay
import com.example.rakshak_accidentsafetyapp.ml.Model
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import org.greenrobot.eventbus.EventBus
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.max
import java.lang.Math.min
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


/** Draw the detected pose in preview. */
class PoseGraphic
internal constructor(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val poseClassification: List<String>,
    private val originalCameraImage: Bitmap?
) : GraphicOverlay.Graphic(overlay) {
    private var zMin = java.lang.Float.MAX_VALUE
    private var zMax = java.lang.Float.MIN_VALUE
    private val classificationTextPaint: Paint
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()
    private val leftPaint: Paint
    private val rightPaint: Paint
    private val whitePaint: Paint
    private var count = 0

//    private var storageRef:StorageReference

    init {
//        FirebaseApp.initializeApp(
//            applicationContext
//        )
//        storageRef = Firebase.storage.reference
        classificationTextPaint = Paint()
        classificationTextPaint.color = Color.WHITE
        classificationTextPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
        classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)

        textBackgroundPaint.color = Color.CYAN
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        whitePaint = Paint()
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        leftPaint = Paint()
        leftPaint.strokeWidth = STROKE_WIDTH
        leftPaint.color = Color.GREEN
        rightPaint = Paint()
        rightPaint.strokeWidth = STROKE_WIDTH
        rightPaint.color = Color.YELLOW
    }

    override fun draw(canvas: Canvas) {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }


        // Draw pose classification text.
        val classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f
        for (i in poseClassification.indices) {
            val classificationY =
                canvas.height -
                        (POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size - i).toFloat())
            canvas.drawText(
                poseClassification[i],
                classificationX,
                classificationY,
                classificationTextPaint
            )
        }

        // Draw all the points
        for (landmark in landmarks) {
            drawPoint(canvas, landmark, whitePaint)
            if (visualizeZ && rescaleZForVisualization) {
                zMin = min(zMin, landmark.position3D.z)
                zMax = max(zMax, landmark.position3D.z)
            }
        }

        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
        val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
        val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
        val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
        val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
        val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
        val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
        val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
        val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)

        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)


        /*Log.i("coords-check","left:${translateX(leftEyeOuter.position3D.x)}")
        Log.i("coords-check","top:${translateY(leftEye!!.position3D.y)}")
        Log.i("coords-check","right:${translateX(rightEyeOuter.position3D.x)}")
        Log.i("coords-check","bottom:${translateY(leftMouth.position3D.y)}")*/
        /*val radius = calcDistance(leftEyeOuter!!,rightEyeOuter!!)
        drawCircle(canvas, nose!!,radius)
        Log.i("radCheck",radius.toString())*/

        try {
            val rightPoint =
                getRightMost(arrayListOf(nose!!,leftEyeInner!!,leftEye!!,leftEyeOuter!!,rightEyeInner!!,rightEye!!,rightEyeOuter!!,leftEar!!,rightEar!!,leftMouth!!,rightMouth!!))
            val leftPoint =
                getLeftMost(arrayListOf(nose!!,leftEyeInner!!,leftEye!!,leftEyeOuter!!,rightEyeInner!!,rightEye!!,rightEyeOuter!!,leftEar!!,rightEar!!,leftMouth!!,rightMouth!!))
            val topPoint =
                getTopMost(arrayListOf(nose!!,leftEyeInner!!,leftEye!!,leftEyeOuter!!,rightEyeInner!!,rightEye!!,rightEyeOuter!!,leftEar!!,rightEar!!,leftMouth!!,rightMouth!!))
            val bottomPoint =
                getBottomMost(arrayListOf(nose!!,leftEyeInner!!,leftEye!!,leftEyeOuter!!,rightEyeInner!!,rightEye!!,rightEyeOuter!!,leftEar!!,rightEar!!,leftMouth!!,rightMouth!!))
            val hortpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.25f
            val vertpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.45f

            /*val width = if((rightPoint.position3D.x - padding).toInt()+((leftPoint.position3D.x + padding) - (rightPoint.position3D.x - padding)).toInt()>originalCameraImage.width)
                originalCameraImage.width-(rightPoint.position3D.x - padding).toInt()
            else
                ((leftPoint.position3D.x + padding) - (rightPoint.position3D.x - padding)).toInt()

            val height = if((topPoint.position3D.y - padding).toInt()+((bottomPoint.position3D.y + padding) - (topPoint.position3D.y - padding)).toInt()>originalCameraImage.height)
                originalCameraImage.height-(topPoint.position3D.y - padding).toInt()
            else
                ((bottomPoint.position3D.y + padding) - (topPoint.position3D.y - padding)).toInt()*/
            val crop2 = cropImage(
                originalCameraImage!!,
                kotlin.math.max((rightPoint.position3D.x - hortpadding).toInt(), 0),
                kotlin.math.max((topPoint.position3D.y - vertpadding).toInt(), 0),
                ((leftPoint.position3D.x + hortpadding) - (rightPoint.position3D.x - hortpadding)).toInt(),
                ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt(),
            )
            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean")
            {
                //draw face

                updateList(classification,"face")
                var hpad = calcDistance(rightPoint, leftPoint)*0.25f
                var vpad = calcDistance(topPoint,bottomPoint)*0.85f
//                hpad = max(hpad, 60.0f)
//                vpad = max(vpad, 60.0f)

                val left = translateX(rightPoint.position3D.x) - hpad
                val top = translateY(topPoint.position3D.y) - vpad
                val right = translateX(leftPoint.position3D.x) + hpad
                val bottom = translateY(bottomPoint.position3D.y) + vpad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)
                val drawableText = classification+" : "+String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }

            //uploadFile(crop2, "face")
        } catch (e: Exception) {
            Log.i("uploadCheck", "face$e")
        }
        /*try {

            var l = rightEar!!.position3D.x
            var t = Math.min(rightEye!!.position3D.y, leftEye!!.position3D.y)
            var r = leftEar!!.position3D.x
            var b = Math.max(leftMouth!!.position3D.y, rightMouth!!.position3D.y)
            val hortPadding = sqrt(
                (abs(r) - abs(l)).pow(2) + (abs(rightEar.position3D.y) - abs(leftEar.position3D.y)).pow(
                    2
                )
            ) * 0.85f
            val vertPadding = abs(b - t) * 1.5f

            l = Math.max(0.0f, l - hortPadding)
            r = Math.min(originalCameraImage.width.toFloat(), r + hortPadding)
            t = Math.max(0.0f, t - vertPadding)
            b = Math.min(originalCameraImage.height.toFloat(), b + vertPadding)

            val width = if ((l.toInt() + abs(r - l).toInt()) > originalCameraImage.width)
                originalCameraImage.width - l.toInt()
            else
                abs(r - l).toInt()

            val height = if ((t.toInt() + abs(b - t).toInt().toInt()) > originalCameraImage.height)
                originalCameraImage.height - t.toInt()
            else
                abs(b - t).toInt()

            val crop1 = cropImage(
                originalCameraImage!!,
                l.toInt(),
                t.toInt(),
                width,
                height
            )

            uploadFile(crop1, "face")
        } catch (e: Exception) {
            Log.i("uploadCheck", "head$e")
        }
*/
        //BODY
        try {
            val rightPoint =
                getRightMost(arrayListOf(rightShoulder!!, rightHip!!, leftHip!!, leftShoulder!!))
            val leftPoint =
                getLeftMost(arrayListOf(rightShoulder!!, rightHip!!, leftHip!!, leftShoulder!!))
            val topPoint =
                getTopMost(arrayListOf(rightShoulder!!, rightHip!!, leftHip!!, leftShoulder!!))
            val bottomPoint =
                getBottomMost(arrayListOf(rightShoulder!!, rightHip!!, leftHip!!, leftShoulder!!))
            val padding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.25f

            val width = if((rightPoint.position3D.x - padding).toInt()+((leftPoint.position3D.x + padding) - (rightPoint.position3D.x - padding)).toInt()>originalCameraImage!!.width)
                originalCameraImage!!.width-(rightPoint.position3D.x - padding).toInt()
            else
                ((leftPoint.position3D.x + padding) - (rightPoint.position3D.x - padding)).toInt()

            val height = if((topPoint.position3D.y - padding).toInt()+((bottomPoint.position3D.y + padding) - (topPoint.position3D.y - padding)).toInt()>originalCameraImage.height)
                originalCameraImage!!.height-(topPoint.position3D.y - padding).toInt()
            else
                ((bottomPoint.position3D.y + padding) - (topPoint.position3D.y - padding)).toInt()
            val crop2 = cropImage(
                originalCameraImage!!,
                (rightPoint.position3D.x - padding).toInt(),
                (topPoint.position3D.y - padding).toInt(),
                width,
                height,
            )

            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean")
            {
                //draw face
                updateList(classification,"body")
                var pad =max(calcDistance(rightPoint, leftPoint),calcDistance(topPoint,bottomPoint))*0.25f

//                hpad = max(hpad, 60.0f)
//                vpad = max(vpad, 60.0f)

                val left = translateX(rightPoint.position3D.x) - pad
                val top = translateY(topPoint.position3D.y) - pad
                val right = translateX(leftPoint.position3D.x) + pad
                val bottom = translateY(bottomPoint.position3D.y) + pad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)

                val drawableText = classification+" : "+String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }


            //uploadFile(crop2, "body")
        } catch (e: Exception) {
            Log.i("uploadCheck", "body$e")
        }

        //RIGHT-ARM
        try {
            val rightPoint =
                getRightMost(
                    arrayListOf(
                        rightShoulder!!,
                        rightElbow!!,
                        rightWrist!!,
                        rightThumb!!,
                        rightIndex!!,
                        rightPinky!!
                    )
                )
            val leftPoint =
                getLeftMost(
                    arrayListOf(
                        rightShoulder!!,
                        rightElbow!!,
                        rightWrist!!,
                        rightThumb!!,
                        rightIndex!!,
                        rightPinky!!
                    )
                )
            val topPoint =
                getTopMost(
                    arrayListOf(
                        rightShoulder!!,
                        rightElbow!!,
                        rightWrist!!,
                        rightThumb!!,
                        rightIndex!!,
                        rightPinky!!
                    )
                )
            val bottomPoint =
                getBottomMost(
                    arrayListOf(
                        rightShoulder!!,
                        rightElbow!!,
                        rightWrist!!,
                        rightThumb!!,
                        rightIndex!!,
                        rightPinky!!
                    )
                )
            val vertpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val hortPadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val crop2 = cropImage(
                originalCameraImage!!,
                Math.max((rightPoint.position3D.x - hortPadding).toInt(), 0),
                Math.max((topPoint.position3D.y - vertpadding).toInt(), 0),
                ((leftPoint.position3D.x + hortPadding) - (rightPoint.position3D.x - hortPadding)).toInt(),
                ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt(),
            )



            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean") {

                updateList(classification,"Right-Arm")
                var hpad = calcDistance(rightPoint, leftPoint)*0.25f
                var vpad = calcDistance(topPoint,bottomPoint)*0.25f
                //uploadFile(crop2, "right_hand")
                val left = translateX(rightPoint.position3D.x) - hpad
                val top = translateY(topPoint.position3D.y) - vpad
                val right = translateX(leftPoint.position3D.x) + hpad
                val bottom = translateY(bottomPoint.position3D.y) + vpad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)
                val drawableText = classification + " : " + String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }
        } catch (e: Exception) {
            Log.i("uploadCheck", "right_hand$e")
        }

        //LEFT-ARM
        try {
            val rightPoint =
                getRightMost(
                    arrayListOf(
                        leftShoulder!!,
                        leftElbow!!,
                        leftWrist!!,
                        leftThumb!!,
                        leftIndex!!,
                        leftPinky!!
                    )
                )
            val leftPoint =
                getLeftMost(
                    arrayListOf(
                        leftShoulder!!,
                        leftElbow!!,
                        leftWrist!!,
                        leftThumb!!,
                        leftIndex!!,
                        leftPinky!!
                    )
                )
            val topPoint =
                getTopMost(
                    arrayListOf(
                        leftShoulder!!,
                        leftElbow!!,
                        leftWrist!!,
                        leftThumb!!,
                        leftIndex!!,
                        leftPinky!!
                    )
                )
            val bottomPoint =
                getBottomMost(
                    arrayListOf(
                        leftShoulder!!,
                        leftElbow!!,
                        leftWrist!!,
                        leftThumb!!,
                        leftIndex!!,
                        leftPinky!!
                    )
                )
            val vertpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val hortPadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val crop2 =cropImage(
                originalCameraImage!!,
                Math.max((rightPoint.position3D.x - hortPadding).toInt(), 0),
                Math.max((topPoint.position3D.y - vertpadding).toInt(), 0),
                ((leftPoint.position3D.x + hortPadding) - (rightPoint.position3D.x - hortPadding)).toInt(),
                ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt(),
            )
            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean") {

                updateList(classification,"Left-Arm")
                var hpad = calcDistance(rightPoint, leftPoint)*0.25f
                var vpad = calcDistance(topPoint,bottomPoint)*0.25f
                //uploadFile(crop2, "right_hand")
                val left = translateX(rightPoint.position3D.x) - hpad
                val top = translateY(topPoint.position3D.y) - vpad
                val right = translateX(leftPoint.position3D.x) + hpad
                val bottom = translateY(bottomPoint.position3D.y) + vpad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)
                val drawableText = classification + " : " + String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }
            //uploadFile(crop2, "left_hand")
        } catch (e: Exception) {
            Log.i("uploadCheck", "left_hand$e")
        }

        //RIGHT-LEG
        try {
            val rightPoint =
                getRightMost(
                    arrayListOf(
                        rightHip!!,
                        rightKnee!!,
                        rightHeel!!,
                        rightAnkle!!,
                        rightFootIndex!!
                    )
                )
            val leftPoint =
                getLeftMost(
                    arrayListOf(
                        rightHip!!,
                        rightKnee!!,
                        rightHeel!!,
                        rightAnkle!!,
                        rightFootIndex!!
                    )
                )
            val topPoint =
                getTopMost(
                    arrayListOf(
                        rightHip!!,
                        rightKnee!!,
                        rightHeel!!,
                        rightAnkle!!,
                        rightFootIndex!!
                    )
                )
            val bottomPoint =
                getBottomMost(
                    arrayListOf(
                        rightHip!!,
                        rightKnee!!,
                        rightHeel!!,
                        rightAnkle!!,
                        rightFootIndex!!
                    )
                )
            val vertpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val hortPadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val height: Int = if ((Math.max(
                    (topPoint.position3D.y - vertpadding).toInt(),
                    0
                ) + ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt()) > originalCameraImage!!.height
            ) {
                originalCameraImage!!.height - Math.max(
                    (topPoint.position3D.y - vertpadding).toInt(),
                    0
                )
            } else {
                ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt()
            }

            val crop2 = cropImage(
                originalCameraImage,
                Math.max((rightPoint.position3D.x - hortPadding).toInt(), 0),
                Math.max((topPoint.position3D.y - vertpadding).toInt(), 0),
                ((leftPoint.position3D.x + hortPadding) - (rightPoint.position3D.x - hortPadding)).toInt(),
                height,
            )
            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean") {
                updateList(classification,"Right-Leg")
                var hpad = calcDistance(rightPoint, leftPoint)*0.25f
                var vpad = calcDistance(topPoint,bottomPoint)*0.25f
                //uploadFile(crop2, "right_hand")
                val left = translateX(rightPoint.position3D.x) - hpad
                val top = translateY(topPoint.position3D.y) - vpad
                val right = translateX(leftPoint.position3D.x) + hpad
                val bottom = translateY(bottomPoint.position3D.y) + vpad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)
                val drawableText = classification + " : " + String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }
            //uploadFile(crop2, "right_leg")
        } catch (e: Exception) {
            Log.i("uploadCheck", "right_leg$e")
        }

        //LEFT-LEG
        try {
            val rightPoint =
                getRightMost(
                    arrayListOf(
                        leftHip!!,
                        leftKnee!!,
                        leftHeel!!,
                        leftAnkle!!,
                        leftFootIndex!!
                    )
                )
            val leftPoint =
                getLeftMost(
                    arrayListOf(
                        leftHip!!,
                        leftKnee!!,
                        leftHeel!!,
                        leftAnkle!!,
                        leftFootIndex!!
                    )
                )
            val topPoint =
                getTopMost(
                    arrayListOf(
                        leftHip!!,
                        leftKnee!!,
                        leftHeel!!,
                        leftAnkle!!,
                        leftFootIndex!!
                    )
                )
            val bottomPoint =
                getBottomMost(
                    arrayListOf(
                        leftHip!!,
                        leftKnee!!,
                        leftHeel!!,
                        leftAnkle!!,
                        leftFootIndex!!
                    )
                )
            val vertpadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f
            val hortPadding = Math.max(
                (leftPoint.position3D.x - rightPoint.position3D.x),
                (bottomPoint.position3D.y - topPoint.position3D.y)
            ) * 0.15f

            val height: Int = if ((Math.max(
                    (topPoint.position3D.y - vertpadding).toInt(),
                    0
                ) + ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt()) > originalCameraImage!!.height
            ) {
                originalCameraImage.height - Math.max(
                    (topPoint.position3D.y - vertpadding).toInt(),
                    0
                )
            } else {
                ((bottomPoint.position3D.y + vertpadding) - (topPoint.position3D.y - vertpadding)).toInt()
            }

            val crop2 = cropImage(
                originalCameraImage,
                Math.max((rightPoint.position3D.x - hortPadding).toInt(), 0),
                Math.max((topPoint.position3D.y - vertpadding).toInt(), 0),
                ((leftPoint.position3D.x + hortPadding) - (rightPoint.position3D.x - hortPadding)).toInt(),
                height,
            )
            val outputArr = outputGenerator(crop2)
            val classification = outputArr[0] as String
            val confidence = outputArr[1] as Float

//            Log.i("modeloutput",classification)
//            Log.i("modeloutput",confidence.toString())
            if(classification != "clean") {
                updateList(classification,"Left-Leg")
                var hpad = calcDistance(rightPoint, leftPoint)*0.25f
                var vpad = calcDistance(topPoint,bottomPoint)*0.25f
                //uploadFile(crop2, "right_hand")
                val left = translateX(rightPoint.position3D.x) - hpad
                val top = translateY(topPoint.position3D.y) - vpad
                val right = translateX(leftPoint.position3D.x) + hpad
                val bottom = translateY(bottomPoint.position3D.y) + vpad
                val bounds = RectF(left, top, right, bottom)
                drawRectangle(canvas, bounds)
                val drawableText = classification + " : " + String.format("%.2f", confidence)
                var bound = Rect()
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bound)
                val textWidth = bound.width()
                val textHeight = bound.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )
                canvas.drawText(drawableText, left, top + bound.height(), textPaint)
            }
            //uploadFile(crop2, "left_leg")
        } catch (e: Exception) {
            Log.i("uploadCheck", "left_leg$e")
        }

        /*// Face
        drawLine(canvas, nose, lefyEyeInner, whitePaint)
        drawLine(canvas, lefyEyeInner, lefyEye, whitePaint)
        drawLine(canvas, lefyEye, leftEyeOuter, whitePaint)
        drawLine(canvas, leftEyeOuter, leftEar, whitePaint)
        drawLine(canvas, nose, rightEyeInner, whitePaint)
        drawLine(canvas, rightEyeInner, rightEye, whitePaint)
        drawLine(canvas, rightEye, rightEyeOuter, whitePaint)
        drawLine(canvas, rightEyeOuter, rightEar, whitePaint)
        drawLine(canvas, leftMouth, rightMouth, whitePaint)

        drawLine(canvas, leftShoulder, rightShoulder, whitePaint)
        drawLine(canvas, leftHip, rightHip, whitePaint)

        // Left body
        drawLine(canvas, leftShoulder, leftElbow, leftPaint)
        drawLine(canvas, leftElbow, leftWrist, leftPaint)
        drawLine(canvas, leftShoulder, leftHip, leftPaint)
        drawLine(canvas, leftHip, leftKnee, leftPaint)
        drawLine(canvas, leftKnee, leftAnkle, leftPaint)
        drawLine(canvas, leftWrist, leftThumb, leftPaint)
        drawLine(canvas, leftWrist, leftPinky, leftPaint)
        drawLine(canvas, leftWrist, leftIndex, leftPaint)
        drawLine(canvas, leftIndex, leftPinky, leftPaint)
        drawLine(canvas, leftAnkle, leftHeel, leftPaint)
        drawLine(canvas, leftHeel, leftFootIndex, leftPaint)

        // Right body
        drawLine(canvas, rightShoulder, rightElbow, rightPaint)
        drawLine(canvas, rightElbow, rightWrist, rightPaint)
        drawLine(canvas, rightShoulder, rightHip, rightPaint)
        drawLine(canvas, rightHip, rightKnee, rightPaint)
        drawLine(canvas, rightKnee, rightAnkle, rightPaint)
        drawLine(canvas, rightWrist, rightThumb, rightPaint)
        drawLine(canvas, rightWrist, rightPinky, rightPaint)
        drawLine(canvas, rightWrist, rightIndex, rightPaint)
        drawLine(canvas, rightIndex, rightPinky, rightPaint)
        drawLine(canvas, rightAnkle, rightHeel, rightPaint)
        drawLine(canvas, rightHeel, rightFootIndex, rightPaint)*/

        // Draw inFrameLikelihood for all points
        if (showInFrameLikelihood) {
            for (landmark in landmarks) {
                canvas.drawText(
                    String.format(Locale.US, "%.2f", landmark.inFrameLikelihood),
                    translateX(landmark.position.x),
                    translateY(landmark.position.y),
                    whitePaint
                )
            }
        }
    }

    private fun updateList(woundClass:String, bodyPart:String)
    {
        EventBus.getDefault().post(DataEvent(woundClass,bodyPart))
    }

    private fun outputGenerator(bitmap: Bitmap): ArrayList<Any> {
        val model = Model.newInstance(applicationContext)

        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR)).build()

        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        tensorImage =imageProcessor.process(tensorImage)

        // val imageSize: Int = 224
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
//        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//
//        val intValues = IntArray(imageSize * imageSize)
//        bitmap.getPixels(intValues, 0, bitmap.width*12, 0, 0, bitmap.width, bitmap.height)
//        var pixel = 0
//        for (i in 0 until imageSize) {
//            for (j in 0 until imageSize) {
//                val value = intValues[pixel++]
//                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
//                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
//                byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
//            }
//        }

        inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        //val outputFeature0 = outputs.getOutputFeature0AsTensorBuffer
        //val outputValues: FloatArray = outputFeature0.floatArray
        // continue with the rest of the code
        val confidences: FloatArray = outputFeature0.floatArray

        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        val classes = arrayOf("abrasions","cut","bruise","burn","laceration","stab","clean")
        // tvOutput.text = classes[maxPos]  Actual result
        /*var s = ""
        for (i in classes.indices) {
            s += "${classes[i]}: ${String.format("%.1f%%", confidences[i] * 100)}\n"*/
        //}

// Releases model resources if no longer used.
        model.close()

        return arrayListOf(classes[maxPos],(maxConfidence/255.0).toFloat())

    }

    private fun cropImage(originalCameraImage: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val w= if(x+width>originalCameraImage.width)
            originalCameraImage.width-x
        else
            width
        val h = if(y+height>originalCameraImage.height)
            originalCameraImage.height-y
        else
            height
        return Bitmap.createBitmap(
            originalCameraImage,
            x,
            y,
            w,
            h
        )
    }


    private fun getRightMost(poseList:ArrayList<PoseLandmark>): PoseLandmark {
        var ret = poseList[0]
        for(landmark in poseList)
        {
            if(landmark.position3D.x < ret.position3D.x)
                ret = landmark
        }
        return ret
    }
    private fun getLeftMost(poseList:ArrayList<PoseLandmark>): PoseLandmark {
        var ret = poseList[0]
        for(landmark in poseList)
        {
            if(landmark.position3D.x > ret.position3D.x)
                ret = landmark
        }
        return ret
    }
    private fun getTopMost(poseList:ArrayList<PoseLandmark>): PoseLandmark {
        var ret = poseList[0]
        for(landmark in poseList)
        {
            if(landmark.position3D.y < ret.position3D.y)
                ret = landmark
        }
        return ret
    }
    private fun getBottomMost(poseList:ArrayList<PoseLandmark>): PoseLandmark {
        var ret = poseList[0]
        for(landmark in poseList)
        {
            if(landmark.position3D.y > ret.position3D.y)
                ret = landmark
        }
        return ret
    }


    private fun calcDistance(point1: PoseLandmark, point2: PoseLandmark): Float {
        val x1 = translateX(point1.position3D.x)
        val x2 = translateX(point2.position3D.x)
        val y1 = translateY(point1.position3D.y)
        val y2 = translateY(point2.position3D.y)
        val z1 = point1.position3D.z
        val z2 = point2.position3D.z
        return sqrt(
            (x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0) + (z1 - z2).toDouble()
                .pow(2.0)
        ).toFloat()
    }


    internal fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint) {
        val point = landmark.position3D
        updatePaintColorByZValue(
            paint,
            canvas,
            visualizeZ,
            rescaleZForVisualization,
            point.z,
            zMin,
            zMax
        )
        canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
    }

    internal fun drawCircle(canvas: Canvas, landmark: PoseLandmark, radius: Float) {
        val point = landmark.position3D

        val circlePaint = Paint()
        circlePaint.strokeWidth = STROKE_WIDTH
        circlePaint.style = Paint.Style.STROKE
        updatePaintColorByZValue(
            circlePaint,
            canvas,
            visualizeZ,
            rescaleZForVisualization,
            point.z,
            zMin,
            zMax
        )
        canvas.drawCircle(
            translateX(point.x),
            translateY(point.y),
            radius + CIRCLE_PADDING,
            circlePaint
        )

    }

    internal fun drawRectangle(canvas: Canvas, bounds: RectF) {


        val rectPaint = Paint()
        rectPaint.strokeWidth = STROKE_WIDTH
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.CYAN
        canvas.drawRect(bounds, rectPaint)
    }

    internal fun drawLine(
        canvas: Canvas,
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?,
        paint: Paint
    ) {
        val start = startLandmark!!.position3D
        val end = endLandmark!!.position3D

        // Gets average z for the current body line
        val avgZInImagePixel = (start.z + end.z) / 2
        updatePaintColorByZValue(
            paint,
            canvas,
            visualizeZ,
            rescaleZForVisualization,
            avgZInImagePixel,
            zMin,
            zMax
        )

        canvas.drawLine(
            translateX(start.x),
            translateY(start.y),
            translateX(end.x),
            translateY(end.y),
            paint
        )
    }

    fun saveImageOnExternalData(filePath: String?, fileData: ByteArray?): Boolean {
        var isFileSaved = false
        try {
            val f = File(filePath)
            if (f.exists()) f.delete()
            f.createNewFile()
            val fos = FileOutputStream(f)
            fos.write(fileData)
            fos.flush()
            fos.close()
            isFileSaved = true
            // File Saved
        } catch (e: FileNotFoundException) {
            println("FileNotFoundException")
            e.printStackTrace()
        } catch (e: IOException) {
            println("IOException")
            e.printStackTrace()
        }
        return isFileSaved
        // File Not Saved
    }

    companion object {

        private val DOT_RADIUS = 8.0f
        private val CIRCLE_PADDING = 15.0f
        private val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private val STROKE_WIDTH = 10.0f
        private val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}
