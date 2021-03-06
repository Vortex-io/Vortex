package io.vortex.android.utils.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import io.vortex.android.models.ui.VortexAnimationSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created By : Yazan Tarifi
 * Date : 12/26/2019
 * Time : 9:31 PM
 */

object VortexGifController {

    private var listener: VortexGifListener? = null

    suspend fun startAnimationWithGlide(
        settings: VortexAnimationSettings,
        context: Context,
        image: ImageView?
    ) {
        withContext(Dispatchers.Main) {
            image?.let {
                Glide.with(context).asGif().load(settings.image)
                    .override(
                        convertDpToPixel(
                            settings.dp,
                            context
                        ).toInt()
                    )
                    .addListener(object : RequestListener<GifDrawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<GifDrawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: GifDrawable?,
                            model: Any?,
                            target: Target<GifDrawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.setLoopCount(settings.loopCountAnimation)
                            resource?.registerAnimationCallback(object :
                                Animatable2Compat.AnimationCallback() {
                                override fun onAnimationEnd(drawable: Drawable?) {
                                    GlobalScope.launch {
                                        listener?.let {
                                            it.onAnimationFinished()
                                        }
                                    }
                                }
                            })
                            return false
                        }

                    }).into(it)
            }
        }
    }

    suspend fun startAnimationWithFresco(
        settings: VortexAnimationSettings,
        image: SimpleDraweeView,
        isAutoPlay: Boolean
    ) {
        withContext(Dispatchers.Main) {
            val imageRequest = ImageRequestBuilder.newBuilderWithResourceId(settings.image).build()
            val controller: DraweeController = Fresco.newDraweeControllerBuilder()
                .setUri(imageRequest.sourceUri)
                .setAutoPlayAnimations(isAutoPlay)
                .build()
            image.controller = controller
        }
    }

    fun attachListener(listener: VortexGifListener) {
        this.listener = listener
    }

    fun destroyGifController() {
        this.listener = null
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}