package fr.aboucorp.variantchess.app.managers;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.db.user.User;
import fr.aboucorp.variantchess.app.exception.HashException;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSessionManager;
import fr.aboucorp.variantchess.app.utils.Encryptor;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;

public class AccountManager {
    private GoogleSignInClient googleSignInClient;
    private MainActivity mainActivity;
    private NakamaSessionManager nakamaSessionManager;
    private VariantChessDatabase database;
    // TODO add UserViewModel

    public AccountManager(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        String appId = mainActivity.getResources().getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(appId)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(mainActivity, gso);
        nakamaSessionManager = NakamaSessionManager.getInstance(mainActivity);
        this.database = VariantChessDatabase.getDatabase(mainActivity);
    }

    public void authentWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        mainActivity.startActivityForResult(signInIntent, 200);
    }

    public void handleAuthentWithGoogleResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.i("fr.aboucorp.variantchess","Successfully signed");
            com.heroiclabs.nakama.api.User nakamaUser = this.nakamaSessionManager.authentWithGoogle(account.getIdToken(),account.getEmail());
            User variantChessUser = new User();
            variantChessUser.displayName = nakamaUser.getDisplayName();
            variantChessUser.mail = account.getEmail();
            variantChessUser.password = null;
            variantChessUser.userId = nakamaUser.getId();
            variantChessUser.username = nakamaUser.getUsername();
            setUserConnected(variantChessUser);
        } catch (ApiException e) {
            Log.e("fr.aboucorp.variantchess", "signInResult:failed code=" + e.getMessage());
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess", "signInResult:failed code=" + e.getMessage());
        } catch (ExecutionException e) {
            Log.e("fr.aboucorp.variantchess", "signInResult:failed code=" + e.getMessage());
        }
    }

    public void disconnectUser() {
        this.googleSignInClient.signOut().addOnCompleteListener(this.mainActivity, task -> Toast.makeText(this.mainActivity,R.string.disconnect_message,Toast.LENGTH_LONG).show());
        this.mainActivity.setFragment(new AccountFragment());
    }

    public User authentWithEmail(String mail, String password,String displayName) throws ExecutionException, InterruptedException {
        com.heroiclabs.nakama.api.User nakamaUser = this.nakamaSessionManager.authentWithEmail(mail,password);
        User variantChessUser = new User();
        variantChessUser.displayName = displayName;
        variantChessUser.mail = mail;
        variantChessUser.password = password;
        variantChessUser.userId = nakamaUser.getId();
        variantChessUser.username = nakamaUser.getUsername();
        return variantChessUser;
    }

    public void signInWithEmail(String mail, String password, String displayName){
        try {
            String hashedPassword = Encryptor.hash(password);
            User variantChessUser = authentWithEmail(mail,hashedPassword,displayName);
            setUserConnected(variantChessUser);
        } catch (HashException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void signUpWithEmail(String mail, String password){
        try {
            String hashedPassword = Encryptor.hash(password);
            User variantChessUser = authentWithEmail(mail,hashedPassword,null);
            setUserConnected(variantChessUser);
        } catch (HashException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void signInWithGoogle() {
        this.authentWithGoogle();
    }

    public void signUpWithGoogle() {
        this.authentWithGoogle();
    }

    private void setUserConnected(User variantChessUser){
        new Thread(() -> {
            User existing = this.database.userDao().findByUserId(variantChessUser.userId);
            if(existing != null){
                existing.isConnected = true;
                this.database.userDao().update(variantChessUser);
            }else{
                variantChessUser.isConnected = true;
                this.database.userDao().insertAll(variantChessUser);
            }
        }).start();
        mainActivity.userIsConnected(variantChessUser);
    }
}
