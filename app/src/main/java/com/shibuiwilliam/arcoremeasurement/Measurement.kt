package com.shibuiwilliam.arcoremeasurement

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class Measurement : AppCompatActivity(), Scene.OnUpdateListener {
    private val MIN_OPENGL_VERSION = 3.0
    private val TAG: String = Measurement::class.java.getSimpleName()

    private var arFragment: ArFragment? = null

    private var distanceCardViewRenderable: ViewRenderable? = null
    private var modelRenderable: ModelRenderable? = null


    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()

    private lateinit var initCM: String

    private lateinit var clearButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // does it support or not
        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(applicationContext, "Device not supported", Toast.LENGTH_LONG)
                .show()
        }

        // Set the activity content to an explicit view. This view is placed directly into the activity's view hierarchy
        // hold ARCustomFragment, connect the ARFragment with its view
        setContentView(R.layout.activity_measurement)


        // A Fragment represents a reusable portion of your app's UI
        // FragmentManager is the class responsible for performing actions on your app's fragments,
        // such as adding, removing, or replacing them, and adding them to the back stack.
        // sceneform_fragment: fragment in activity_measurement
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment?

        initCM = resources.getString(R.string.initCM)

        toastMode()
        // Loading your objects/renderables
        initRenderable()

        // Creating a clear button to clearAllAnchors()
        clearButton()

        // 0: distance_from_camera (default)
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (distanceCardViewRenderable == null) return@setOnTapArPlaneListener
            // Creating Anchor.
            clearAllAnchors()
            placeAnchor(hitResult, distanceCardViewRenderable!!,modelRenderable!!)
        }
    }

    // Loading your objects/renderables
    private fun initRenderable() {
        // add my model
        ModelRenderable.builder()
            .setSource(this, R.raw.wolves)
            .build()
            .thenAccept { renderable: ModelRenderable ->
                modelRenderable = renderable
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, R.layout.distance_text_layout)
            .build()
            .thenAccept {
                distanceCardViewRenderable = it
                distanceCardViewRenderable!!.isShadowCaster = false
                distanceCardViewRenderable!!.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }


    private fun placeAnchor(hitResult: HitResult, renderable: Renderable, modelRenderable: Renderable) {
        val anchor = hitResult.createAnchor()
        placedAnchors.add(anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        placedAnchorNodes.add(anchorNode)

        val node = TransformableNode(arFragment!!.transformationSystem)
            .apply {
                this.rotationController.isEnabled = false
                this.scaleController.isEnabled = false
                this.translationController.isEnabled = true
                this.renderable = renderable
                setParent(anchorNode)
            }

        val wolf = Node()
            .apply {
                setParent(node)
                this.worldPosition = Vector3(
                    node.worldPosition.x,
                    node.worldPosition.y + 0.1f,
                    node.worldPosition.z
                )
                this.renderable = modelRenderable
            }

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(anchorNode)
        node.select()
    }


    // Used to clear all anchors, Used in onCreate()
    private fun clearButton() {
        clearButton = findViewById(R.id.clearButton)
        clearButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                clearAllAnchors()
            }
        })
    }

    // Clear all the anchors, Used in many functions and it is the main functionality of clearButton
    private fun clearAllAnchors() {
        placedAnchors.clear()
        for (anchorNode in placedAnchorNodes) {
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor!!.detach()
            anchorNode.setParent(null)
        }
        placedAnchorNodes.clear()
    }


    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        measureDistanceFromCamera()
    }


    private fun measureDistanceFromCamera() {
        val frame = arFragment!!.arSceneView.arFrame
        if (placedAnchorNodes.size >= 1) {
            val distanceMeter = calculateDistance(
                placedAnchorNodes[0].worldPosition,
                frame!!.camera.pose
            )
            // set Card Text
            setDistanceCardText(distanceMeter)
        }
    }

    private fun setDistanceCardText(distanceMeter: Float) {
        val distanceTextCM = makeDistanceTextWithCM(distanceMeter)
        val textView = (distanceCardViewRenderable!!.view as LinearLayout)
            .findViewById<TextView>(R.id.distanceCard)
        textView.text = distanceTextCM
        Log.d(TAG, "distance: $distanceTextCM")
    }

    private fun makeDistanceTextWithCM(distanceMeter: Float): String {
        val distanceCM = changeUnit(distanceMeter, "cm")
        val distanceCMFloor = "%.2f".format(distanceCM)
        return "$distanceCMFloor cm"
    }

    // with Float
    private fun calculateDistance(x: Float, y: Float, z: Float): Float {
        return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
    }

    // with Vector3 and Pose
    private fun calculateDistance(objectPose0: Vector3, objectPose1: Pose): Float {
        return calculateDistance(
            objectPose0.x - objectPose1.tx(),
            objectPose0.y - objectPose1.ty(),
            objectPose0.z - objectPose1.tz()
        )
    }

    private fun changeUnit(distanceMeter: Float, unit: String): Float {
        return when (unit) {
            "cm" -> distanceMeter * 100
            "mm" -> distanceMeter * 1000
            else -> distanceMeter
        }
    }

    private fun toastMode() {
        Toast.makeText(
            this@Measurement, "Find plane and tap somewhere",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString =
            (Objects.requireNonNull(
                activity
                    .getSystemService(Context.ACTIVITY_SERVICE)
            ) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES ${MIN_OPENGL_VERSION} later")
            Toast.makeText(
                activity,
                "Sceneform requires OpenGL ES ${MIN_OPENGL_VERSION} or later",
                Toast.LENGTH_LONG
            )
                .show()
            activity.finish()
            return false
        }
        return true
    }
}