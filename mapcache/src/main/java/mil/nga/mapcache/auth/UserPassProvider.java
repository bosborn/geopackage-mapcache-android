package mil.nga.mapcache.auth;

import android.accounts.Account;
import android.app.Activity;

import java.net.URL;

public class UserPassProvider {

    private Activity activity;

    public UserPassProvider(Activity activity) {
        this.activity = activity;
    }

    public Account getAccount(URL url) {
        Account account = null;

        activity.runOnUiThread(()->askUser());

        return account;
    }

    private void askUser() {
        UserPassDialog dialog = new UserPassDialog(this.activity);
        dialog.show();
    }
}
