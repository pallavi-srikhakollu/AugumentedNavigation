package com.example.locationapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log.e
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.IOException





class ArActivity : AppCompatActivity() {
    private lateinit var arFragment:  ArFragment
    private  var shouldAddModel = true;
    private  lateinit var arSceneView :ArSceneView
    var mSession: Session? = null
    private var sessionConfigured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
                //CustomArFragment
        //if (arFragment.arSceneView != null){
        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
        arSceneView= arFragment.getArSceneView();
        //}

    }


    fun setupAugmentedImagesDb(config: Config, session: Session): Boolean {
        val augmentedImageDatabase: AugmentedImageDatabase
        val bitmap = loadAugmentedImage() ?: return false
        print(bitmap)
        augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("book", bitmap)
        config.setAugmentedImageDatabase(augmentedImageDatabase)
        return true
    }

    private fun loadAugmentedImage(): Bitmap? {
        try {
            assets.open("book.jpeg").use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            e("ImageLoad", "IO Exception", e)
        }

        return null
    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor, uri: Uri) {
        ModelRenderable.builder()
            .setSource(arFragment.context, uri)
            .build()
            .thenAccept({ renderable -> addNodeToScene(arFragment, anchor, renderable) })
            .exceptionally({ throwable ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage(throwable.message)
                    .setTitle("Error!")
                val dialog = builder.create()
                dialog.show()
                null
            })
    }

    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun onUpdateFrame(frameTime: FrameTime) {

        val frame = arFragment.arSceneView.arFrame
        val augmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
        for (augmentedImage in augmentedImages) {
            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                if (augmentedImage.name == "book" && shouldAddModel) {

                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Detected")
                        .setTitle("Error!")
                    val dialog = builder.create()
                    dialog.show()
                    print("Detected")
                    placeObject(
                        arFragment,
                        augmentedImage.createAnchor(augmentedImage.centerPose),
                        Uri.parse("NOVELO_EARTH.sfb")
                    )
                    shouldAddModel = true
                }
            }
        }
    }

    private fun configureSession() {
        val config = Config(mSession)
        if  (mSession != null) {
            if (!setupAugmentedImagesDb(config,mSession as Session)) {
                Toast.makeText(this, "Unable to setup augmented", Toast.LENGTH_SHORT).show()
            }
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            (mSession as Session).configure(config)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mSession != null) {

            arSceneView.pause()
            (mSession as Session).pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mSession == null) {
            var message: String? = null
            var exception: Exception? = null
            try {
                mSession = Session(this)
            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update android"
                exception = e
            } catch (e: Exception) {
                message = "AR is not supported"
                exception = e
            }

            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                e("hello", "Exception creating session", exception)
                return
            }
            sessionConfigured = true

        }
        if (sessionConfigured) {
            configureSession()
            sessionConfigured = false

            arSceneView.setupSession(mSession)
        }


    }
}
