@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package mvdm.shared.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainDrawable0 {
  public val bevrydingskamp: DrawableResource by 
      lazy { init_bevrydingskamp() }

  public val forging_background: DrawableResource by 
      lazy { init_forging_background() }

  public val manne1: DrawableResource by 
      lazy { init_manne1() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("bevrydingskamp", CommonMainDrawable0.bevrydingskamp)
  map.put("forging_background", CommonMainDrawable0.forging_background)
  map.put("manne1", CommonMainDrawable0.manne1)
}

internal val Res.drawable.bevrydingskamp: DrawableResource
  get() = CommonMainDrawable0.bevrydingskamp

private fun init_bevrydingskamp(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:bevrydingskamp",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/mvdm.shared.generated.resources/drawable/bevrydingskamp.jpeg", -1, -1),
    )
)

internal val Res.drawable.forging_background: DrawableResource
  get() = CommonMainDrawable0.forging_background

private fun init_forging_background(): DrawableResource =
    org.jetbrains.compose.resources.DrawableResource(
  "drawable:forging_background",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/mvdm.shared.generated.resources/drawable/forging_background.jpg", -1, -1),
    )
)

internal val Res.drawable.manne1: DrawableResource
  get() = CommonMainDrawable0.manne1

private fun init_manne1(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:manne1",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/mvdm.shared.generated.resources/drawable/manne1.png", -1, -1),
    )
)
