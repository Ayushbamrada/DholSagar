//// file: com/dholsagar/app/presentation/onboarding_provider/components/SkillsAndExperiencePage.kt
//package com.dholsagar.app.presentation.onboarding_provider.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.dholsagar.app.presentation.onboarding_provider.TeamMember
//
//@Composable
//fun SkillsAndExperiencePage(
//    experience: String,
//    onExperienceChange: (String) -> Unit,
//    teamMembers: List<TeamMember>,
//    onAddMember: () -> Unit
//) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Spacer(modifier = Modifier.height(24.dp))
//        OutlinedTextField(
//            value = experience,
//            onValueChange = onExperienceChange,
//            label = { Text("Years of Experience") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(32.dp))
//        Text("Team Members", style = MaterialTheme.typography.titleMedium)
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyColumn(modifier = Modifier.weight(1f)) {
//            items(teamMembers) { member ->
//                Text("- ${member.name} (${member.role})")
//            }
//        }
//
//        Button(onClick = onAddMember) {
//            Text("Add Team Member")
//        }
//    }
//}


// file: com/dholsagar/app/presentation/onboarding_provider/components/SkillsAndExperiencePage.kt
package com.dholsagar.app.presentation.onboarding_provider.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dholsagar.app.presentation.onboarding_provider.TeamMember

@Composable
fun SkillsAndExperiencePage(
    experience: String,
    onExperienceChange: (String) -> Unit,
    teamMembers: List<TeamMember>,
    onAddMemberClicked: () -> Unit
) {
    // MODIFICATION: The entire page is now a single LazyColumn.
    // This creates a unified scrollable list for all content, which is the best
    // practice for forms that can grow in length (like adding team members).
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedTextField(
                value = experience,
                onValueChange = onExperienceChange,
                label = { Text("Years of Experience") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Star, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Your Team", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (teamMembers.isEmpty()) {
            item {
                Text(
                    text = "No team members added yet. Add some to build your crew!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(teamMembers) { member ->
                ListItem(
                    headlineContent = { Text(member.name) },
                    supportingContent = { Text(member.role) },
                    leadingContent = {
                        Icon(Icons.Outlined.Person, contentDescription = null)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = onAddMemberClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Member")
                Text("Add Team Member")
            }
        }
    }
}