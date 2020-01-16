package com.smartit.beunique.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartit.beunique.entity.account.EOAccountRegister;
import com.smartit.beunique.entity.account.EOChangePassword;
import com.smartit.beunique.entity.allproducts.EOBrandProducts;
import com.smartit.beunique.entity.currency.EOCurrencyList;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryIds;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryProducts;
import com.smartit.beunique.entity.drawerMenu.EOPerfumesSubMenu;
import com.smartit.beunique.entity.language.EOLanguageList;
import com.smartit.beunique.entity.manufacturers.EOManufacturersList;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

import static com.smartit.beunique.util.Constants.BASE_URL;


public class RestClient {

    public static APIInterface getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(logging)
                .readTimeout(60, TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        return retrofit.create(APIInterface.class);
    }

    public interface APIInterface {

        @FormUrlEncoded
        @POST("webservice/Registration.php")
        Call<ResponseBody> registerUser(@Field("firstname") String firstName,
                                        @Field("lastname") String lastName,
                                        @Field("email") String emailId,
                                        @Field("password") String password,
                                        @Field("id_gender") String id_gender,
                                        @Field("mobile") String mobile);

        @FormUrlEncoded
        @POST("webservice/Login.php")
        Call<ResponseBody> loginUser(@Field("email") String emailId, @Field("password") String password);

        @GET()
        Call<EOCategoryIds> getNewArrivalProductIds(@Url String url);

        @GET()
        Call<EOCategoryProducts> getNewArrivalProduct(@Url String url);

        @GET()
        Call<EOCategoryIds> getFeatureProductsIds(@Url String url);

        @GET()
        Call<EOCategoryProducts> getFeatureProducts(@Url String url);

        @GET()
        Call<EOCategoryIds> getTopSellersIds(@Url String url);

        @GET()
        Call<EOCategoryProducts> getTopSellersProducts(@Url String url);

        @GET("api/languages/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=full")
        Call<EOLanguageList> getLanguageApi();

        @GET("api/currencies/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=full&filter[active]=1")
        Call<EOCurrencyList> getCurrencyApi();

        @GET()
        Call<EOPerfumesSubMenu> getPerfumesSubMenus(@Url String url);

        @GET()
        Call<EOManufacturersList> getAllBrandsApi(@Url String url);

        @GET()
        Call<ResponseBody> getTotalProductsCount(@Url String url);

        @GET()
        Call<EOBrandProducts> getBrandsWiseProducts(@Url String url);

        @GET()
        Call<ResponseBody> getTotalCategoryCounts(@Url String url);

        @GET()
        Call<ResponseBody> getMagazinesApi(@Url String url);

        @GET()
        Call<ResponseBody> getMagazineDetailsApi(@Url String url);

        @FormUrlEncoded
        @POST("webservice/blog/add_comment.php")
        Call<ResponseBody> sendComment(@Field("id_blog") String blogId,
                                       @Field("id_customer") String customerId,
                                       @Field("customer_name") String customerName,
                                       @Field("customer_email") String customerEmail,
                                       @Field("content") String contents
        );


        @GET()
        Call<ResponseBody> getProductDetailVideoWishlistApi(@Url String url);

        @FormUrlEncoded
        @POST("webservice/product/add_comment.php")
        Call<ResponseBody> sendReviewForProduct(@Field("id_customer") String customerId,
                                                @Field("customer_name") String customerName,
                                                @Field("id_product") int productId,
                                                @Field("grade") int grade,
                                                @Field("title") String title,
                                                @Field("content") String description);

        @FormUrlEncoded
        @POST("webservice/product/add_or_remove_wishlist.php")
        Call<ResponseBody> addProductToWishlist(@Field("id_customer") String customerId,
                                                @Field("name") String name,
                                                @Field("id_product") int productId,
                                                @Field("quantity") int quantity,
                                                @Field("id_product_attribute") int id_product_attribute);


        @FormUrlEncoded
        @POST("webservice/product/add_or_remove_wishlist.php")
        Call<ResponseBody> removeProductToWishlist(@Field("id_wishlist") String id_wishlist);

        @GET()
        Call<ResponseBody> getAllWishlistProducts(@Url String url);

        @GET()
        Call<EOCategoryProducts> getWishlistProduct(@Url String url);

        @GET()
        Call<ResponseBody> getAllPointsOfOrders(@Url String url);

        @GET()
        Call<ResponseBody> getAllSearchResults(@Url String url);

        @FormUrlEncoded
        @POST("webservice/cart/Cart.php")
        Call<ResponseBody> addProductInCart(@Field("id_currency") int currencyId,
                                            @Field("id_lang") int langId,
                                            @Field("id_product") int productId,
                                            @Field("id_product_attribute") int id_product_attribute,
                                            @Field("id_customer") int customerId,
                                            @Field("quantity") int quantity);

        @GET()
        Call<ResponseBody> showDataInCart(@Url String url);

        @FormUrlEncoded
        @POST("webservice/cart/remove_cart.php")
        Call<ResponseBody> removeProductFromCart(@Field("id_cart") int cartId);

        @FormUrlEncoded
        @POST
        Call<ResponseBody> updateProductInCart(@Url String url,
                                               @Field("id_cart") int cartId,
                                               @Field("quantity") int quantity);

        @FormUrlEncoded
        @POST("webservice/my-account/change_password.php")
        Call<EOChangePassword> changePasswordApi(@Field("id_customer") String customerId,
                                                 @Field("old_password") String oldPassword,
                                                 @Field("new_password") String newPassword);

        @FormUrlEncoded
        @POST
        Call<EOAccountRegister> updateProfileApi(@Url String url,
                                                 @Field("firstname") String firstname,
                                                 @Field("lastname") String lastname,
                                                 @Field("email") String email,
                                                 @Field("id_gender") String id_gender,
                                                 @Field("id_lang") int langId,
                                                 @Field("mobile") String mobile,
                                                 @Field("dob") String dob);

        @Multipart
        @POST
        Call<EOAccountRegister> uploadProfileImage(@Url String url,
                                                   @Part MultipartBody.Part file);

        @FormUrlEncoded
        @POST("webservice/my-account/get_profile.php")
        Call<ResponseBody> getUserInfo(@Field("id_customer") String customerId);



    }


}
