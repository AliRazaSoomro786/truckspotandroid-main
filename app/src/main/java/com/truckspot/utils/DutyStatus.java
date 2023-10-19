package com.truckspot.utils;

import androidx.core.app.NotificationCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ConstUtils.kt */
public final class DutyStatus {
    public static final int $stable = 0;
    public static final String CERTIFY = "CERTIFY";
    public static final String DRIVE = "DRIVE";
    public static final String E_OFF = "E_OFF";
    public static final String E_ON = "E_ON";
    public static final DutyStatus INSTANCE = new DutyStatus();
    public static final String INT = "INT";
    public static final String INTERMEDIATE = "INTERMEDIATE";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String OFF_DUTY = "OFFDUTY";
    public static final String ON_DUTY = "ONDUTY";
    public static final String PERSONAL_USE = "PC";
    public static final String SLEEP = "SLEEP";
    public static final String YARD_MOVE = "YM";

    private DutyStatus() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005f A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean isEventsConsideredForINTLog(String r2) {
        /*
            r1 = this;
            java.lang.String r0 = "status"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            int r0 = r2.hashCode()
            switch(r0) {
                case -1959119563: goto L_0x0055;
                case -830856027: goto L_0x004c;
                case 2547: goto L_0x0043;
                case 2836: goto L_0x003a;
                case 72655: goto L_0x0031;
                case 65315178: goto L_0x0028;
                case 66631253: goto L_0x001f;
                case 78984887: goto L_0x0016;
                case 1457563897: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x005f
        L_0x000d:
            java.lang.String r0 = "INTERMEDIATE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x0016:
            java.lang.String r0 = "SLEEP"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x001f:
            java.lang.String r0 = "E_OFF"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x0028:
            java.lang.String r0 = "DRIVE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x0031:
            java.lang.String r0 = "INT"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x003a:
            java.lang.String r0 = "YM"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x0043:
            java.lang.String r0 = "PC"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x004c:
            java.lang.String r0 = "OFFDUTY"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005d
            goto L_0x005f
        L_0x0055:
            java.lang.String r0 = "ONDUTY"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x005f
        L_0x005d:
            r2 = 1
            goto L_0x0060
        L_0x005f:
            r2 = 0
        L_0x0060:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nationwideeld.android.utils.DutyStatus.isEventsConsideredForINTLog(java.lang.String):boolean");
    }

    public final boolean isValidToChangeToDrive(String str) {
        Intrinsics.checkNotNullParameter(str, NotificationCompat.CATEGORY_STATUS);
        int hashCode = str.hashCode();
        return hashCode == 2547 ? !str.equals(PERSONAL_USE) : !(hashCode == 2836 ? str.equals(YARD_MOVE) : hashCode == 72655 ? str.equals(INT) : !(hashCode == 65315178 ? !str.equals("DRIVE") : !(hashCode == 1457563897 && str.equals(INTERMEDIATE))));
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x004e A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean isAutomatedEvents(String r2) {
        /*
            r1 = this;
            java.lang.String r0 = "status"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            int r0 = r2.hashCode()
            switch(r0) {
                case -2043999862: goto L_0x0043;
                case 72655: goto L_0x003a;
                case 2149401: goto L_0x0031;
                case 65315178: goto L_0x0028;
                case 66631253: goto L_0x001f;
                case 72611657: goto L_0x0016;
                case 1457563897: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x004e
        L_0x000d:
            java.lang.String r0 = "INTERMEDIATE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x0016:
            java.lang.String r0 = "LOGIN"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x001f:
            java.lang.String r0 = "E_OFF"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x0028:
            java.lang.String r0 = "DRIVE"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x004e
            goto L_0x004c
        L_0x0031:
            java.lang.String r0 = "E_ON"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x003a:
            java.lang.String r0 = "INT"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x0043:
            java.lang.String r0 = "LOGOUT"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004c
            goto L_0x004e
        L_0x004c:
            r2 = 1
            goto L_0x004f
        L_0x004e:
            r2 = 0
        L_0x004f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nationwideeld.android.utils.DutyStatus.isAutomatedEvents(java.lang.String):boolean");
    }

    public final boolean isValidToChangeToInt(String str) {
        Intrinsics.checkNotNullParameter(str, NotificationCompat.CATEGORY_STATUS);
        int hashCode = str.hashCode();
        return hashCode == 72655 ? str.equals(INT) : !(hashCode == 65315178 ? !str.equals("DRIVE") : !(hashCode == 1457563897 && str.equals(INTERMEDIATE)));
    }

    public final boolean isValidToChangeToOnDuty(String str) {
        Intrinsics.checkNotNullParameter(str, NotificationCompat.CATEGORY_STATUS);
        int hashCode = str.hashCode();
        return hashCode == 72655 ? str.equals(INT) : !(hashCode == 65315178 ? !str.equals("DRIVE") : !(hashCode == 1457563897 && str.equals(INTERMEDIATE)));
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0044 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean isDriving(String r2) {
        return true;
        /*
            r1 = this;
            java.lang.String r0 = "status"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            int r0 = r2.hashCode()
            switch(r0) {
                case -1959119563: goto L_0x003a;
                case 2547: goto L_0x0031;
                case 2836: goto L_0x0028;
                case 72655: goto L_0x001f;
                case 65315178: goto L_0x0016;
                case 1457563897: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0044
        L_0x000d:
            java.lang.String r0 = "INTERMEDIATE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0042
            goto L_0x0044
        L_0x0016:
            java.lang.String r0 = "DRIVE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0042
            goto L_0x0044
        L_0x001f:
            java.lang.String r0 = "INT"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0042
            goto L_0x0044
        L_0x0028:
            java.lang.String r0 = "YM"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0042
            goto L_0x0044
        L_0x0031:
            java.lang.String r0 = "PC"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0042
            goto L_0x0044
        L_0x003a:
            java.lang.String r0 = "ONDUTY"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0044
        L_0x0042:
            r2 = 1
            goto L_0x0045
        L_0x0044:
            r2 = 0
        L_0x0045:
            return r2
        */
        //throw new UnsupportedOperationException("Method not decompiled: com.nationwideeld.android.utils.DutyStatus.isDriving(java.lang.String):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0045 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean isValidStatusToShowInDashboardButton(String r2) {
        return  true;

        /*
            r1 = this;
            java.lang.String r0 = "status"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
            int r0 = r2.hashCode()
            switch(r0) {
                case -1959119563: goto L_0x003a;
                case -830856027: goto L_0x0031;
                case 2547: goto L_0x0028;
                case 2836: goto L_0x001f;
                case 65315178: goto L_0x0016;
                case 78984887: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0045
        L_0x000d:
            java.lang.String r0 = "SLEEP"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0043
            goto L_0x0045
        L_0x0016:
            java.lang.String r0 = "DRIVE"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0043
            goto L_0x0045
        L_0x001f:
            java.lang.String r0 = "YM"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0043
            goto L_0x0045
        L_0x0028:
            java.lang.String r0 = "PC"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0043
            goto L_0x0045
        L_0x0031:
            java.lang.String r0 = "OFFDUTY"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0045
            goto L_0x0043
        L_0x003a:
            java.lang.String r0 = "ONDUTY"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0043
            goto L_0x0045
        L_0x0043:
            r2 = 1
            goto L_0x0046
        L_0x0045:
            r2 = 0
        L_0x0046:
            return r2
        */
      //  throw new UnsupportedOperationException("Method not decompiled: com.nationwideeld.android.utils.DutyStatus.isValidStatusToShowInDashboardButton(java.lang.String):boolean");
    }
}
