package ir.rahkarpouya.rpandroidlib

//RPA.replaceFont(this, "BTrafcBd.ttf")
//RPA.userId = 0

private fun createCityTable() {
//    CityController(this).createCityTable(
//        object : CityController.CreateCityTable {
//            override fun create() {
//            }
//
//            override fun complete() {
//            }
//        }
//    )
}

private fun setImageGalleryOrCamera() {
//    CropImage.activity()
//        .setActivityTitle("انتخاب عکس")
//        .start(this) if is fragment .start(this,fragment)

//-------- in code put onActivityResult ->

//    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//        val result = CropImage.getActivityResult(data)
//        if (resultCode == Activity.RESULT_OK) {
//            val resultUri: Uri = result!!.uri!!
//            imageTest.setImageURI(resultUri)
//        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//            val error = result!!.error
//            Log.i("CropImage_Error",error!!.message!!)
//        }
//    }
}


private fun setBottomNavigation() {
//    val viewPager: BottomNavigationViewPager = findViewById(R.id.viewPager)
//    val bottomNavigation: BottomNavigation = findViewById(R.id.bottomNavigation)

//    val fragment = ArrayList<Fragment>()
//        fragment.add(0, UserFragment())
//        fragment.add(1, CategoryFragment())
//        fragment.add(2, SpecialFragment())
//        fragment.add(3, HomeFragment())

//    bottomNavigation.addItem(
//        BottomNavigationItem(
//            "R.string.title_user",
//            R.drawable.ic_flip,
//            android.R.color.white
//        )
//    )
//    bottomNavigation.addItem(
//        BottomNavigationItem(
//            "R.string.title_category",
//            R.drawable.ic_flip,
//            android.R.color.white
//        )
//    )
//    bottomNavigation.addItem(
//        BottomNavigationItem(
//            "R.string.title_special",
//            R.drawable.ic_flip,
//            android.R.color.white
//        )
//    )
//
//    bottomNavigation.addItem(
//        BottomNavigationItem(
//            "R.string.title_home",
//            R.drawable.ic_flip,
//            android.R.color.white
//        )
//    )
//
//    bottomNavigation.setDefaultBackgroundColor(
//        ContextCompat.getColor(
//            this,
//            R.color.colorPrimaryDark
//        )
//    )
//    bottomNavigation.setActiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
//    bottomNavigation.setInActiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
//    bottomNavigation.setTitleTextSize(36f, 36f)
//    bottomNavigation.setTitleState(TitleState.ALWAYS_SHOW)
//    bottomNavigation.isBehaviorTranslationEnabled()
//    bottomNavigation.setSelectedBackgroundVisible(true)
//    bottomNavigation.setNotification("5", 2)
//
//    viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragment)
//
//    bottomNavigation.setCurrentItem(3)
//    viewPager.currentItem = 3
//    viewPager.offscreenPageLimit = 4
//
//    bottomNavigation.setOnTabSelectedListener(object :
//        BottomNavigation.OnTabSelectedListener {
//        override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
//            viewPager.setCurrentItem(position, wasSelected)
//            return true
//        }
//    })
}


private fun aesCrypt() {
//    val message = "hello world"
//    try {
//        val encryptedMsg = AESCrypt.encrypt("password", message)
//        textTest.text = encryptedMsg
//    } catch (e: GeneralSecurityException) { //handle error
//    }
//
//    val encryptedMsg = "2B22cS3UC5s35WBihLBo8w=="
//    try {
//        val messageAfterDecrypt = AESCrypt.decrypt("password", encryptedMsg)
//        textTest.text = messageAfterDecrypt
//    } catch (e: GeneralSecurityException) { //handle error - could be due to incorrect password or tampered encryptedMsg
//    }
}


private fun showSliderBanner() {
//    RPSliderView<MainActivity.DataImage>()
//        .setActivity(this)
//        .setModels(models)
//        .setRecyclerView(recyclerView)
//        .setHaveCard(true)
//        .setColorActiveIndicatorSlider(R.color.colorPrimary)
//        .setTimeNextPageSlider(3000)
//        .setRadiusCard(16f)
//        .setHolderSlider(object : SliderAdapter.SetSliderHolder<MainActivity.DataImage> {
//            override fun setDataSlider(
//                data: MainActivity.DataImage,
//                holder: SliderAdapter<*>.SliderHolder
//            ) {
//                holder.itemSliderTitle.text = data.title
//                Glide.with(this).load(data.url)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            holder.itemSliderProgress.visibility = View.GONE
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            holder.itemSliderProgress.visibility = View.GONE
//                            holder.itemSliderTitle.text = data.title
//                            return false
//                        }
//                    }).into(holder.itemSliderImage)
//            }
//        }).create()
}

private fun sharedPreference() {
//    val sharedPreference = RPSharedPreference(this)
//    sharedPreference.saveData("data", "ali")
//    sharedPreference.saveData("boll", true)
//    val s = sharedPreference.getString("data")
//    val b = sharedPreference.getBoolean("boll")
//    textTest.text = s.plus(b)
}

private class Manifest {

    class UseCropImage{

        /**** manifest ****/
//        <activity
//              android:name=".imageCroper.CropImageActivity"
//              android:theme="@style/Base.Theme.AppCompat" />

//        <provider
//            android:name="androidx.core.content.FileProvider"
//            android:authorities="${applicationId}.provider"
//            android:exported="false"
//            android:grantUriPermissions="true">
//            <meta-data
//                android:name="android.support.FILE_PROVIDER_PATHS"
//                android:resource="@xml/provide_paths" />
//            </provider>
        /**** manifest ****/

        //res/xml/provide_paths
//        <paths xmlns:android="http://schemas.android.com/apk/res/android">
//            <external-path name="external_files" path="."/>
//        </paths>
    }

    class CrashConfig{

//        <activity
//            android:name=".crashlytics.DefaultErrorActivity"
//            android:process=":error_activity" />

//        <provider
//            android:name=".crashlytics.CrashInitProvider"
//            android:authorities="${applicationId}.crashInitProvider"
//            android:exported="false"
//            android:grantUriPermissions="true"
//            android:initOrder="101" />
    }

}
