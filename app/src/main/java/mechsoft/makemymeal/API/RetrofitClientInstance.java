package mechsoft.makemymeal.API;

import mechsoft.makemymeal.Util.MConstants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by priyanka.shirke on 02/05/18.
 */

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = MConstants.HOME_URL;
    private static final String NOTIFICATION_URL=MConstants.NOTIFICATION_BASE_URL+"/api/home/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(NOTIFICATION_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
