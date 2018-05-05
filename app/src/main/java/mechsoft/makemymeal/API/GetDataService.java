package mechsoft.makemymeal.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by priyanka.shirke on 02/05/18.
 */

public interface GetDataService {

    @GET("GetToken")
    Call<String> notifyFCMToken(@Query("token") String token, @Query("PreviousToken") String previous_token, @Query("UserName") String username);
}