package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Diálogo modal para validar un PIN de 4 dígitos.
 *
 * @param onDismiss Acción al cancelar o cerrar el diálogo.
 * @param onPinValido Acción que se ejecuta al ingresar el PIN correcto ("1234").
 * @param pinEsperado PIN requerido para la validación (por defecto "1234").
 */
@Composable
fun PinDialog(
    onDismiss: () -> Unit,
    onPinValido: () -> Unit,
    pinEsperado: String = "1234",
    modifier: Modifier = Modifier
) {
    var pinText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("pin_dialog"),
        title = {
            Text(
                text = "Ingrese el PIN",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { nuevoTexto ->
                        // Limitar a máximo 4 dígitos numéricos
                        val filtrado = nuevoTexto.filter { it.isDigit() }
                        if (filtrado.length <= 4) {
                            pinText = filtrado
                        }
                    },
                    label = { Text("PIN de 4 dígitos") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pin_text_field")
                )

                Spacer(modifier = Modifier.height(8.dp))

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pinText == pinEsperado) {
                        onPinValido()
                    } else {
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("PIN incorrecto")
                        }
                    }
                },
                modifier = Modifier.testTag("pin_validate_button")
            ) {
                Text("Validar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("pin_cancel_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
