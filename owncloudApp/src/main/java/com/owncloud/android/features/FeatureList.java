/**
 * ownCloud Android client application
 *
 * @author Bartosz Przybylski
 * @author Christian Schabesberger
 * Copyright (C) 2019 Bartosz Przybylski
 * Copyright (C) 2019 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.features;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.owncloud.android.MainApp;
import com.owncloud.android.R;
import com.owncloud.android.lib.common.utils.Log_OC;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Bartosz Przybylski
 */
public class FeatureList {

    static final private boolean SHOW_ON_FIRST_RUN = true;
    static final private boolean SHOW_ON_UPGRADE = false;

    private static final String TAG = FeatureList.class.getSimpleName();

    static final private FeatureItem featuresList[] = {
            // Basic features showed on first install
            new FeatureItem(R.drawable.whats_new_files, R.string.welcome_feature_1_title,
                    R.string.welcome_feature_1_text, "2.7.0", "0", SHOW_ON_FIRST_RUN, false, false),
            new FeatureItem(R.drawable.whats_new_share, R.string.welcome_feature_2_title,
                    R.string.welcome_feature_2_text, "2.7.0", "0", SHOW_ON_FIRST_RUN, false, false),
            new FeatureItem(R.drawable.whats_new_accounts, R.string.welcome_feature_3_title,
                    R.string.welcome_feature_3_text, "2.7.0", "0", SHOW_ON_FIRST_RUN, false, false),
            new FeatureItem(R.drawable.whats_new_camera_uploads, R.string.welcome_feature_4_title,
                    R.string.welcome_feature_4_text, "2.7.0", "0", SHOW_ON_FIRST_RUN, false, false),
            new FeatureItem(R.drawable.whats_new_video_streaming, R.string.welcome_feature_5_title,
                    R.string.welcome_feature_5_text, "2.7.0", "0", SHOW_ON_FIRST_RUN, false, false),

            // Features introduced in 2.14.0
            new FeatureItem(R.drawable.whats_new_no_more_10, R.string.welcome_feature_11_title,
                    R.string.welcome_feature_11_text, "2.14.1", "0", SHOW_ON_UPGRADE, true, false),
            new FeatureItem(R.drawable.whats_new_no_more_4_4, R.string.welcome_feature_12_title,
                    R.string.welcome_feature_12_text, "2.14.1", "0", SHOW_ON_UPGRADE, false, true)
    };

    static public FeatureItem[] get() {
        return featuresList;
    }

    static public FeatureItem[] getFiltered(final int lastSeenVersionCode, final boolean isFirstRun, boolean isBeta,
                                            boolean anyAccountWithServerVersionLowerThan10) {
        List<FeatureItem> features = new LinkedList<>();

        Log_OC.d(TAG, "Getting filtered features");

        for (FeatureItem item : get()) {
            final int itemVersionCode = isBeta ? item.getBetaVersionNumber() : item.getVersionNumber();
            if (isFirstRun && item.shouldShowOnFirstRun()) {
                features.add(item);
            } else if (!isFirstRun && !item.shouldShowOnFirstRun() &&
                    MainApp.Companion.getVersionCode() >= itemVersionCode &&
                    lastSeenVersionCode < itemVersionCode) {
                if (anyAccountWithServerVersionLowerThan10 && item.shouldShowOnlyInAccountsWithServersLowerThan10()) {
                    features.add(item);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && item.shouldShowOnlyInDevicesLowerThan5()) {
                    features.add(item);
                } else if (!item.shouldShowOnlyInAccountsWithServersLowerThan10() && !item.shouldShowOnlyInDevicesLowerThan5()) {
                    features.add(item);
                }
            }
        }
        return features.toArray(new FeatureItem[features.size()]);
    }

    static public class FeatureItem implements Parcelable {
        private static final int DO_NOT_SHOW = -1;
        private int image;
        private int titleText;
        private int contentText;
        private int versionNumber;
        private int betaVersion;
        private boolean showOnInitialRun;
        private boolean showOnlyInAccountsWithServersLowerThan10;
        private boolean shouldShowOnlyInDevicesLowerThan5;

        private FeatureItem(int image, int titleText, int contentText, String version, String betaVersion,
                            boolean showOnInitialRun, boolean showOnlyInAccountsWithServersLowerThan10,
                            boolean shouldShowOnlyInDevicesLowerThan5) {
            this.image = image;
            this.titleText = titleText;
            this.contentText = contentText;
            this.versionNumber = versionCodeFromString(version);
            this.betaVersion = versionCodeFromString(betaVersion);
            this.showOnInitialRun = showOnInitialRun;
            this.showOnlyInAccountsWithServersLowerThan10 = showOnlyInAccountsWithServersLowerThan10;
            this.shouldShowOnlyInDevicesLowerThan5 = shouldShowOnlyInDevicesLowerThan5;
        }

        public boolean shouldShowImage() {
            return image != DO_NOT_SHOW;
        }

        public int getImage() {
            return image;
        }

        public boolean shouldShowTitleText() {
            return titleText != DO_NOT_SHOW;
        }

        public int getTitleText() {
            return titleText;
        }

        public boolean shouldShowContentText() {
            return contentText != DO_NOT_SHOW;
        }

        public int getContentText() {
            return contentText;
        }

        private int getVersionNumber() {
            return versionNumber;
        }

        private int getBetaVersionNumber() {
            return betaVersion;
        }

        private boolean shouldShowOnFirstRun() {
            return showOnInitialRun;
        }

        private boolean shouldShowOnlyInAccountsWithServersLowerThan10() {
            return showOnlyInAccountsWithServersLowerThan10;
        }

        private boolean shouldShowOnlyInDevicesLowerThan5() {
            return shouldShowOnlyInDevicesLowerThan5;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(image);
            dest.writeInt(titleText);
            dest.writeInt(contentText);
            dest.writeInt(versionNumber);
            dest.writeInt(betaVersion);
            dest.writeByte((byte) (showOnInitialRun ? 1 : 0));
        }

        private FeatureItem(Parcel p) {
            image = p.readInt();
            titleText = p.readInt();
            contentText = p.readInt();
            versionNumber = p.readInt();
            betaVersion = p.readInt();
            showOnInitialRun = p.readByte() == 1;
        }

        public static final Parcelable.Creator CREATOR =
                new Parcelable.Creator() {

                    @Override
                    public Object createFromParcel(Parcel source) {
                        return new FeatureItem(source);
                    }

                    @Override
                    public Object[] newArray(int size) {
                        return new FeatureItem[size];
                    }
                };
    }

    static int versionCodeFromString(String version) {
        String v[] = version.split(Pattern.quote("."));
        if (v.length != 3) {
            Log_OC.d(TAG, "Version string is incorrect " + version);
            return 0;
        }
        int result = Integer.parseInt(v[0]) * (int) (10e6) +
                Integer.parseInt(v[1]) * (int) (10e4) +
                Integer.parseInt(v[2]) * 100;

        return result;
    }
}