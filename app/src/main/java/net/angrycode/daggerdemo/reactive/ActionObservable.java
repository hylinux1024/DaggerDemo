package net.angrycode.daggerdemo.reactive;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Observable to character selected, this observable call all observer subscribed when a
 * character is selected
 *
 * @author glomadrian
 */
public class ActionObservable implements Observable<ActionObserver> {

    List<ActionObserver> actionObservers;

    @Inject
    public ActionObservable() {
        actionObservers = new ArrayList<ActionObserver>();
    }


    @Override
    public void register(ActionObserver observer) {
        //To avoid duplicated register
        if (!actionObservers.contains(observer)) {
            actionObservers.add(observer);
        }
    }

    @Override
    public void unregister(ActionObserver observer) {
        actionObservers.remove(observer);
    }


    public void notifyObservers(String actionDesc) {
        for (ActionObserver actionObserver : actionObservers) {
            actionObserver.action(actionDesc);
        }
    }
}
