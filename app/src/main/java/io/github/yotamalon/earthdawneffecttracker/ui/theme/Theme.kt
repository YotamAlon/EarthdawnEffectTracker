package io.github.yotamalon.earthdawneffecttracker.ui.theme

data class EarthdawnExtendedColors(
    val karma: Int,
    val horror: Int,
    val recovery: Int,
    val wound: Int,
    val strain: Int
)

object EarthdawnTheme {
    val lightColors = EarthdawnExtendedColors(
        karma = ArcaneTealLight,
        horror = AstralVioletLight,
        recovery = LivingEarthGreenLight,
        wound = WoundRedLight,
        strain = OchreAmberLight
    )

    val darkColors = EarthdawnExtendedColors(
        karma = ArcaneTealDark,
        horror = AstralVioletDark,
        recovery = LivingEarthGreenDark,
        wound = WoundRedDark,
        strain = OchreAmberDark
    )
}
