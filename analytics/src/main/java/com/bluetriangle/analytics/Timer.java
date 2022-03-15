package com.bluetriangle.analytics;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * A timer instance that can be started, marked interactive, and ended.
 * <p>
 * Timers maintain the start, interactive, and end times in milliseconds. They also maintain a map of attributes for the
 * timer such as page name, brand value, campaign name, AB test, etc.
 * <p>
 * Additional attributes beyond the ones defined below can be set on a timer as well.
 */
public class Timer implements Parcelable {
    private static final String LOG_TAG = "BTT_TIMER";

    public static final String EXTRA_TIMER = "BTT_TIMER";

    public static final String FIELD_PAGE_NAME = "pageName";
    public static final String FIELD_NST = "nst";
    public static final String FIELD_UNLOAD_EVENT_START = "unloadEventStart";
    public static final String FIELD_CONTENT_GROUP_NAME = "pageType";
    public static final String FIELD_PAGE_VALUE = "pageValue";
    public static final String FIELD_PAGE_TIME = "pgTm";
    public static final String FIELD_DOM_INTERACTIVE = "domInteractive";
    public static final String FIELD_NAVIGATION_TYPE = "navigationType";
    public static final String FIELD_CART_VALUE = "cartValue";
    public static final String FIELD_ORDER_NUMBER = "ONumBr";
    public static final String FIELD_ORDER_TIME = "orderTND";
    public static final String FIELD_EVENT_TYPE = "eventType";
    public static final String FIELD_SITE_ID = "siteID";
    public static final String FIELD_TRAFFIC_SEGMENT_NAME = "txnName";
    public static final String FIELD_CAMPAIGN = "campaign";
    public static final String FIELD_TIME_ON_PAGE = "top";
    public static final String FIELD_BRAND_VALUE = "bv";
    public static final String FIELD_URL = "thisURL";
    public static final String FIELD_BVZN = "bvzn";
    public static final String FIELD_OS = "EUOS";
    public static final String FIELD_SESSION_ID = "sID";
    public static final String FIELD_GLOBAL_USER_ID = "gID";
    public static final String FIELD_CUSTOM_VALUE_4 = "CV4";
    public static final String FIELD_RV = "RV";
    public static final String FIELD_WCD = "wcd";
    public static final String FIELD_AB_TEST_ID = "AB";
    public static final String FIELD_CAMPAIGN_SOURCE = "CmpS";
    public static final String FIELD_CAMPAIGN_MEDIUM = "CmpM";
    public static final String FIELD_CAMPAIGN_NAME = "CmpN";
    public static final String FIELD_DATACENTER = "DCTR";
    public static final String FIELD_REFERRER_URL = "RefURL";
    public static final String FIELD_BROWSER = "browser";
    public static final String FIELD_NATIVE_OS = "os";
    public static final String FIELD_DEVICE = "device";
    public static final String FIELD_BROWSER_VERSION = "browserVersion";

    /**
     * A map of fields and their associated default values
     */
    private static final Map<String, String> DEFAULT_VALUES;

    static {
        DEFAULT_VALUES = new HashMap<>(40);
        DEFAULT_VALUES.put(FIELD_BVZN, "");
        DEFAULT_VALUES.put(FIELD_OS, "Android");
        DEFAULT_VALUES.put(FIELD_NATIVE_OS, "Android");
        DEFAULT_VALUES.put(FIELD_EVENT_TYPE, "9");
        DEFAULT_VALUES.put(FIELD_NAVIGATION_TYPE, "9");
        DEFAULT_VALUES.put(FIELD_RV, "0");
        DEFAULT_VALUES.put(FIELD_CUSTOM_VALUE_4, "0");
        DEFAULT_VALUES.put(FIELD_WCD, "0");
        DEFAULT_VALUES.put(FIELD_DATACENTER, "Default");
        DEFAULT_VALUES.put(FIELD_AB_TEST_ID, "Default");
        DEFAULT_VALUES.put(FIELD_BRAND_VALUE, "0");
        DEFAULT_VALUES.put(FIELD_TIME_ON_PAGE, "0");
        DEFAULT_VALUES.put(FIELD_CAMPAIGN, "");
        DEFAULT_VALUES.put(FIELD_CAMPAIGN_NAME, "");
        DEFAULT_VALUES.put(FIELD_CAMPAIGN_MEDIUM, "");
        DEFAULT_VALUES.put(FIELD_CAMPAIGN_SOURCE, "");
        DEFAULT_VALUES.put(FIELD_REFERRER_URL, "");
        DEFAULT_VALUES.put(FIELD_URL, "");
        DEFAULT_VALUES.put(FIELD_CART_VALUE, "0");
        DEFAULT_VALUES.put(FIELD_ORDER_TIME, "0");
        DEFAULT_VALUES.put(FIELD_ORDER_NUMBER, "");
        DEFAULT_VALUES.put(FIELD_PAGE_VALUE, "0");
        DEFAULT_VALUES.put(FIELD_CONTENT_GROUP_NAME, "");
    }

    /**
     * A map of all fields for this timer to be sent to the cloud server
     */
    private final Map<String, String> fields;

    /**
     * Start time in milliseconds
     */
    private long start;

    /**
     * The current time when the interactive call was made in milliseconds
     */
    private long interactive;

    /**
     * The time in milliseconds this timer was ended
     */
    private long end;

    /**
     * Create a timer instance with no page name or traffic segment name. These will need to be set later before
     * submitting the timer.
     */
    public Timer() {
        super();
        fields = new HashMap<>(DEFAULT_VALUES);
    }

    /**
     * Create a timer instance with the given page name and traffic segment name
     *
     * @param pageName           page name
     * @param trafficSegmentName traffic segment name
     */
    public Timer(@Nullable final String pageName, @Nullable final String trafficSegmentName) {
        this();
        if (pageName != null) {
            fields.put(FIELD_PAGE_NAME, pageName);
        }
        if (trafficSegmentName != null) {
            fields.put(FIELD_TRAFFIC_SEGMENT_NAME, trafficSegmentName);
        }
    }

    /**
     * Create a timer instance with the given page name, traffic segment name, optional AB test id, and optional content
     * group name.
     *
     * @param pageName           page name
     * @param trafficSegmentName traffic segment name
     * @param abTestIdentifier   AB Test ID/Name
     * @param contentGroupName   Content Group Name
     */
    public Timer(@Nullable final String pageName, @Nullable final String trafficSegmentName, @Nullable final String abTestIdentifier, @Nullable final String contentGroupName) {
        this(pageName, trafficSegmentName);
        if (abTestIdentifier != null) {
            fields.put(FIELD_AB_TEST_ID, abTestIdentifier);
        }
        if (contentGroupName != null) {
            fields.put(FIELD_CONTENT_GROUP_NAME, contentGroupName);
        }
    }

    /**
     * Start this timer if not already started. If already started, will log an error.
     *
     * @return this timer
     */
    synchronized public Timer start() {
        if (start == 0) {
            start = System.currentTimeMillis();
        } else {
            Log.e(LOG_TAG, "Timer already started");
        }
        return this;
    }

    /**
     * Mark this timer interactive at current time if the timer has been started and not already marked interactive. If
     * the timer has not been started yet, log an error. If the timer has already been marked interactive, log an
     * error.
     *
     * @return this timer
     */
    synchronized public Timer interactive() {
        if (start > 0 && interactive == 0) {
            interactive = System.currentTimeMillis();
            setField(FIELD_DOM_INTERACTIVE, interactive);
        } else {
            if (start == 0) {
                Log.e(LOG_TAG, "Timer never started");
            } else if (interactive != 0) {
                Log.e(LOG_TAG, "Timer already marked as interactive");
            }
        }
        return this;
    }

    /**
     * End this timer.
     *
     * @return this timer.
     */
    synchronized public Timer end() {
        if (start > 0 && end == 0) {
            end = System.currentTimeMillis();
            setField(FIELD_UNLOAD_EVENT_START, start);
            setField(FIELD_NST, start);
            setField(FIELD_PAGE_TIME, end - start);
        } else {
            if (start == 0) {
                Log.e(LOG_TAG, "Timer never started");
            } else if (end != 0) {
                Log.e(LOG_TAG, "Timer already ended");
            }
        }

        return this;
    }

    /**
     * Determines if this timer is currently running
     *
     * @return True if timer has started but not yet ended.
     */
    public boolean isRunning() {
        return start > 0 && end == 0;
    }

    /**
     * Determines if this timer has been ended
     *
     * @return True if ended, else false
     */
    public boolean hasEnded() {
        return start > 0 && end > 0;
    }

    /**
     * Determines if this timer has been marked as interactive yet
     *
     * @return True if marked interactive, else false
     */
    public boolean isInteractive() {
        return start > 0 && interactive > 0;
    }

    /**
     * Convenience method to submit this timer to the global tracker
     */
    public void submit() {
        final Tracker tracker = Tracker.getInstance();
        if (tracker != null) {
            tracker.submitTimer(this);
        } else {
            Log.w(LOG_TAG, "Tracker not initialized");
        }
    }

    /**
     * Get all the fields currently associated with this timer.
     *
     * @return returns a new hash map with all the fields currently.
     */
    public Map<String, String> getFields() {
        HashMap<String, String> fieldsCopy;
        synchronized (fields) {
            fieldsCopy = new HashMap<>(fields);
        }
        return fieldsCopy;
    }

    /**
     * Set the timer's page name
     *
     * @param pageName name of the page for this timer
     * @return this timer
     */
    public Timer setPageName(@NonNull final String pageName) {
        return setField(FIELD_PAGE_NAME, pageName);
    }

    /**
     * Set the timer's traffic segment name
     *
     * @param trafficSegmentName name of the traffic segment for this timer
     * @return this timer
     */
    public Timer setTrafficSegmentName(@NonNull final String trafficSegmentName) {
        return setField(FIELD_TRAFFIC_SEGMENT_NAME, trafficSegmentName);
    }

    /**
     * Set this timer's AB test identifier
     *
     * @param abTestIdentifier the AB test id
     * @return this timer
     */
    public Timer setAbTestIdentifier(@NonNull final String abTestIdentifier) {
        return setField(FIELD_AB_TEST_ID, abTestIdentifier);
    }

    /**
     * Set the content group name or page type for this timer
     *
     * @param contentGroupName name of content group or page type
     * @return this timer
     */
    public Timer setContentGroupName(@NonNull final String contentGroupName) {
        return setField(FIELD_CONTENT_GROUP_NAME, contentGroupName);
    }

    /**
     * Set the value of this page/timer
     *
     * @param pageValue value of page
     * @return this timer
     */
    public Timer setPageValue(final double pageValue) {
        return setField(FIELD_PAGE_VALUE, pageValue);
    }

    /**
     * Set the brand value of this timer
     *
     * @param brandValue brand's value
     * @return this timer
     */
    public Timer setBrandValue(final double brandValue) {
        return setField(FIELD_BRAND_VALUE, brandValue);
    }

    /**
     * Set the value of the cart for this timer
     *
     * @param cartValue value of cart
     * @return this timer
     */
    public Timer setCartValue(final double cartValue) {
        return setField(FIELD_CART_VALUE, cartValue);
    }

    /**
     * Set the order number for this timer
     *
     * @param orderNumber order number
     * @return this timer
     */
    public Timer setOrderNumber(@NonNull final String orderNumber) {
        return setField(FIELD_ORDER_NUMBER, orderNumber);
    }

    /**
     * Set the time of the order
     *
     * @param orderTime epoch time of order in milliseconds
     * @return this timer
     */
    public Timer setOrderTime(final long orderTime) {
        return setField(FIELD_ORDER_TIME, orderTime);
    }

    /**
     * Set the name of the campaign
     *
     * @param campaignName name of campaign
     * @return this timer
     */
    public Timer setCampaignName(@NonNull final String campaignName) {
        return setField(FIELD_CAMPAIGN_NAME, campaignName);
    }

    /**
     * Set the source of the campaign
     *
     * @param campaignSource source of campaign
     * @return this timer
     */
    public Timer setCampaignSource(@NonNull final String campaignSource) {
        return setField(FIELD_CAMPAIGN_SOURCE, campaignSource);
    }

    /**
     * Set the medium of the campaign
     *
     * @param campaignMedium medium of campaign
     * @return this timer
     */
    public Timer setCampaignMedium(@NonNull final String campaignMedium) {
        return setField(FIELD_CAMPAIGN_MEDIUM, campaignMedium);
    }

    /**
     * Set campaign details
     *
     * @param campaignName   name of campaign
     * @param campaignSource source of campaign
     * @param campaignMedium medium of campaign
     * @return this timer
     */
    public Timer setCampaign(@NonNull final String campaignName, @NonNull final String campaignSource, @NonNull final String campaignMedium) {
        setCampaignName(campaignName);
        setCampaignMedium(campaignMedium);
        setCampaignSource(campaignSource);
        return this;
    }

    /**
     * Set time on page for this timer
     *
     * @param timeOnPage time on page in milliseconds
     * @return this timer
     */
    public Timer setTimeOnPage(final long timeOnPage) {
        return setField(FIELD_TIME_ON_PAGE, timeOnPage);
    }

    /**
     * Set the URL for this timer
     *
     * @param url the url for this timer
     * @return this timer
     */
    public Timer setUrl(@NonNull final String url) {
        return setField(FIELD_URL, url);
    }

    /**
     * Set the referrer URL for this timer
     *
     * @param referrer the referrer URL
     * @return this timer
     */
    public Timer setReferrer(@NonNull final String referrer) {
        return setField(FIELD_REFERRER_URL, referrer);
    }

    /**
     * Allows the setting of multiple fields via a Map
     *
     * @param fields A map of attribute names as keys and their associated string value to set
     * @return this timer
     */
    public Timer setFields(@NonNull final Map<String, String> fields) {
        synchronized (this.fields) {
            this.fields.putAll(fields);
        }
        return this;
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, @NonNull final String value) {
        synchronized (fields) {
            fields.put(fieldName, value);
        }
        return this;
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, final int value) {
        return setField(fieldName, Integer.toString(value));
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, final double value) {
        return setField(fieldName, Double.toString(value));
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, final float value) {
        return setField(fieldName, Float.toString(value));
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, final boolean value) {
        return setField(fieldName, Boolean.toString(value));
    }

    /**
     * Sets a field name with the given value
     *
     * @param fieldName the name of the field to set
     * @param value     the value to set for the given field
     * @return this timer
     */
    public Timer setField(@NonNull final String fieldName, final long value) {
        return setField(fieldName, Long.toString(value));
    }

    /**
     * Get the current value for the given field
     *
     * @param fieldName the name of the field to get
     * @return the current value for the given field or null if not set
     */
    public String getField(@NonNull final String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Get the current value for the given field or default if not set
     *
     * @param fieldName the name of the field to get
     * @param
     * @return the current value for the given field or null if not set
     */
    public String getField(@NonNull final String fieldName, @NonNull final String defaultValue) {
        final String fieldValue = fields.get(fieldName);
        if (fieldValue == null) {
            return defaultValue;
        }
        return fieldValue;
    }

    /**
     * Resets a field to the default value if there is one else removes the field completely.
     *
     * @param fieldName name of field to remove
     * @return this timer
     */
    public Timer clearField(@NonNull final String fieldName) {
        synchronized (fields) {
            if (DEFAULT_VALUES.containsKey(fieldName)) {
                fields.put(fieldName, DEFAULT_VALUES.get(fieldName));
            } else {
                fields.remove(fieldName);
            }
        }
        return this;
    }

    private Timer(Parcel in) {
        start = in.readLong();
        interactive = in.readLong();
        end = in.readLong();
        final int size = in.readInt();
        fields = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final String key = in.readString();
            final String value = in.readString();
            fields.put(key, value);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(start);
        dest.writeLong(interactive);
        dest.writeLong(end);
        dest.writeInt(fields.size());
        for (final Map.Entry<String, String> entry : fields.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Timer> CREATOR = new Parcelable.Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    @Override
    public String toString() {
        return String.format("BTT Timer: %s", getField(FIELD_PAGE_NAME));
    }
}
