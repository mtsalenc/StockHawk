package com.udacity.stockhawk;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by rigel on 26/03/17.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}

