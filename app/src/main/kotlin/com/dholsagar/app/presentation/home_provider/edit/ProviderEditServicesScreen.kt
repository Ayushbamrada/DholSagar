package com.dholsagar.app.presentation.home_provider.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dholsagar.app.presentation.home_provider.ProviderDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderEditServicesScreen(
    navController: NavController,
    viewModel: ProviderDashboardViewModel = hiltViewModel()
) {
    val description by viewModel.description.collectAsState()
    val specialty by viewModel.specialty.collectAsState()
    val perDayCharge by viewModel.perDayCharge.collectAsState()
    val chargeDescription by viewModel.chargeDescription.collectAsState()
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Details saved successfully!")
            viewModel.onSaveSuccessShown() // Reset flag
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Services") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Pricing", style = MaterialTheme.typography.titleMedium, color = Color(0xFF5D4037))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = perDayCharge,
                    onValueChange = viewModel::onPerDayChargeChange,
                    label = { Text("Price (₹)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = chargeDescription,
                    onValueChange = viewModel::onChargeDescriptionChange,
                    label = { Text("Per (Unit)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("e.g. Day/Hour") }
                )
            }

            Text("Details", style = MaterialTheme.typography.titleMedium, color = Color(0xFF5D4037))

            OutlinedTextField(
                value = specialty,
                onValueChange = viewModel::onSpecialtyChange,
                label = { Text("Specialty") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Punjabi Dhol, Nashik Dhol") }
            )

            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description / Bio") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::onSaveDetails,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037)),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Filled.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes")
                }
            }
        }
    }
}