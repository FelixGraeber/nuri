package app.getnuri.theme.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.getnuri.theme.LocalSharedTransitionScope
import app.getnuri.theme.displayFontFamily
import app.getnuri.theme.displayLargeFontFamily
import app.getnuri.theme.R
import app.getnuri.theme.SharedElementKey
import app.getnuri.theme.sharedBoundsReveal

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NuriTopAppBar(
    modifier: Modifier = Modifier,
    isMediumWindowSize: Boolean = false,
    backEnabled: Boolean = false,
    aboutEnabled: Boolean = true,
    customTitle: String? = null,
    expandedCenterButtons: @Composable () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    useNuriStyling: Boolean = false,
) {
    val backgroundColor = if (useNuriStyling) Color(0xFFF5FF8C) else MaterialTheme.colorScheme.surfaceContainerLowest
    val contentColor = if (useNuriStyling) Color(0xFF800080) else MaterialTheme.colorScheme.onSurface
    
    if (isMediumWindowSize) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 8.dp, end = 16.dp, top = 16.dp),
        ) {
            Row(
                modifier = modifier
                    .height(64.dp)
                    .padding(start = 8.dp)
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (backEnabled) {
                    BackButton(onBackPressed, contentColor)
                } else {
                    Spacer(modifier.size(16.dp))
                }
                if (customTitle != null) {
                    CustomTitle(customTitle, contentColor)
                } else {
                    NuriTitle(contentColor)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center),
            ) {
                expandedCenterButtons()
            }

            if (aboutEnabled) {
                AboutButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(
                            color = backgroundColor,
                            shape = CircleShape,
                        ),
                    onAboutClicked = onAboutClicked,
                    contentColor = contentColor,
                )
            }
        }
    } else {
        CenterAlignedTopAppBar(
            title = {
                if (customTitle != null) {
                    CustomTitle(customTitle, contentColor)
                } else {
                    NuriTitle(contentColor)
                }
            },
            modifier = modifier
                .statusBarsPadding()
                .padding(8.dp)
                .clip(
                    MaterialTheme.shapes.large,
                ),
            navigationIcon = {
                if (backEnabled) {
                    BackButton(onBackPressed, contentColor)
                }
            },
            actions = {
                if (aboutEnabled) {
                    AboutButton(onAboutClicked = onAboutClicked, contentColor = contentColor)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
        )
    }
}

// Legacy alias for backward compatibility - will be removed
@Deprecated("Use NuriTopAppBar instead", ReplaceWith("NuriTopAppBar"))
@Composable
fun AndroidifyTopAppBar(
    modifier: Modifier = Modifier,
    isMediumWindowSize: Boolean = false,
    backEnabled: Boolean = false,
    aboutEnabled: Boolean = true,
    customTitle: String? = null,
    expandedCenterButtons: @Composable () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    useNuriStyling: Boolean = false,
) = NuriTopAppBar(
    modifier = modifier,
    isMediumWindowSize = isMediumWindowSize,
    backEnabled = backEnabled,
    aboutEnabled = aboutEnabled,
    customTitle = customTitle,
    expandedCenterButtons = expandedCenterButtons,
    onBackPressed = onBackPressed,
    onAboutClicked = onAboutClicked,
    useNuriStyling = useNuriStyling,
)

@Composable
private fun BackButton(onBackPressed: () -> Unit, tintColor: Color = MaterialTheme.colorScheme.onSurface) {
    IconButton(onClick = onBackPressed) {
        Icon(
            ImageVector.vectorResource(R.drawable.rounded_arrow_back_24),
            contentDescription = "Back",
            tint = tintColor,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuriTranslucentTopAppBar(
    modifier: Modifier = Modifier,
    isMediumSizeLayout: Boolean = false,
) {
    if (isMediumSizeLayout) {
        TopAppBar(
            title = {
                Spacer(Modifier.statusBarsPadding())
                NuriTitle()
            },
            modifier = modifier.clip(
                MaterialTheme.shapes.large.copy(topStart = CornerSize(0f), topEnd = CornerSize(0f)),
            ),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Spacer(Modifier.statusBarsPadding())
                NuriTitle()
            },
            modifier = modifier.clip(
                MaterialTheme.shapes.large.copy(topStart = CornerSize(0f), topEnd = CornerSize(0f)),
            ),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )
    }
}

// Legacy alias for backward compatibility - will be removed
@Deprecated("Use NuriTranslucentTopAppBar instead", ReplaceWith("NuriTranslucentTopAppBar"))
@Composable
fun AndroidifyTranslucentTopAppBar(
    modifier: Modifier = Modifier,
    isMediumSizeLayout: Boolean = false,
) = NuriTranslucentTopAppBar(modifier, isMediumSizeLayout)

@Composable
private fun NuriTitle(textColor: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = stringResource(R.string.app_name_nuri),
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = displayLargeFontFamily,
            fontWeight = FontWeight.Black, // Font weight 900 for maximum impact
            letterSpacing = (-0.5).sp,
        ),
        color = textColor
    )
}

// Legacy alias for backward compatibility - will be removed
@Deprecated("Use NuriTitle instead", ReplaceWith("NuriTitle"))
@Composable
private fun AndroidifyTitle(textColor: Color = MaterialTheme.colorScheme.onSurface) = NuriTitle(textColor)

@Composable
private fun CustomTitle(title: String, textColor: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = displayLargeFontFamily,
            fontWeight = FontWeight.Black, // Font weight 900 for maximum impact
            letterSpacing = (-0.5).sp,
        ),
        color = textColor
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AboutButton(modifier: Modifier = Modifier, onAboutClicked: () -> Unit = {}, contentColor: Color = MaterialTheme.colorScheme.onSurface) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    with(sharedTransitionScope) {
        IconButton(
            onClick = {
                onAboutClicked()
            },
            modifier = modifier.sharedBoundsReveal(
                rememberSharedContentState(SharedElementKey.AboutKey),
                renderInOverlayDuringTransition = false,
            ),
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.outline_info_24),
                contentDescription = "About",
                tint = contentColor,
            )
        }
    }
}

// New styled top app bar specifically for nuri branded screens
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NuriStyledTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { 
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF800080) // Purple
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: { },
        actions = { actions?.invoke() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF5FF8C) // Light yellow-green background
        )
    )
}
