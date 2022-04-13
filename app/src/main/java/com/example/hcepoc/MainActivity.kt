package com.example.hcepoc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hcepoc.service.HCEService
import com.example.hcepoc.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mTurnNFCDialog : AlertDialog
    private var mNfcAdapter : NfcAdapter? = null
    private lateinit var startServiceBtn : Button
    private lateinit var idText : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        //BTN initialized
        startServiceBtn = findViewById<View>(R.id.startServiceBtn) as Button
        idText = findViewById<View>(R.id.editText) as EditText
        initNFCFunction()
    }

    private fun initNFCFunction(){
        if (checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)){
            initService()
        }else {
            //NFC 연결이 안 될 경우 , NFC 를 키게함.
            showTurnOnNfcDialog();
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNFCDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.ad_nfcTurnOn_title))
            .setMessage(getString(R.string.ad_nfcTurnOn_message))
            .setPositiveButton(
                getString(R.string.ad_nfcTurnOn_pos)
            ) { _, _ ->
                if (Build.VERSION.SDK_INT >= JELLY_BEAN){
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }else{
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }
            }.setNegativeButton(getString(R.string.ad_nfcTurnOn_neg)){
                _,_ ->
                onBackPressed()
            }
            .create()
        mTurnNFCDialog.show()
    }

    private fun initService(){
        startServiceBtn.setOnClickListener {
            if(TextUtils.isEmpty(idText.text)){
                Toast.makeText(
                    this@MainActivity,
                    "input ID",
                    Toast.LENGTH_LONG
                ).show();
            }else{
                val intent = Intent(this@MainActivity, HCEService::class.java)
                intent.putExtra("ndefMessage", idText.text.toString())
                startService(intent)
            }
        }
    }
    private fun checkNFCEnable() : Boolean{
        return if (mNfcAdapter == null){
            false
        }else{
            mNfcAdapter!!.isEnabled
        }
    }

    override fun onResume() {
        super.onResume()
        if(mNfcAdapter!!.isEnabled){
            initService()
        }
    }
}