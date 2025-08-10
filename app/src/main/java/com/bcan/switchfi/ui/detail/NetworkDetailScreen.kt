package com.bcan.switchfi.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bcan.switchfi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetailScreen(ssid: String?) {
    Scaffold(topBar = {
        LargeTopAppBar(title = { Text(text = stringResource(id = R.string.title_network_detail)) })
    }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = ssid ?: stringResource(id = R.string.ssid_unknown))
        }
    }
}


