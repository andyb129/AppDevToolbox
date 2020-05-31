package uk.co.barbuzz.devtiles.service

import android.graphics.drawable.Icon
import android.service.quicksettings.TileService
import uk.co.barbuzz.devtiles.util.AnimationScaler


/**
 * A {@link TileService} for toggling Window Animation Scale, Transition Animation Scale, and Animator Duration Scale.
 */
class ToggleAnimationService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        AnimationScaler.toggleAnimationScale(this)
        updateTile()
    }

    private fun updateTile() {
        val scale = AnimationScaler.getAnimationScale(contentResolver)
        val tile = qsTile
        tile.icon = Icon.createWithResource(applicationContext, AnimationScaler.getIcon(scale))
        tile.updateTile()
    }
}