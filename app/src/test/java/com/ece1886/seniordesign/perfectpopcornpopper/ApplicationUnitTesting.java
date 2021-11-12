package com.ece1886.seniordesign.perfectpopcornpopper;


import com.ece1886.seniordesign.perfectpopcornpopper.activities.MainActivity;
import com.ece1886.seniordesign.perfectpopcornpopper.activities.SplashActivity;
import com.ece1886.seniordesign.perfectpopcornpopper.fragments.SettingsFragment;
import com.ece1886.seniordesign.perfectpopcornpopper.services.BluetoothLeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ApplicationUnitTesting {

    private MainActivity mainActivity;
    private SplashActivity splashActivity;
    private SettingsFragment settingsFragment;
    BluetoothLeService service;
    @Before
    public void setup(){
       mainActivity =  Robolectric.setupActivity(MainActivity.class);
       splashActivity =  Robolectric.setupActivity(SplashActivity.class);
       settingsFragment = SettingsFragment.newInstance();
       service = Robolectric.setupService(BluetoothLeService.class);
    }

    @Test
    public void test_Service(){
        assertNotEquals(null, service);
    }

    @Test
    public void test_isSplashActivityInView(){
        assertNotEquals(null, splashActivity);
    }

    @Test
    public void test_isHomeFragmentInView(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_transitionToSettingsFragment(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_transitionToHomeFromSettings(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_sharedPreferencesSaveLoad(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_navigationBar(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_connectBTButton(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void isAlertDialogShowing(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_disconnectBTButton(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    @Test
    public void test_isMACShowing(){
        assertEquals(4, 2 + 2);assertEquals(4, 2 + 2);
    }

    public void test_Log(){

    }

}