package com.truckspot.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.nationwideeld.android.ui.others.ELDGraph */
/* compiled from: ELDGraph.kt */
public final class ELDGraph extends View {
    public static final int $stable = 8;
    private Canvas canvas;
    private List<? extends ELDGraphData> eldGraphDataList;
    private final Paint graphPlotPaint;
    private final int numColumns;
    private final int numRows;
    private final Paint whitePaint;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ELDGraph(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        Paint paint = new Paint();
        this.whitePaint = paint;
        Paint paint2 = new Paint();
        this.graphPlotPaint = paint2;
        this.numColumns = 24;
        this.numRows = 4;
        this.eldGraphDataList = new ArrayList();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(-1);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1.0f);
        paint2.setStrokeWidth(2.0f);
        paint2.setColor(Color.parseColor("#05ff50"));
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ELDGraph(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    public final void plotGraph(List<? extends ELDGraphData> list) {
        Intrinsics.checkNotNullParameter(list, "eldGraphDataList");
        this.eldGraphDataList = list;
    }

    private final void changeDutyStatus(int i, int i2, float f) {
//        Timber.Forest forest = Timber.Forest;
//        forest.mo46574i("change duty -> " + i + ' ' + i2 + ' ' + f, new Object[0]);
        Canvas canvas2 = this.canvas;
        if (canvas2 != null) {
            canvas2.drawLine((((float) getWidth()) * f) / ((float) this.numColumns), (float) (((double) ((i * getHeight()) / this.numRows)) - ((((double) getHeight()) * 0.5d) / ((double) this.numRows))), (f * ((float) getWidth())) / ((float) this.numColumns), (float) (((double) ((i2 * getHeight()) / this.numRows)) - ((((double) getHeight()) * 0.5d) / ((double) this.numRows))), this.graphPlotPaint);
        }
    }

    private final void drawStatusLine(float f, float f2, int i) {
//        Timber.Forest forest = Timber.Forest;
//        forest.mo46574i("draw line -> " + f + ' ' + f2 + ' ' + i, new Object[0]);
        Canvas canvas2 = this.canvas;
        if (canvas2 != null) {
            canvas2.drawLine((f * ((float) getWidth())) / ((float) this.numColumns), (float) (((double) ((getHeight() * i) / this.numRows)) - ((((double) getHeight()) * 0.5d) / ((double) this.numRows))), (f2 * ((float) getWidth())) / ((float) this.numColumns), (float) (((double) ((i * getHeight()) / this.numRows)) - ((((double) getHeight()) * 0.5d) / ((double) this.numRows))), this.graphPlotPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas2) {
        int i;
        int i2;
        Canvas canvas3 = canvas2;
        super.onDraw(canvas2);
        this.canvas = canvas3;
        if (canvas3 != null) {
            canvas3.drawColor(ViewCompat.MEASURED_STATE_MASK);
        }
        if (canvas3 != null) {
            canvas2.drawLine(0.0f, 0.0f, (float) getWidth(), 0.0f, this.whitePaint);
        }
        if (canvas3 != null) {
            canvas2.drawLine(0.0f, 0.0f, 0.0f, (float) getHeight(), this.whitePaint);
        }
        if (canvas3 != null) {
            canvas2.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1),
                    this.whitePaint);
        }
        if (canvas3 != null) {
            canvas2.drawLine((float) (getWidth() - 1), (float) getHeight(), (float) (getWidth() - 1), 0.0f,
                    this.whitePaint);
        }
        int i3 = this.numRows;
        int i4 = 0;
        for (int i5 = 0; i5 < i3; i5++) {
            if (canvas3 != null) {
                canvas2.drawLine(0.0f, (float) ((getHeight() * i5) / this.numRows), (float) getWidth(),
                        (float) ((getHeight() * i5) / this.numRows), this.whitePaint);
            }
        }
        int i6 = this.numColumns;
        for (int i7 = 0; i7 < i6; i7++) {
            if (canvas3 != null) {
                canvas2.drawLine((float) ((getWidth() * i7) / this.numColumns), 0.0f, (float) ((getWidth() * i7) / this.numColumns), (float) getHeight(), this.whitePaint);
            }
        }
        int i8 = this.numRows;
        if (i8 >= 0) {
            int i9 = 0;
            while (true) {
                int i10 = this.numColumns;
                if (i10 >= 0) {
                    int i11 = 0;
                    while (true) {
                        if (canvas3 != null) {
                            i2 = i8;
                            canvas2.drawLine((float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.5d) / ((double) this.numColumns))), (float) ((getHeight() * i9) / this.numRows), (float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.5d) / ((double) this.numColumns))), (float) (((double) ((getHeight() * i9) / this.numRows)) - ((((double) getHeight()) * 0.5d) / ((double) this.numRows))), this.whitePaint);
                        } else {
                            i2 = i8;
                        }
                        if (canvas3 != null) {
                            canvas2.drawLine((float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.25d) / ((double) this.numColumns))), (float) ((getHeight() * i9) / this.numRows), (float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.25d) / ((double) this.numColumns))), (float) (((double) ((getHeight() * i9) / this.numRows)) - ((((double) getHeight()) * 0.25d) / ((double) this.numRows))), this.whitePaint);
                        }
                        if (canvas3 != null) {
                            canvas2.drawLine((float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.75d) / ((double) this.numColumns))), (float) ((getHeight() * i9) / this.numRows), (float) (((double) ((getWidth() * i11) / this.numColumns)) + ((((double) getWidth()) * 0.75d) / ((double) this.numColumns))), (float) (((double) ((getHeight() * i9) / this.numRows)) - ((((double) getHeight()) * 0.25d) / ((double) this.numRows))), this.whitePaint);
                        }
                        if (i11 == i10) {
                            break;
                        }
                        i11++;
                        i8 = i2;
                    }
                    i = i2;
                } else {
                    i = i8;
                }
                if (i9 == i) {
                    break;
                }
                i9++;
                i8 = i;
            }
        }
        Collection arrayList = new ArrayList();
        for (Object next : this.eldGraphDataList) {
            ELDGraphData eLDGraphData = (ELDGraphData) next;
            if (!Intrinsics.areEqual((Object) eLDGraphData.getStatus(), (Object) DutyStatus.E_ON) && !Intrinsics.areEqual((Object) eLDGraphData.getStatus(), (Object) DutyStatus.E_OFF)) {
                arrayList.add(next);
            }
        }
        for (Object next2 : (List) arrayList) {
            int i12 = i4 + 1;
            if (i4 < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            ELDGraphData eLDGraphData2 = (ELDGraphData) next2;
            if (i4 < this.eldGraphDataList.size() - 1) {
                drawStatusLine(((ELDGraphData) this.eldGraphDataList.get(i4)).getTime(), ((ELDGraphData) this.eldGraphDataList.get(i12)).getTime(),
                        convertEventNameToStatus(((ELDGraphData) this.eldGraphDataList.get(i4)).getStatus()));
            }
            if (i4 != 0) {
                changeDutyStatus(convertEventNameToStatus(((ELDGraphData) this.eldGraphDataList.get(i4 - 1)).getStatus()), convertEventNameToStatus(((ELDGraphData) this.eldGraphDataList.get(i4)).getStatus()), ((ELDGraphData) this.eldGraphDataList.get(i4)).getTime());
            }
            i4 = i12;
        }
    }


    private int convertEventNameToStatus(String value) {

        if (Objects.equals(value, "off")) {
            return 1;
        } else if (Objects.equals(value, "sb")) {
            return 2;
        } else if (Objects.equals(value, "d")) {
            return 3;
        } else if (Objects.equals(value, "on")) {
            return 4;
        }
        return 0;
    }
}
