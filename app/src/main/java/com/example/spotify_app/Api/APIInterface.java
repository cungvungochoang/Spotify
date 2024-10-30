package com.example.spotify_app.Api;

import com.example.spotify_app.Model.AlbumReponse;
import com.example.spotify_app.Model.AuthResponse;
import com.example.spotify_app.Model.FavoriteResponse;
import com.example.spotify_app.Model.FavoriteSongResponse;
import com.example.spotify_app.Model.HomeResponse;
import com.example.spotify_app.Model.Playlist;
import com.example.spotify_app.Model.PlaylistItemResponse;
import com.example.spotify_app.Model.PlaylistResponse;
import com.example.spotify_app.Model.SearchSongResponse;
import com.example.spotify_app.Model.SongResponse;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APIInterface {
    @GET("home")
    Call<HomeResponse> getStudentResource();

    @GET("album/{albumID}")
    Call<AlbumReponse> getDetailAlbum(@Path("albumID") int albumID);

    @GET("songs-favorite/{ID}")
    Call<FavoriteSongResponse> getListAllFavoriteSongs(@Path("ID") Object ID);

    @POST("songs-favorite")
    Call<SongResponse> insertSongsFavorite(@Body JsonObject jsonObject);


    @GET("playlist/{userId}")
    Call<PlaylistResponse> getPlaylistResource(@Path("userId") int userId);

    @POST("playlist/add-song")
    Call<SongResponse> insertSongsPlaylist(@Body JsonObject jsonObject);

    @FormUrlEncoded
    @POST("playlist")
    Call<PlaylistItemResponse> createPlaylistResource(@Field("title") String title, @Field("userId") int userId);

    @GET("songs-search")
    Call<SearchSongResponse> searchSongResource(@Query("name") String songName);

    @FormUrlEncoded
    @POST("auth/login")
    Call<AuthResponse> loginResource(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/register")
    Call<AuthResponse> registerResource(@Field("fullname") String fullname , @Field("email") String email, @Field("password") String password);

    @GET("songs-favorite/{userId}")
    Call<FavoriteResponse> getFavoriteListSongResource(@Path("userId") int userId);

    @Multipart
    @PUT("auth/edit/{userId}")
    Call<AuthResponse> editUserResource(@Path("userId") int userId,
                                        @Part("fullname") RequestBody fullname,
                                        @Part MultipartBody.Part file);

    @Multipart
    @PUT("auth/edit/{userId}")
    Call<AuthResponse> editUserWithoutImageResource(@Path("userId") int userId,
                                        @Part("fullname") RequestBody fullname);
}
