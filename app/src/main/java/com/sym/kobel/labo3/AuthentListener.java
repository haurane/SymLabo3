package com.sym.kobel.labo3;

import java.util.EventListener;

/**
 * Created by haurane on 12/1/15.
 */
public interface AuthentListener extends EventListener{
    void handleAuthentification (Integer level);
}
