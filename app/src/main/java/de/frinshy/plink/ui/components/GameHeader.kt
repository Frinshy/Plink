package de.frinshy.plink.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import de.frinshy.plink.ui.theme.TextStyle.GameTitleTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onTitleClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            TitleText(text = title, onClick = onTitleClick)
        }, actions = actions, modifier = modifier, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ), windowInsets = TopAppBarDefaults.windowInsets, navigationIcon = {}, scrollBehavior = null
    )
}

@Composable
private fun TitleText(text: String, onClick: (() -> Unit)?) {
    val modifier = if (onClick != null) {
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) { onClick() }
    } else Modifier

    Text(
        text = text,
        style = GameTitleTypography,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}


@Composable
fun SectionHeader(
    title: String, icon: ImageVector, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary
        )
    }
}