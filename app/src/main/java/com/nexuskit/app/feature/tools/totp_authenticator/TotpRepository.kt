package com.nexuskit.app.feature.tools.totp_authenticator

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TotpRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val accountsFile: File
        get() = File(context.filesDir, "totp_accounts.json")

    private val _accounts = MutableStateFlow<List<TotpAccount>>(emptyList())
    val accounts: StateFlow<List<TotpAccount>> = _accounts.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        runCatching {
            if (accountsFile.exists()) {
                val jsonStr = accountsFile.readText()
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<TotpAccount>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        TotpAccount(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            accountName = obj.optString("accountName", ""),
                            issuer = obj.optString("issuer", ""),
                            secretKey = obj.optString("secretKey", ""),
                            digits = obj.optInt("digits", 6),
                            periodSeconds = obj.optInt("periodSeconds", 30),
                            algorithm = obj.optString("algorithm", "SHA1")
                        )
                    )
                }
                _accounts.value = list
            }
        }
    }

    private fun saveAccounts(list: List<TotpAccount>) {
        runCatching {
            val jsonArray = JSONArray()
            for (acc in list) {
                val obj = JSONObject().apply {
                    put("id", acc.id)
                    put("accountName", acc.accountName)
                    put("issuer", acc.issuer)
                    put("secretKey", acc.secretKey)
                    put("digits", acc.digits)
                    put("periodSeconds", acc.periodSeconds)
                    put("algorithm", acc.algorithm)
                }
                jsonArray.put(obj)
            }
            accountsFile.writeText(jsonArray.toString(2))
            _accounts.value = list
        }
    }

    suspend fun addAccount(account: TotpAccount) = withContext(Dispatchers.IO) {
        val updated = _accounts.value + account
        saveAccounts(updated)
    }

    suspend fun deleteAccount(id: String) = withContext(Dispatchers.IO) {
        val updated = _accounts.value.filter { it.id != id }
        saveAccounts(updated)
    }
}
