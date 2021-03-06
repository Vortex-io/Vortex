package io.vortex.android.firebase

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import io.vortex.android.firebase.impl.FirebaseAuthImpl
import io.vortex.android.firebase.listener.VortexAuthListener
import io.vortex.android.firebase.listener.VortexGoogleAuthLIstener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 10/26/2019
 * Time : 10:29 PM
 */

class VortexFirebaseAuth(
    private val auth: FirebaseAuth,
    private var listener: VortexAuthListener?
) : FirebaseAuthImpl {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    override suspend fun checkAccountStatus(): Boolean {
        return withContext(Dispatchers.IO) { auth.currentUser == null }
    }

    override suspend fun signInEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.let {
                            GlobalScope.launch {
                                listener?.onAuthSuccess(it.user)
                            }
                        }
                    } else {
                        GlobalScope.launch {
                            it.exception?.let {
                                listener?.onAuthError(it)
                            }
                        }
                    }
                }
        }
    }

    override suspend fun signInByGoogle(context: Context, defWebClientId: String, reqCode: Int) {
        withContext(Dispatchers.IO) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(defWebClientId)
                .requestEmail()
                .build()

            val intent = GoogleSignIn.getClient(context, gso).signInIntent
            (context as FragmentActivity).startActivityForResult(intent, reqCode)
        }
    }

    override suspend fun registerGoogleSignInHandler(
        requestCode: Int,
        data: Intent?,
        googleListener: VortexGoogleAuthLIstener?
    ) {
        withContext(Dispatchers.IO) {
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    googleListener?.onAuthSuccess(account)
                } catch (e: ApiException) {
                    GlobalScope.launch {
                        googleListener?.onAuthError(e)
                    }
                }
            }
        }
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    GlobalScope.launch {
                        it.result?.let {
                            listener?.onAuthSuccess(it.user)
                        }
                    }
                } else {
                    GlobalScope.launch {
                        it.exception?.let {
                            listener?.onAuthError(it)
                        }
                    }
                }
            }
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            auth.signOut()
        }
    }

    override suspend fun destroyAuth() {
        withContext(Dispatchers.IO) {
            listener = null
        }
    }

}
