package edu.cnm.deepdive.codebreaker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.codebreaker.BuildConfig;

import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import io.reactivex.Single;
import java.util.UUID;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CodebreakerWebService {

  @GET("users/me")
  Single<User> getProfile(@Header("Authorization") String bearerToken);

  @POST("matches")
  Single<Match> startMatch(@Header("Authorization") String bearerToken, @Body Match match);

  @POST("matches/{matchId}")
  Single<Match> getMatch(@Header("Authorization") String bearerToken, @Path("matchId") UUID match);

  static CodebreakerWebService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final CodebreakerWebService INSTANCE;

    static {
      Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSZ")
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URl)
          .build();
      INSTANCE = retrofit.create(CodebreakerWebService.class);
    }

  }
}