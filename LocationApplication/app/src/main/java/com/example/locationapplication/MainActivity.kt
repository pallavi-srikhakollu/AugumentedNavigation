package com.example.locationapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                // Handler code here.
                val intent = Intent(this@MainActivity, ArActivity::class.java);
                startActivity(intent);
            }
        })
        //arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CustomArFragment
        //arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*fun setupAugmentedImagesDb(config: Config, session: Session): Boolean {
        val augmentedImageDatabase: AugmentedImageDatabase
        val bitmap = loadAugmentedImage() ?: return false
        augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("tiger", bitmap)
        config.setAugmentedImageDatabase(augmentedImageDatabase)
        return true
    }

    private fun loadAugmentedImage(): Bitmap? {
        try {
            assets.open("earth.jpg").use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e("ImageLoad", "IO Exception", e)
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
                if (augmentedImage.name == "tiger" && shouldAddModel) {
                    placeObject(
                        arFragment,
                        augmentedImage.createAnchor(augmentedImage.centerPose),
                        Uri.parse("NOVELO_EARTH.sfb")
                    )
                    shouldAddModel = false
                }
            }
        }
    }*/
}
