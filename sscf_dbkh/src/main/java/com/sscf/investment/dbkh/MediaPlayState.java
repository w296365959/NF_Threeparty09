package com.sscf.investment.dbkh;

import java.util.Observable;
import java.util.Observer;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/8/13 17:24 <br/>
 * Summary:
 */
public class MediaPlayState extends Observable implements Observer {

    private PlayerSeekChangeListener lisenters;

    private String action;

    private String flagCode;

    public static MediaPlayState getInstance =new MediaPlayState();

    public   void seek(String flagCode, String action) {
        getInstance.flagCode = flagCode;
        getInstance.action = action;
        getInstance.setChanged();
        getInstance.notifyObservers();
    }

    public void addPlayerSeekChangeListener(PlayerSeekChangeListener listener) {
        this.lisenters = listener;
    }

    @Override
    public void update(Observable observable, Object o) {
        MediaPlayState state = (MediaPlayState) observable;
        lisenters.onPlayerSeekChange(state.flagCode, state.action);
    }

    public interface PlayerSeekChangeListener {
        void onPlayerSeekChange(String code, String action);
    }
}
