package app.simple.inure.activities.association

import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import app.simple.inure.extension.activities.BaseActivity

class ApkInstallerAssociationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println(DocumentFile.fromSingleUri(applicationContext, intent!!.data!!)?.name)
    }
}