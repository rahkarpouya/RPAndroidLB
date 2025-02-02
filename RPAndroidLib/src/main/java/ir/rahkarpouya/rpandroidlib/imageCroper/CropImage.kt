package ir.rahkarpouya.rpandroidlib.imageCroper

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap.CompressFormat
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ir.rahkarpouya.rpandroidlib.R
import ir.rahkarpouya.rpandroidlib.imageCroper.CropImageView.*
import java.io.File
import java.util.*

object CropImage {
    // region: Fields and Consts
    /** The key used to pass crop image source URI to [CropImageActivity].  */
    const val CROP_IMAGE_EXTRA_SOURCE = "CROP_IMAGE_EXTRA_SOURCE"

    /** The key used to pass crop image options to [CropImageActivity].  */
    const val CROP_IMAGE_EXTRA_OPTIONS = "CROP_IMAGE_EXTRA_OPTIONS"

    /** The key used to pass crop image bundle data to [CropImageActivity].  */
    const val CROP_IMAGE_EXTRA_BUNDLE = "CROP_IMAGE_EXTRA_BUNDLE"

    /** The key used to pass crop image result data back from [CropImageActivity].  */
    const val CROP_IMAGE_EXTRA_RESULT = "CROP_IMAGE_EXTRA_RESULT"

    /**
     * The request code used to start pick image activity to be used on result to identify the this
     * specific request.
     */
    const val PICK_IMAGE_CHOOSER_REQUEST_CODE = 200

    /** The request code used to request permission to pick image from external storage.  */
    const val PICK_IMAGE_PERMISSIONS_REQUEST_CODE = 201

    /** The request code used to request permission to capture image from camera.  */
    const val CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE = 2011

    /**
     * The request code used to start [CropImageActivity] to be used on result to identify the
     * this specific request.
     */
    const val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203

    /** The result code used to return error from [CropImageActivity].  */
    const val CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE = 204


    fun startPickImageActivity(activity: Activity) {
        activity.startActivityForResult(
            getPickImageChooserIntent(activity), PICK_IMAGE_CHOOSER_REQUEST_CODE
        )
    }

    fun startPickImageActivity(
        context: Context,
        fragment: Fragment
    ) {
        fragment.startActivityForResult(
            getPickImageChooserIntent(context), PICK_IMAGE_CHOOSER_REQUEST_CODE
        )
    }

    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param title the title to use for the chooser UI
     * @param includeDocuments if to include KitKat documents activity containing all sources
     * @param includeCamera if to include camera intents
     */
    private fun getPickImageChooserIntent(
        context: Context,
        title: CharSequence? = context.getString(R.string.pick_image_intent_chooser_title),
        includeDocuments: Boolean = false,
        includeCamera: Boolean = true
    ): Intent {
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = context.packageManager
        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager))
        }
        var galleryIntents =
            getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
        if (galleryIntents.isEmpty()) { // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
        }
        allIntents.addAll(galleryIntents)
        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }
        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)
        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
        )
        return chooserIntent
    }

    /** Get all Camera intents for capturing image using device camera apps.  */
    private fun getCameraIntents(
        context: Context, packageManager: PackageManager
    ): List<Intent> {
        val allIntents: MutableList<Intent> = ArrayList()
        // Determine Uri of camera image to  save.
        val outputFileUri = getCaptureImageOutputUri(context)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam =
            packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }
        return allIntents
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     */
    private fun getGalleryIntents(
        packageManager: PackageManager, action: String, includeDocuments: Boolean
    ): List<Intent> {
        val intents: MutableList<Intent> = ArrayList()
        val galleryIntent =
            if (action === Intent.ACTION_GET_CONTENT) Intent(action) else Intent(
                action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        galleryIntent.type = "image/*"
        val listGallery =
            packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }
        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        return intents
    }

    fun isExplicitCameraPermissionRequired(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasPermissionInManifest(context) &&
                (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                )
    }

    private fun hasPermissionInManifest(
        context: Context, permissionName: String = "android.permission.CAMERA"
    ): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo = context.packageManager
                .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermission = packageInfo.requestedPermissions
            if (declaredPermission != null && declaredPermission.isNotEmpty()) {
                for (p in declaredPermission) {
                    if (p.equals(permissionName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    /**
     * Get URI to image received from capture by camera.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    private fun getCaptureImageOutputUri(context: Context): Uri? {
        context.externalCacheDir?.apply {
            return Uri.fromFile(File(this.path, "pickImageResult.jpeg"))
        }
        return null
    }

    /**
     * Get the URI of the selected image from [.getPickImageChooserIntent].<br></br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param data the returned data of the activity result
     */
    fun getPickImageResultUri(context: Context, data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                FileUtil.getRealPathFromURI(context, data.data!!)?.apply {
                    val file = File(this)
                    if (file.exists()) {
                        try {
                            data.data = FileProvider.getUriForFile(
                                context, context.applicationContext.packageName + ".provider", file
                            )
                        } catch (e: Exception) {
                            Log.i("Error_CropImage", e.message!!)
                        }
                    }
                }

            }
        }
        return if (isCamera || data!!.data == null) getCaptureImageOutputUri(context) else data.data
    }

    /**
     * Check if the given picked image URI requires READ_EXTERNAL_STORAGE permissions.<br></br>
     * Only relevant for API version 23 and above and not required for all URI's depends on the
     * implementation of the app that was used for picking the image. So we just test if we can open
     * the stream or do we get an exception when we try, Android is awesome.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param uri the result URI of image pick.
     * @return true - required permission are not granted, false - either no need for permissions or
     * they are granted
     */
    fun isReadExternalStoragePermissionsRequired(
        context: Context, uri: Uri
    ): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (context.checkSelfPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
                != PackageManager.PERMISSION_GRANTED) && isUriRequiresPermissions(context, uri)
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br></br>
     * Only relevant for API version 23 and above.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param uri the result URI of image pick.
     */
    private fun isUriRequiresPermissions(
        context: Context,
        uri: Uri
    ): Boolean {
        return try {
            val resolver = context.contentResolver
            val stream = resolver.openInputStream(uri)
            stream?.close()
            false
        } catch (e: Exception) {
            true
        }
    }

    fun activity(): ActivityBuilder {
        return ActivityBuilder(null)
    }

    fun activity(uri: Uri?): ActivityBuilder {
        return ActivityBuilder(uri)
    }

    fun getActivityResult(@Nullable data: Intent?): ActivityResult? {
        return if (data != null)
            data.getParcelableExtra<Parcelable>(CROP_IMAGE_EXTRA_RESULT) as ActivityResult
        else null
    }
    // region: Inner class: ActivityBuilder
    /** Builder used for creating Image Crop Activity by user request.  */
    class ActivityBuilder(
        /** The image to crop source Android uri.  */
        private val mSource: Uri?
    ) {
        /** Options for image crop UX  */
        private val mOptions: CropImageOptions = CropImageOptions()

        /** Get [CropImageActivity] intent to start the activity.  */
        private fun getIntent(context: Context): Intent {
            return getIntent(context, CropImageActivity::class.java)
        }

        /** Get [CropImageActivity] intent to start the activity.  */
        private fun getIntent(context: Context, cls: Class<*>?): Intent {
            mOptions.validate()
            val intent = Intent()
            intent.setClass(context, cls!!)
            val bundle = Bundle()
            bundle.putParcelable(CROP_IMAGE_EXTRA_SOURCE, mSource)
            bundle.putParcelable(CROP_IMAGE_EXTRA_OPTIONS, mOptions)
            intent.putExtra(CROP_IMAGE_EXTRA_BUNDLE, bundle)
            return intent
        }

        /**
         * Start [CropImageActivity].
         *
         * @param activity activity to receive result
         */
        fun start(activity: Activity) {
            mOptions.validate()
            activity.startActivityForResult(
                getIntent(activity),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * Start [CropImageActivity].
         *
         * @param activity activity to receive result
         */
        fun start(activity: Activity, cls: Class<*>?) {
            mOptions.validate()
            activity.startActivityForResult(
                getIntent(activity, cls),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        fun start(
            context: Context,
            fragment: Fragment
        ) {
            fragment.startActivityForResult(
                getIntent(context),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        fun start(
            context: Context,
            fragment: Fragment,
            cls: Class<*>?
        ) {
            fragment.startActivityForResult(
                getIntent(context, cls),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * The shape of the cropping window.<br></br>
         * To set square/circle crop shape set aspect ratio to 1:1.<br></br>
         * *Default: RECTANGLE*
         */
        fun setCropShape(cropShape: CropShape): ActivityBuilder {
            mOptions.cropShape = cropShape
            return this
        }

        /**
         * An edge of the crop window will snap to the corresponding edge of a specified bounding box
         * when the crop window edge is less than or equal to this distance (in pixels) away from the
         * bounding box edge (in pixels).<br></br>
         * *Default: 3dp*
         */
        fun setSnapRadius(snapRadius: Float): ActivityBuilder {
            mOptions.snapRadius = snapRadius
            return this
        }

        /**
         * The radius of the touchable area around the handle (in pixels).<br></br>
         * We are basing this value off of the recommended 48dp Rhythm.<br></br>
         * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm<br></br>
         * *Default: 48dp*
         */
        fun setTouchRadius(touchRadius: Float): ActivityBuilder {
            mOptions.touchRadius = touchRadius
            return this
        }

        /**
         * whether the guidelines should be on, off, or only showing when resizing.<br></br>
         * *Default: ON_TOUCH*
         */
        fun setGuidelines(guidelines: Guidelines): ActivityBuilder {
            mOptions.guidelines = guidelines
            return this
        }

        /**
         * The initial scale type of the image in the crop image view<br></br>
         * *Default: FIT_CENTER*
         */
        fun setScaleType(scaleType: ScaleType): ActivityBuilder {
            mOptions.scaleType = scaleType
            return this
        }

        /**
         * if to show crop overlay UI what contains the crop window UI surrounded by background over the
         * cropping image.<br></br>
         * *default: true, may disable for animation or frame transition.*
         */
        fun setShowCropOverlay(showCropOverlay: Boolean): ActivityBuilder {
            mOptions.showCropOverlay = showCropOverlay
            return this
        }

        /**
         * if auto-zoom functionality is enabled.<br></br>
         * default: true.
         */
        fun setAutoZoomEnabled(autoZoomEnabled: Boolean): ActivityBuilder {
            mOptions.autoZoomEnabled = autoZoomEnabled
            return this
        }

        /**
         * if multi touch functionality is enabled.<br></br>
         * default: true.
         */
        fun setMultiTouchEnabled(multiTouchEnabled: Boolean): ActivityBuilder {
            mOptions.multiTouchEnabled = multiTouchEnabled
            return this
        }

        /**
         * The max zoom allowed during cropping.<br></br>
         * *Default: 4*
         */
        fun setMaxZoom(maxZoom: Int): ActivityBuilder {
            mOptions.maxZoom = maxZoom
            return this
        }

        /**
         * The initial crop window padding from image borders in percentage of the cropping image
         * dimensions.<br></br>
         * *Default: 0.1*
         */
        fun setInitialCropWindowPaddingRatio(initialCropWindowPaddingRatio: Float): ActivityBuilder {
            mOptions.initialCropWindowPaddingRatio = initialCropWindowPaddingRatio
            return this
        }

        /**
         * whether the width to height aspect ratio should be maintained or free to change.<br></br>
         * *Default: false*
         */
        fun setFixAspectRatio(fixAspectRatio: Boolean): ActivityBuilder {
            mOptions.fixAspectRatio = fixAspectRatio
            return this
        }

        /**
         * the X,Y value of the aspect ratio.<br></br>
         * Also sets fixes aspect ratio to TRUE.<br></br>
         * *Default: 1/1*
         *
         * @param aspectRatioX the width
         * @param aspectRatioY the height
         */
        fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int): ActivityBuilder {
            mOptions.aspectRatioX = aspectRatioX
            mOptions.aspectRatioY = aspectRatioY
            mOptions.fixAspectRatio = true
            return this
        }

        /**
         * the thickness of the guidelines lines (in pixels).<br></br>
         * *Default: 3dp*
         */
        fun setBorderLineThickness(borderLineThickness: Float): ActivityBuilder {
            mOptions.borderLineThickness = borderLineThickness
            return this
        }

        /**
         * the color of the guidelines lines.<br></br>
         * *Default: Color.argb(170, 255, 255, 255)*
         */
        fun setBorderLineColor(borderLineColor: Int): ActivityBuilder {
            mOptions.borderLineColor = borderLineColor
            return this
        }

        /**
         * thickness of the corner line (in pixels).<br></br>
         * *Default: 2dp*
         */
        fun setBorderCornerThickness(borderCornerThickness: Float): ActivityBuilder {
            mOptions.borderCornerThickness = borderCornerThickness
            return this
        }

        /**
         * the offset of corner line from crop window border (in pixels).<br></br>
         * *Default: 5dp*
         */
        fun setBorderCornerOffset(borderCornerOffset: Float): ActivityBuilder {
            mOptions.borderCornerOffset = borderCornerOffset
            return this
        }

        /**
         * the length of the corner line away from the corner (in pixels).<br></br>
         * *Default: 14dp*
         */
        fun setBorderCornerLength(borderCornerLength: Float): ActivityBuilder {
            mOptions.borderCornerLength = borderCornerLength
            return this
        }

        /**
         * the color of the corner line.<br></br>
         * *Default: WHITE*
         */
        fun setBorderCornerColor(@ColorInt borderCornerColor: Int): ActivityBuilder {
            mOptions.borderCornerColor = borderCornerColor
            return this
        }

        /**
         * the thickness of the guidelines lines (in pixels).<br></br>
         * *Default: 1dp*
         */
        fun setGuidelinesThickness(guidelinesThickness: Float): ActivityBuilder {
            mOptions.guidelinesThickness = guidelinesThickness
            return this
        }

        /**
         * the color of the guidelines lines.<br></br>
         * *Default: Color.argb(170, 255, 255, 255)*
         */
        fun setGuidelinesColor(@ColorInt guidelinesColor: Int): ActivityBuilder {
            mOptions.guidelinesColor = guidelinesColor
            return this
        }

        /**
         * the color of the overlay background around the crop window cover the image parts not in the
         * crop window.<br></br>
         * *Default: Color.argb(119, 0, 0, 0)*
         */
        fun setBackgroundColor(@ColorInt backgroundColor: Int): ActivityBuilder {
            mOptions.backgroundColor = backgroundColor
            return this
        }

        /**
         * the min size the crop window is allowed to be (in pixels).<br></br>
         * *Default: 42dp, 42dp*
         */
        fun setMinCropWindowSize(
            minCropWindowWidth: Int,
            minCropWindowHeight: Int
        ): ActivityBuilder {
            mOptions.minCropWindowWidth = minCropWindowWidth
            mOptions.minCropWindowHeight = minCropWindowHeight
            return this
        }

        /**
         * the min size the resulting cropping image is allowed to be, affects the cropping window
         * limits (in pixels).<br></br>
         * *Default: 40px, 40px*
         */
        fun setMinCropResultSize(
            minCropResultWidth: Int,
            minCropResultHeight: Int
        ): ActivityBuilder {
            mOptions.minCropResultWidth = minCropResultWidth
            mOptions.minCropResultHeight = minCropResultHeight
            return this
        }

        /**
         * the max size the resulting cropping image is allowed to be, affects the cropping window
         * limits (in pixels).<br></br>
         * *Default: 99999, 99999*
         */
        fun setMaxCropResultSize(
            maxCropResultWidth: Int,
            maxCropResultHeight: Int
        ): ActivityBuilder {
            mOptions.maxCropResultWidth = maxCropResultWidth
            mOptions.maxCropResultHeight = maxCropResultHeight
            return this
        }

        /**
         * the title of the [CropImageActivity].<br></br>
         * *Default: ""*
         */
        fun setActivityTitle(activityTitle: CharSequence?): ActivityBuilder {
            mOptions.activityTitle = activityTitle
            return this
        }

        /**
         * the color to use for action bar items icons.<br></br>
         * *Default: NONE*
         */
        fun setActivityMenuIconColor(activityMenuIconColor: Int): ActivityBuilder {
            mOptions.activityMenuIconColor = activityMenuIconColor
            return this
        }

        /**
         * the Android Uri to save the cropped image to.<br></br>
         * *Default: NONE, will create a temp file*
         */
        fun setOutputUri(outputUri: Uri?): ActivityBuilder {
            mOptions.outputUri = outputUri
            return this
        }

        /**
         * the compression format to use when writting the image.<br></br>
         * *Default: JPEG*
         */
        fun setOutputCompressFormat(outputCompressFormat: CompressFormat?): ActivityBuilder {
            mOptions.outputCompressFormat = outputCompressFormat!!
            return this
        }

        /**
         * the quility (if applicable) to use when writting the image (0 - 100).<br></br>
         * *Default: 90*
         */
        fun setOutputCompressQuality(outputCompressQuality: Int): ActivityBuilder {
            mOptions.outputCompressQuality = outputCompressQuality
            return this
        }

        /**
         * the size to resize the cropped image to.<br></br>
         * *Default: 0, 0 - not set, will not resize*
         */
        fun setRequestedSize(
            reqWidth: Int,
            reqHeight: Int,
            options: RequestSizeOptions? = RequestSizeOptions.RESIZE_INSIDE
        ): ActivityBuilder {
            mOptions.outputRequestWidth = reqWidth
            mOptions.outputRequestHeight = reqHeight
            mOptions.outputRequestSizeOptions = options!!
            return this
        }

        /**
         * if the result of crop image activity should not save the cropped image bitmap.<br></br>
         * Used if you want to crop the image manually and need only the crop rectangle and rotation
         * data.<br></br>
         * *Default: false*
         */
        fun setNoOutputImage(noOutputImage: Boolean): ActivityBuilder {
            mOptions.noOutputImage = noOutputImage
            return this
        }

        /**
         * the initial rectangle to set on the cropping image after loading.<br></br>
         * *Default: NONE - will initialize using initial crop window padding ratio*
         */
        fun setInitialCropWindowRectangle(initialCropWindowRectangle: Rect?): ActivityBuilder {
            mOptions.initialCropWindowRectangle = initialCropWindowRectangle
            return this
        }

        /**
         * the initial rotation to set on the cropping image after loading (0-360 degrees clockwise).
         * <br></br>
         * *Default: NONE - will read image exif data*
         */
        fun setInitialRotation(initialRotation: Int): ActivityBuilder {
            mOptions.initialRotation = (initialRotation + 360) % 360
            return this
        }

        /**
         * if to allow rotation during cropping.<br></br>
         * *Default: true*
         */
        fun setAllowRotation(allowRotation: Boolean): ActivityBuilder {
            mOptions.allowRotation = allowRotation
            return this
        }

        /**
         * if to allow flipping during cropping.<br></br>
         * *Default: true*
         */
        fun setAllowFlipping(allowFlipping: Boolean): ActivityBuilder {
            mOptions.allowFlipping = allowFlipping
            return this
        }

        /**
         * if to allow counter-clockwise rotation during cropping.<br></br>
         * Note: if rotation is disabled this option has no effect.<br></br>
         * *Default: false*
         */
        fun setAllowCounterRotation(allowCounterRotation: Boolean): ActivityBuilder {
            mOptions.allowCounterRotation = allowCounterRotation
            return this
        }

        /**
         * The amount of degreees to rotate clockwise or counter-clockwise (0-360).<br></br>
         * *Default: 90*
         */
        fun setRotationDegrees(rotationDegrees: Int): ActivityBuilder {
            mOptions.rotationDegrees = (rotationDegrees + 360) % 360
            return this
        }

        /**
         * whether the image should be flipped horizontally.<br></br>
         * *Default: false*
         */
        fun setFlipHorizontally(flipHorizontally: Boolean): ActivityBuilder {
            mOptions.flipHorizontally = flipHorizontally
            return this
        }

        /**
         * whether the image should be flipped vertically.<br></br>
         * *Default: false*
         */
        fun setFlipVertically(flipVertically: Boolean): ActivityBuilder {
            mOptions.flipVertically = flipVertically
            return this
        }

        /**
         * optional, set crop menu crop button title.<br></br>
         * *Default: null, will use resource string: crop_image_menu_crop*
         */
        fun setCropMenuCropButtonTitle(title: CharSequence?): ActivityBuilder {
            mOptions.cropMenuCropButtonTitle = title
            return this
        }

        /**
         * Image resource id to use for crop icon instead of text.<br></br>
         * *Default: 0*
         */
        fun setCropMenuCropButtonIcon(@DrawableRes drawableResource: Int): ActivityBuilder {
            mOptions.cropMenuCropButtonIcon = drawableResource
            return this
        }

    }
    // endregion
// region: Inner class: ActivityResult
    /** Result data of Crop Image Activity.  */
    open class ActivityResult : CropResult, Parcelable {
        constructor(
            originalUri: Uri?,
            uri: Uri?,
            error: Exception?,
            cropPoints: FloatArray?,
            cropRect: Rect?,
            rotation: Int,
            wholeImageRect: Rect?,
            sampleSize: Int
        ) : super(
            originalUri,
            null,
            uri,
            error,
            cropPoints,
            cropRect,
            wholeImageRect,
            rotation,
            sampleSize
        )

        protected constructor(`in`: Parcel) : super(
            `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
            null,
            `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
            `in`.readSerializable() as Exception?,
            `in`.createFloatArray(),
            `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
            `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
            `in`.readInt(),
            `in`.readInt()
        )

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(originalUri, flags)
            dest.writeParcelable(uri, flags)
            dest.writeSerializable(error)
            dest.writeFloatArray(cropPoints)
            dest.writeParcelable(cropRect, flags)
            dest.writeParcelable(wholeImageRect, flags)
            dest.writeInt(rotation)
            dest.writeInt(sampleSize)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ActivityResult> {
            override fun createFromParcel(parcel: Parcel): ActivityResult {
                return ActivityResult(parcel)
            }

            override fun newArray(size: Int): Array<ActivityResult?> {
                return arrayOfNulls(size)
            }
        }
    } // endregion
}