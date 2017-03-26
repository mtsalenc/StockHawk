package com.udacity.stockhawk;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rigel on 26/03/17.
 */

class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {


    private static final String TAG = "WidgetDataProvider";
    private static final String ACTION_APP_OPEN = "open";
    List<String> mCollection = new ArrayList<>();

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;


    Context context = null;
    Cursor cursor = null;

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(cursor == null)
            return 0;

        return cursor.getCount();

//        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.list_item_quote);
        cursor.moveToPosition(position);

        String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        String position_price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));
        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        int pillDrawable;
        if (rawAbsoluteChange > 0)
            pillDrawable = R.drawable.percent_change_pill_green;
        else
            pillDrawable = R.drawable.percent_change_pill_red;

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);
        String displayMode = PrefUtils.getDisplayMode(context);

        view.setTextViewText(R.id.symbol,symbol);
        view.setTextViewText(R.id.price,position_price);
        view.setTextViewText(R.id.change,
                (displayMode.equals(context.getString(R.string.pref_display_mode_absolute_key)))?
                        change:percentage
        );
        view.setInt(R.id.change,"setBackgroundResource",pillDrawable);


        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        cursor = context.getContentResolver().query(Contract.Quote.URI,null,null,null,null);

//        mCollection.clear();
//        for (int i = 1; i <= 10; i++) {
//            mCollection.add("ListView item " + i);
//        }
    }
}
