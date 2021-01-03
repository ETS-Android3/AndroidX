package com.androidx.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

/**
 * TV - Activity
 */
public class TVCoreActivity extends CoreActivity {


    @Override
    protected int getContentViewResId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    /**
     * Enter键按下
     */
    protected void onPressedEnter() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedEnter();
        }
    }

    /**
     * Back按下
     */
    protected void onPressedBack() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedBack();
        }
    }

    /**
     * 按下设置
     */
    protected void onPressedSetting() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedSetting();
        }
    }

    /**
     * 按下向下
     */
    protected void onPressedDown() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedDown();
        }
    }

    /**
     * 按下向上
     */
    protected void onPressedUp() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedUp();
        }
    }

    /**
     * 按下向左
     */
    protected void onPressedLeft() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedLeft();
        }
    }

    /**
     * 按下向右
     */
    protected void onPressedRight() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedRight();
        }
    }

    /**
     * 按下信息按键
     */
    protected void onPressedInfo() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedInfo();
        }
    }

    /**
     * 按下翻页 - 下
     */
    protected void onPressedPageDown() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedPageDown();
        }
    }

    /**
     * 按下翻页 - 上
     */
    protected void onPressedPageUp() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedPageUp();
        }
    }

    /**
     * 按下音量 -
     */
    protected void onPressedVolumeUp() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedVolumeUp();
        }
    }

    /**
     * 按下音量+
     */
    protected void onPressedVolumeDown() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedVolumeDown();
        }
    }

    /**
     * 按下禁用声音
     */
    protected void onPressedVolumeMute() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedVolumeMute();
        }
    }


    /**
     * 按下Home键
     */
    protected void onPressedHome() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedHome();
        }
    }

    /**
     * 按下菜单键
     */
    protected void onPressedMenu() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedMenu();
        }
    }

    /**
     * 按下播放暂停键
     */
    protected void onPressedPlayPause() {
        TVCoreFragment fgt = (TVCoreFragment) getStackTopFragment();
        if (fgt != null) {
            fgt.onPressedPlayPause();
        }
    }


    private long currentTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (System.currentTimeMillis() - currentTime < 200) {
            currentTime = System.currentTimeMillis();
            return true;
        }
        currentTime = System.currentTimeMillis();
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER://确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    onPressedEnter();
                }
                break;
            case KeyEvent.KEYCODE_BACK://返回键
                onPressedBack();
                return true;//这里由于break会退出，所以我们自己要处理掉 不返回上一层
            case KeyEvent.KEYCODE_SETTINGS: //设置键
                onPressedSetting();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键（ 实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发 exp:KeyEvent.ACTION_UP）
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    onPressedDown();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP://向上键
                onPressedUp();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                onPressedLeft();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                onPressedRight();
                break;
            case KeyEvent.KEYCODE_INFO:    //info键
                onPressedInfo();
                break;
            case KeyEvent.KEYCODE_PAGE_DOWN://向上翻页键
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                onPressedPageDown();
                break;
            case KeyEvent.KEYCODE_PAGE_UP:     //向下翻页键
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                onPressedPageUp();
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键
                onPressedVolumeUp();
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键
                onPressedVolumeDown();
                break;
            case KeyEvent.KEYCODE_VOLUME_MUTE: //禁用声音
                onPressedVolumeMute();
                break;
            case KeyEvent.KEYCODE_HOME: //Home
                onPressedHome();
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: //Play Pause
                onPressedPlayPause();
                break;
            case KeyEvent.KEYCODE_MENU: //Menu
                onPressedMenu();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}
