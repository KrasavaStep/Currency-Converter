package com.example.currencyconverter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import kotlinx.coroutines.*

fun EditText.afterTextChangedDebounce(delayMillis: Long, input: (String) -> Unit) {
    var lastInput = ""
    var debounceJob: Job? = null
    val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (editable != null) {
                val newtInput = editable.toString()
                debounceJob?.cancel()
                if (lastInput != newtInput) {
                    lastInput = newtInput
                    debounceJob = uiScope.launch {
                        delay(delayMillis)
                        if (lastInput == newtInput) {
                            input(newtInput)
                            if (newtInput.isNotBlank()){

                            }
                        }
                    }
                }
            }
        }

        override fun beforeTextChanged(cs: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {}
    })}