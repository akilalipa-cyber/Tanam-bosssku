package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddBatchDialog
import com.example.ui.screens.BatchDetailScreen
import com.example.ui.screens.BatchesScreen
import com.example.ui.screens.CareTasksScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HealthAlertsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.WorkspaceProfileScreen
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LeafGreenContainer

enum class NavDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Beranda", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    BATCHES("Tanaman", Icons.Filled.Eco, Icons.Outlined.Eco, "nav_batches"),
    TASKS("Perawatan", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop, "nav_tasks"),
    HEALTH("Kesehatan", Icons.Filled.HealthAndSafety, Icons.Outlined.HealthAndSafety, "nav_health"),
    REPORTS("Laporan", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_reports")
}

@Composable
fun TanamBosquuApp(
    viewModel: PlantViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var activeBatchId by remember { mutableStateOf<String?>(null) }
    var showAddBatchDialog by remember { mutableStateOf(false) }

    // System BackHandler
    BackHandler(enabled = activeBatchId != null || currentDestination != NavDestination.DASHBOARD) {
        if (activeBatchId != null) {
            activeBatchId = null
        } else {
            currentDestination = NavDestination.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (activeBatchId == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavDestination.values().forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                activeBatchId = null
                                currentDestination = destination
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreenPrimary,
                                selectedTextColor = ForestGreenPrimary,
                                indicatorColor = LeafGreenContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeBatchId != null) {
                BatchDetailScreen(
                    batchId = activeBatchId!!,
                    viewModel = viewModel,
                    onBack = { activeBatchId = null }
                )
            } else {
                when (currentDestination) {
                    NavDestination.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToBatches = { currentDestination = NavDestination.BATCHES },
                        onNavigateToBatchDetail = { batchId -> activeBatchId = batchId },
                        onNavigateToCareTasks = { currentDestination = NavDestination.TASKS },
                        onNavigateToHealth = { currentDestination = NavDestination.HEALTH },
                        onAddBatchClick = { showAddBatchDialog = true }
                    )
                    NavDestination.BATCHES -> BatchesScreen(
                        viewModel = viewModel,
                        onBatchClick = { batchId -> activeBatchId = batchId },
                        onAddBatchClick = { showAddBatchDialog = true }
                    )
                    NavDestination.TASKS -> CareTasksScreen(
                        viewModel = viewModel
                    )
                    NavDestination.HEALTH -> HealthAlertsScreen(
                        viewModel = viewModel
                    )
                    NavDestination.REPORTS -> ReportsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showAddBatchDialog) {
        AddBatchDialog(
            onDismiss = { showAddBatchDialog = false },
            onConfirm = { pName, sName, cat, varName, seeds, initSeed, pDate, sSource, loc, med, tray, pic, germ, trans, notes ->
                viewModel.addBatch(
                    pName, sName, cat, varName, seeds, initSeed, pDate, sSource, loc, med, tray, pic, germ, trans, notes
                )
                showAddBatchDialog = false
            }
        )
    }
}
