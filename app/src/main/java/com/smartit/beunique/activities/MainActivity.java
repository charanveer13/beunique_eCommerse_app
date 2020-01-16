package com.smartit.beunique.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.LevelBeamView;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.account.EOAccountRegister;
import com.smartit.beunique.entity.currency.EOCurrency;
import com.smartit.beunique.entity.drawerMenu.SubMenuCategory;
import com.smartit.beunique.entity.language.EOLanguage;
import com.smartit.beunique.expandablelistview.BaseItem;
import com.smartit.beunique.expandablelistview.GroupItem;
import com.smartit.beunique.expandablelistview.Item;
import com.smartit.beunique.fragments.AccountFragment;
import com.smartit.beunique.fragments.DashboardFragment;
import com.smartit.beunique.fragments.SearchFragment;
import com.smartit.beunique.fragments.WhatsappFragment;
import com.smartit.beunique.fragments.WhishlistFragment;
import com.smartit.beunique.util.BottomNavigationHelper;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

import static android.view.View.LAYOUT_DIRECTION_LTR;
import static android.view.View.LAYOUT_DIRECTION_RTL;
import static android.view.View.TEXT_DIRECTION_LTR;
import static android.view.View.TEXT_DIRECTION_RTL;
import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_ID;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_ISO_CODE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;
import static com.smartit.beunique.util.Constants.SELECTED_PROFILE_PIC;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private DrawerLayout drawer;
    private BottomNavigationView bottomNavigationView;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private String selectedCurrencySign, selectedConversionRate;

    private LinearLayout layout_free_shipping, layout_gift_with_every_product, layout_original_products, layout_logout,
            layout_user_info, layout_welcome;
    private CircleImageView profileImage;
    private TextView tv_profile_name, tv_profile_email;
    private SessionSecuredPreferences loginSignupPreference;

    private MultiLevelListView multiLevelMenu;
    private static final int MAX_LEVELS = 3;

    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;

    private ArrayList <EOLanguage> eoLanguageArrayList = new ArrayList <> ( );
    private ArrayList <EOCurrency> eoCurrencyArrayList = new ArrayList <> ( );
    private ArrayList <SubMenuCategory> menuPerfumesArrayList = new ArrayList <> ( );
    private EOAccountRegister userInfoObject; //TODO it is used to set data on navigation drawer for user profile

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( newBase ) );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        //TODO set layout direction on language change
        if(LANGUAGE_ARABIC.equals ( LocalizationHelper.getLanguage ( this ) )) {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_RTL );
        } else {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_LTR );
        }

        setContentView ( R.layout.drawer_layout );
        UIUtil.hideKeyboard ( this );

        Bundle bundle = (!ObjectUtil.isEmpty ( this.getIntent ( ).getBundleExtra ( "bundleList" ) )) ? this.getIntent ( ).getBundleExtra ( "bundleList" ) : new Bundle ( );

        if((!ObjectUtil.isEmpty ( bundle.getSerializable ( "languageList" ) )) || (!ObjectUtil.isEmpty ( bundle.getSerializable ( "currencyList" ) ))
                || (!ObjectUtil.isEmpty ( bundle.getSerializable ( "perfumesList" ) ))
                || !ObjectUtil.isEmpty ( this.getIntent ( ).getSerializableExtra ( "userInfoObject" ) )) {

            this.eoLanguageArrayList = (ArrayList <EOLanguage>) bundle.getSerializable ( "languageList" );
            this.eoCurrencyArrayList = (ArrayList <EOCurrency>) bundle.getSerializable ( "currencyList" );
            this.menuPerfumesArrayList = (ArrayList <SubMenuCategory>) bundle.getSerializable ( "perfumesList" );
            this.userInfoObject = (EOAccountRegister) this.getIntent ( ).getSerializableExtra ( "userInfoObject" );
        }

        this.initView ( );
        this.loadMenusOnDrawer ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }

    private void initView() {
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );

        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );

        this.drawer = this.findViewById ( R.id.drawer_layout );

        //TODO here toolbar layout
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );
        this.iv_cart_icon.setVisibility ( View.GONE );

        this.bottomNavigationView = this.findViewById ( R.id.navigation );

        this.layout_free_shipping = this.findViewById ( R.id.layout_free_shipping );
        this.layout_gift_with_every_product = this.findViewById ( R.id.layout_gift_with_every_product );
        this.layout_original_products = this.findViewById ( R.id.layout_original_products );
        this.layout_logout = this.findViewById ( R.id.layout_logout );
        this.profileImage = this.findViewById ( R.id.profileImage );
        this.tv_profile_name = this.findViewById ( R.id.tv_profile_name );
        this.tv_profile_email = this.findViewById ( R.id.tv_profile_email );

        this.multiLevelMenu = this.findViewById ( R.id.multiLevelMenu );

        this.layout_user_info = this.findViewById ( R.id.layout_user_info );
        this.layout_welcome = this.findViewById ( R.id.layout_welcome );

    }

    private void loadMenusOnDrawer() {
        ListAdapter listAdapter = new ListAdapter ( );
        this.multiLevelMenu.setAdapter ( listAdapter );
        listAdapter.setDataItems ( getInitialItems ( ) );
    }

    private void setOnClickListener() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener ( mOnNavigationItemSelectedListener );
        this.multiLevelMenu.setOnItemClickListener ( mOnItemClickListener );
        this.layout_free_shipping.setOnClickListener ( this );
        this.layout_gift_with_every_product.setOnClickListener ( this );
        this.layout_original_products.setOnClickListener ( this );
        this.layout_logout.setOnClickListener ( this );
        this.iv_back_arrow.setOnClickListener ( this );
        this.layout_user_info.setOnClickListener ( this );
        this.layout_welcome.setOnClickListener ( this );
    }

    public List <BaseItem> getInitialItems() {
        List <BaseItem> rootMenu = new ArrayList <> ( );
        rootMenu.add ( new GroupItem ( getString ( R.string.perfumes ) ) );
        rootMenu.add ( new Item ( getString ( R.string.hair_perfumes ) ) );
        rootMenu.add ( new GroupItem ( getString ( R.string.brands ) ) );
        rootMenu.add ( new Item ( getString ( R.string.magazine ) ) );
        rootMenu.add ( new GroupItem ( getString ( R.string.languages ) ) );
        rootMenu.add ( new GroupItem ( getString ( R.string.currencies ) ) );
        rootMenu.add ( new GroupItem ( getString ( R.string.rate_our_app ) ) );
        return rootMenu;
    }

    private void dataToView() {

        this.multiLevelMenu.setLayoutDirection ( selectedLangId == 1 ? LAYOUT_DIRECTION_LTR : LAYOUT_DIRECTION_RTL );
        this.multiLevelMenu.setTextDirection ( selectedLangId == 1 ? TEXT_DIRECTION_LTR : TEXT_DIRECTION_RTL );
        this.multiLevelMenu.setBackgroundColor ( getResources ( ).getColor ( android.R.color.transparent ) );
        this.iv_back_arrow.setImageResource ( R.drawable.ic_slide_bar );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        //TODO from here remove the shifting mode animation from bottom navigation
        BottomNavigationHelper.removeShiftMode ( bottomNavigationView );
        //TODO Always load first fragment as default
        this.bottomNavigationView.setSelectedItemId ( R.id.navigation_home );
    }


    private OnItemClickListener mOnItemClickListener = new OnItemClickListener ( ) {

        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, final ItemInfo itemInfo) {

            if((((BaseItem) item).getName ( ) instanceof String)) { //TODO this case is used to set data for string "HairPerfumes" & "Magazine"
                String str = (String) (((BaseItem) item).getName ( ));
                if(str.equals ( getString ( R.string.hair_perfumes ) )) {
                    if(drawer.isDrawerOpen ( GravityCompat.START )) {
                        drawer.closeDrawers ( );
                    }
                    Intent productCategoryIntent = new Intent ( view.getContext ( ), PerfumesMenuSubCategoryActivity.class );
                    productCategoryIntent.putExtra ( "isShowAllHairPerfumes", true );
                    view.getContext ( ).startActivity ( productCategoryIntent );
                } else if(str.equals ( getString ( R.string.magazine ) )) {
                    if(drawer.isDrawerOpen ( GravityCompat.START )) {
                        drawer.closeDrawers ( );
                    }
                    Intent magazineIntent = new Intent ( view.getContext ( ), MagazinesActivity.class );
                    view.getContext ( ).startActivity ( magazineIntent );
                }
            }

            if((((BaseItem) item).getName ( ) instanceof SubMenuCategory)) {
                SubMenuCategory subMenuCategory = (SubMenuCategory) ((BaseItem) item).getName ( );
                if(subMenuCategory.getId ( ) == 459) {
                    Intent productCategoryIntent = new Intent ( view.getContext ( ), PerfumesMenuSubCategoryActivity.class );
                    productCategoryIntent.putExtra ( "menuCategoryId", subMenuCategory.getId ( ) );
                    productCategoryIntent.putExtra ( "isShowAllKidsPerfumes", true );
                    view.getContext ( ).startActivity ( productCategoryIntent );
                } else {
                    if(drawer.isDrawerOpen ( GravityCompat.START )) {
                        drawer.closeDrawers ( );
                    }
                    Intent menuCategoryIntent = new Intent ( view.getContext ( ), PerfumesMenuCategoryActivity.class );
                    menuCategoryIntent.putExtra ( "menuCategoryId", subMenuCategory.getId ( ) );
                    view.getContext ( ).startActivity ( menuCategoryIntent );
                }
            } else if((((BaseItem) item).getName ( ) instanceof EOLanguage)) {
                EOLanguage eoLanguage = (EOLanguage) ((BaseItem) item).getName ( );
                SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
                securedPreferences.edit ( ).putInt ( Constants.SELECTED_LANG_ID, eoLanguage.getId ( ) ).apply ( );
                LocalizationHelper.setLocale ( view.getContext ( ), eoLanguage.getIsoCode ( ) );
                if(drawer.isDrawerOpen ( GravityCompat.START )) {
                    drawer.closeDrawers ( );
                }
                view.getContext ( ).startActivity ( new Intent ( view.getContext ( ), ActivityExplore.class ) );

            } else if((((BaseItem) item).getName ( ) instanceof EOCurrency)) {
                EOCurrency eoCurrency = (EOCurrency) ((BaseItem) item).getName ( );
                SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
                securedPreferences.edit ( ).putInt ( SELECTED_CURRENCY_ID, eoCurrency.getId ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_ISO_CODE, eoCurrency.getIsoCode ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_SIGN, eoCurrency.getSign ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_CONVERSION_RATE, eoCurrency.getConversionRate ( ) ).apply ( );
                if(drawer.isDrawerOpen ( GravityCompat.START )) {
                    drawer.closeDrawers ( );
                }
                view.getContext ( ).startActivity ( new Intent ( view.getContext ( ), ActivityExplore.class ) );
                //Toast.makeText ( MainActivity.this, "Clicked On :" + eoCurrency.getIsoCode ( ), Toast.LENGTH_SHORT ).show ( );
            }

        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, final Object item, ItemInfo itemInfo) {

            if(itemInfo.isExpandable ( )) {
                if((((BaseItem) item).getName ( ) instanceof String)) { //TODO this case is used to set data for string "Brands" & "Q and A"
                    String str = (String) (((BaseItem) item).getName ( ));
                    if(str.equals ( getString ( R.string.brands ) )) {
                        if(drawer.isDrawerOpen ( GravityCompat.START )) {
                            drawer.closeDrawers ( );
                        }
                        Intent brandsIntent = new Intent ( view.getContext ( ), BrandsActivity.class );
                        view.getContext ( ).startActivity ( brandsIntent );
                    } else if(str.equals ( getString ( R.string.rate_our_app ) )) {
                        if(drawer.isDrawerOpen ( GravityCompat.START )) {
                            drawer.closeDrawers ( );
                        }
                        //TODO rate application on play store from here
                        rateOurApp ( );
                    }
                }
            }

        }
    };

    private void rateOurApp() {
        Uri uri = Uri.parse ( "market://details?id=" + ApplicationHelper.application ( ).packageName ( ) );
        Intent playStoreIntent = new Intent ( Intent.ACTION_VIEW, uri );
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        playStoreIntent.addFlags ( Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK );
        try {
            startActivity ( playStoreIntent );
        } catch (ActivityNotFoundException e) {
            e.printStackTrace ( );
        }
    }

    private List <BaseItem> getPerfumesList() {
        List <BaseItem> perfumesList = new ArrayList <> ( );
        for (SubMenuCategory perfumeMenu : this.menuPerfumesArrayList) {
            perfumesList.add ( new Item ( perfumeMenu ) );
        }
        return perfumesList;
    }

    private List <BaseItem> getLanguageList() {
        List <BaseItem> languageList = new ArrayList <> ( );
        for (EOLanguage eoLanguage : this.eoLanguageArrayList) {
            if(!(selectedLangId == eoLanguage.getId ( ))) {
                languageList.add ( new Item ( eoLanguage ) );
            }
        }
        return languageList;
    }

    private List <BaseItem> getCurrenciesList() {
        List <BaseItem> currencyList = new ArrayList <> ( );
        for (EOCurrency eoCurrency : this.eoCurrencyArrayList) {
            if(!selectedCurrencySign.equals ( eoCurrency.getSign ( ) )) {
                currencyList.add ( new Item ( eoCurrency ) );
            }
        }
        return currencyList;
    }

    public List <BaseItem> getSubItems(BaseItem baseItem) {
        List <BaseItem> result = new ArrayList <> ( );

        int level = ((GroupItem) baseItem).getLevel ( ) + 1;
        String menuItem = (String) baseItem.getName ( );

        if(!(baseItem instanceof GroupItem)) {
            throw new IllegalArgumentException ( "GroupItem required" );
        }
        GroupItem groupItem = (GroupItem) baseItem;
        if(groupItem.getLevel ( ) >= MAX_LEVELS) {
            return null;
        }

        //TODO on the basis of expandable menu load sub menu list from here
        switch (level) {
            case LEVEL_1:
                if(menuItem.equals ( getString ( R.string.perfumes ) )) {
                    result = getPerfumesList ( );
                } else if(menuItem.equals ( getString ( R.string.languages ) )) {
                    result = getLanguageList ( );
                } else if(menuItem.equals ( getString ( R.string.currencies ) )) {
                    result = getCurrenciesList ( );
                }
                break;
        }
        return result;
    }

    private class ListAdapter extends MultiLevelListAdapter {

        private class ViewHolder {
            LevelBeamView levelBeamView;
            TextView nameView;
            ImageView arrowView;
            RelativeLayout relativeLayout;
        }

        @Override
        public List <?> getSubObjects(Object object) {
            return getSubItems ( (BaseItem) object );
        }

        @Override
        public boolean isExpandable(Object object) {
            return object instanceof GroupItem;
        }

        @Override
        public View getViewForObject(Object object, View convertView, ItemInfo itemInfo) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder ( );
                convertView = LayoutInflater.from ( MainActivity.this ).inflate ( R.layout.drawer_list_row_item, null );
                viewHolder.nameView = convertView.findViewById ( R.id.dataItemName );
                viewHolder.arrowView = convertView.findViewById ( R.id.dataItemArrow );
                viewHolder.levelBeamView = convertView.findViewById ( R.id.dataItemLevelBeam );
                viewHolder.relativeLayout = convertView.findViewById ( R.id.relativeLayout );
                convertView.setTag ( viewHolder );
            } else {
                viewHolder = (ViewHolder) convertView.getTag ( );
            }

            viewHolder.levelBeamView.setLevel ( itemInfo.getLevel ( ) );

            if(itemInfo.isExpandable ( )) {
                if((((BaseItem) object).getName ( ) instanceof String)) {
                    String menuName = (String) ((BaseItem) object).getName ( );
                    viewHolder.nameView.setText ( menuName );
                    viewHolder.arrowView.setVisibility ( View.VISIBLE );
                    if(menuName.equalsIgnoreCase ( getString ( R.string.perfumes ) ))
                        viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_down_arrow : R.drawable.ic_right_arrow );
                    if(menuName.equalsIgnoreCase ( getString ( R.string.languages ) ))
                        viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_down_arrow : R.drawable.ic_right_arrow );
                    if(menuName.equalsIgnoreCase ( getString ( R.string.currencies ) ))
                        viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_down_arrow : R.drawable.ic_right_arrow );
                    if(menuName.equalsIgnoreCase ( getString ( R.string.brands ) ))
                        viewHolder.arrowView.setImageResource ( R.drawable.ic_right_arrow );
                    if(menuName.equalsIgnoreCase ( getString ( R.string.rate_our_app ) ))
                        viewHolder.arrowView.setImageResource ( R.drawable.ic_right_arrow );

                }
            } else {
                viewHolder.arrowView.setVisibility ( View.GONE );
                //TODO here we set different types of object for perfumes menu, language menu and currency menu
                Object convertedObject = (((BaseItem) object).getName ( ));
                if(convertedObject instanceof SubMenuCategory) {
                    SubMenuCategory subMenuCategory = (SubMenuCategory) convertedObject;
                    viewHolder.nameView.setText ( subMenuCategory.getName ( ) );
                    //viewHolder.arrowView.setVisibility ( View.VISIBLE );
                    //viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_right_arrow : R.drawable.ic_right_arrow );
                } else if(convertedObject instanceof EOLanguage) {
                    EOLanguage eoLanguage = (EOLanguage) convertedObject;
                    viewHolder.nameView.setText ( eoLanguage.getName ( ) );
                    //viewHolder.arrowView.setVisibility ( View.VISIBLE );
                    //viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_right_arrow : R.drawable.ic_right_arrow );
                } else if(convertedObject instanceof EOCurrency) {
                    EOCurrency eoCurrency = (EOCurrency) convertedObject;
                    viewHolder.nameView.setText ( eoCurrency.getIsoCode ( ) );
                    //viewHolder.arrowView.setVisibility ( View.VISIBLE );
                    //viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_right_arrow : R.drawable.ic_right_arrow );
                } else if(convertedObject instanceof String) {  //TODO this case is used to set data for string "HairPerfumes" & "Magazine"
                    viewHolder.nameView.setText ( (String) convertedObject );
                }
            }

            //TODO this case is used to set arrow on the basics of expandable item
//            if(itemInfo.isExpandable ( )) {
//                viewHolder.arrowView.setVisibility ( View.VISIBLE );
//                viewHolder.arrowView.setImageResource ( itemInfo.isExpanded ( ) ? R.drawable.ic_down_arrow : R.drawable.ic_right_arrow );
//            } else {
//                viewHolder.arrowView.setVisibility ( View.GONE );
//            }

            return convertView;
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener ( ) {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId ( )) {
                case R.id.navigation_home:
                    fragment = getSupportFragmentManager ( ).findFragmentByTag ( "dashboard" );
                    if(fragment != null && fragment.isVisible ( )) {
                        return false;
                    } else {
                        changeFragment ( new DashboardFragment ( ), "dashboard" );
                        return true;
                    }
                case R.id.navigation_account:
                    fragment = getSupportFragmentManager ( ).findFragmentByTag ( "account" );
                    if(fragment != null && fragment.isVisible ( )) {
                        return false;
                    } else {
                        changeFragment ( new AccountFragment ( ), "account" );
                        return true;
                    }
                case R.id.navigation_whatsapp:
                    fragment = getSupportFragmentManager ( ).findFragmentByTag ( "whatsapp" );
                    if(fragment != null && fragment.isVisible ( )) {
                        return false;
                    } else {
                        changeFragment ( new WhatsappFragment ( ), "whatsapp" );
                        return true;
                    }
                case R.id.navigation_search:
                    fragment = getSupportFragmentManager ( ).findFragmentByTag ( "search" );
                    if(fragment != null && fragment.isVisible ( )) {
                        return false;
                    } else {
                        changeFragment ( new SearchFragment ( ), "search" );
                        return true;
                    }
                case R.id.navigation_whishlist:
                    fragment = getSupportFragmentManager ( ).findFragmentByTag ( "whishlist" );
                    if(fragment != null && fragment.isVisible ( )) {
                        return false;
                    } else {
                        changeFragment ( new WhishlistFragment ( ), "whishlist" );
                        return true;
                    }
            }
            return false;
        }
    };

    private void changeFragment(Fragment fragment, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager ( ).beginTransaction ( );
        transaction.replace ( R.id.fragmentContainer, fragment, fragmentTag );
        transaction.addToBackStack ( null );
        transaction.commit ( );
    }

    //TODO this method is called from Account Fragment to selected wishlist tab
    public void wishlistTabIsSelected() {
        this.bottomNavigationView.setSelectedItemId ( R.id.navigation_whishlist );
    }

    @Override
    public void onBackPressed() {
        if(this.drawer.isDrawerOpen ( GravityCompat.START )) {
            this.drawer.closeDrawers ( );
            return;
        }

        if(bottomNavigationView.getSelectedItemId ( ) == R.id.navigation_home) {
            super.onBackPressed ( );
            finish ( );
        } else {
            bottomNavigationView.setSelectedItemId ( R.id.navigation_home );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
        if(drawer.isDrawerOpen ( Gravity.END )) {
            drawer.closeDrawer ( Gravity.END );
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.layout_free_shipping:
                //Toast.makeText ( this, "Clicked on free shipping", Toast.LENGTH_SHORT ).show ( );
                break;
            case R.id.layout_gift_with_every_product:
                //Toast.makeText ( this, "Clicked on gifts", Toast.LENGTH_SHORT ).show ( );
                break;
            case R.id.layout_original_products:
                //Toast.makeText ( this, "Clicked on 100% original", Toast.LENGTH_SHORT ).show ( );
                break;
            case R.id.layout_logout:
                //Toast.makeText ( this, "Clicked on logout button", Toast.LENGTH_SHORT ).show ( );
                break;
            case R.id.iv_back_arrow:
                hideKeyboard ( this );
                this.drawer.openDrawer ( Gravity.START );
                break;
            case R.id.layout_user_info:
                this.showUserInfo ( );
                break;
            case R.id.layout_welcome:
                this.showWelcomeUser ( );
                break;
        }
    }

    private void showUserInfo() {
        if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {

            if(drawer.isDrawerOpen ( GravityCompat.START )) {
                drawer.closeDrawers ( );
            }

            startActivity ( new Intent ( this, ProfileActivity.class ) );
        }
    }

    private void showWelcomeUser() {
        if(!loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && !loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                && !loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && !loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {

            if(drawer.isDrawerOpen ( GravityCompat.START )) {
                drawer.closeDrawers ( );
            }

            startActivity ( new Intent ( this, LoginActivity.class ) );
        }
    }

    @Override
    protected void onResume() {
        super.onResume ( );

        drawer.addDrawerListener ( new DrawerLayout.DrawerListener ( ) {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

                if(!ObjectUtil.isEmpty ( userInfoObject )){

                }

                //TODO check shared preference user email id exist or not
                if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                        && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) || loginSignupPreference.contains ( SELECTED_PROFILE_PIC )) {

                    layout_user_info.setVisibility ( View.VISIBLE );
                    layout_welcome.setVisibility ( View.GONE );

                    String firstName = loginSignupPreference.getString ( Constants.SELECTED_FIRST_NAME, "" );
                    String lastName = loginSignupPreference.getString ( Constants.SELECTED_LAST_NAME, "" );
                    String emailId = loginSignupPreference.getString ( Constants.SELECTED_EMAIL_ID, "" );
                    String profilePic = loginSignupPreference.getString ( SELECTED_PROFILE_PIC, "" );

                    if(!ObjectUtil.isEmpty ( profilePic ))
                        loadProfileImage ( profilePic, profileImage );
                    tv_profile_name.setText ( firstName.concat ( " " ).concat ( lastName ) );
                    tv_profile_email.setText ( emailId );

                } else {
                    layout_user_info.setVisibility ( View.GONE );
                    layout_welcome.setVisibility ( View.VISIBLE );
                }



            }
        } );

    }

    private void loadProfileImage(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( "https://bu-beunique.com/images/" + imagePath )
                .error ( R.drawable.ic_profile )
                .into ( imageView );
    }

    /**
     * Helper to hide the keyboard
     *
     * @param act
     */
    public void hideKeyboard(Activity act) {
        if(act != null && act.getCurrentFocus ( ) != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService ( Activity.INPUT_METHOD_SERVICE );
            inputMethodManager.hideSoftInputFromWindow ( act.getCurrentFocus ( ).getWindowToken ( ), 0 );
        }
    }
}
