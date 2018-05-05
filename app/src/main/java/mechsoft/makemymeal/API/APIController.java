package mechsoft.makemymeal.API;

import android.text.TextUtils;
import android.util.Log;

import mechsoft.makemymeal.Util.MConstants;
import mechsoft.makemymeal.MMMApplication;
import mechsoft.makemymeal.Util.MMMPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by priyanka.shirke on 02/05/18.
 */

public class APIController {

    public void notifyToken(String token) {
        MMMPreferences mmmPreferences = MMMPreferences.getInstance(MMMApplication.getInstance().getApplicationContext());
        final String username = mmmPreferences.loadPreferences(MConstants.KEY_USERNAME);
        String previous_token = mmmPreferences.loadPreferences(MConstants.PREVIOUS_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            token = mmmPreferences.loadPreferences(MConstants.TOKEN);
        }

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<String> call = service.notifyFCMToken(token, previous_token, username);
        call.enqueue(new Callback<String>() {
            MMMPreferences mmmPreferences = MMMPreferences.getInstance(MMMApplication.getInstance().getApplicationContext());

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!TextUtils.isEmpty(response.body()))
                    Log.d("Response : ", response.body());
                /**
                 * If user is anonymous i.e. username is blank
                 * then Make Anonymous FCM Token sent flag true.
                 */
                if (TextUtils.isEmpty(username)) {
                    mmmPreferences.savePreferences(MConstants.ANM_TOKEN_SENT, true);
                } else {
                    mmmPreferences.savePreferences(MConstants.LGN_TOKEN_SENT, true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Response: ", call.toString());
                if (TextUtils.isEmpty(username)) {
                    mmmPreferences.savePreferences(MConstants.ANM_TOKEN_SENT, false);
                } else {
                    mmmPreferences.savePreferences(MConstants.LGN_TOKEN_SENT, false);
                }
            }
        });
    }
}
