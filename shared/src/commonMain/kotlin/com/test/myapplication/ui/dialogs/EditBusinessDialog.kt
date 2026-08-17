package com.test.myapplication.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.myapplication.ui.components.*
import com.test.myapplication.domain.navigation.Screen
import com.test.myapplication.domain.model.*
import com.test.myapplication.data.remote.MvmFirebase
import com.test.myapplication.data.repository.BibleRepository
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import coil3.compose.AsyncImage
import com.test.myapplication.ui.theme.MvmCard
import com.test.myapplication.ui.theme.MvmGold
import com.test.myapplication.ui.theme.MvmHeader
import kotlinx.coroutines.launch
import com.test.myapplication.util.*

@Composable
fun EditBusinessDialog(business: MvmBusiness, onDismiss: () -> Unit, onUpdate: (MvmBusiness) -> Unit, onDelete: (String) -> Unit) {
    var name by remember { mutableStateOf(business.name) }
    var category by remember { mutableStateOf(business.category) }
    var description by remember { mutableStateOf(business.description) }
    var phone by remember { mutableStateOf(business.phone) }
    var email by remember { mutableStateOf(business.email) }
    var logoUrl by remember { mutableStateOf(business.logoUrl) }
    var selectedImageUri by remember { mutableStateOf<Any?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wysig Besigheid", color = MvmGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Besigheid Naam") })
                TextField(value = category, onValueChange = { category = it }, label = { Text("Kategorie") })
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beskrywing") })
                TextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefoon") })
                TextField(value = email, onValueChange = { email = it }, label = { Text("E-pos") })
                
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "Logo", fontWeight = FontWeight.Bold, color = MvmGold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = { 
                            showToast("Lêer kieser nog nie geïmplementeer nie")
                            selectedImageUri = "dummy_logo.png"
                        },
                        color = MvmHeader,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MvmGold.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Verander Foto",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                            color = MvmGold,
                            fontSize = 12.sp
                        )
                    }
                    val displayImage = selectedImageUri ?: if (logoUrl.isNotEmpty()) logoUrl else null
                    if (displayImage != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        AsyncImage(
                            model = displayImage,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(4.dp))
                        )
                    }
                }
                if (isUploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = MvmGold)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isUploading,
                onClick = {
                    coroutineScope.launch {
                        isUploading = true
                        var finalLogoUrl = logoUrl
                        selectedImageUri?.let { uri ->
                            val uploadedUrl = MvmFirebase.uploadLogo(uri)
                            if (uploadedUrl != null) {
                                finalLogoUrl = uploadedUrl
                            }
                        }
                        onUpdate(business.copy(name = name, category = category, description = description, phone = phone, email = email, logoUrl = finalLogoUrl))
                        isUploading = false
                    }
                }
            ) {
                Text(if (isUploading) "Stoor..." else "Stoor", color = MvmGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onDelete(business.id) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Verwyder")
                }
                TextButton(onClick = onDismiss) {
                    Text("Kanselleer", color = Color.Gray)
                }
            }
        },
        containerColor = MvmCard,
        shape = RoundedCornerShape(16.dp)
    )
}
