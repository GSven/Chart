package com.sxt.chart.chart;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;

import com.sxt.chart.R;
import com.sxt.chart.utils.DateFormatUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sxt on 2017/8/5.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class BeizerCurveLine extends View {

    private Paint basePaint;
    private Paint baseLabelPaint;
    /**
     * XY轴的画笔
     */
    private Paint xyPaint;
    /**
     * 曲线画笔
     */
    private Paint curvePaint;
    /**
     * 边框线画笔
     */
    private Paint coverPaint;
    /**
     * 边框线路径
     */
    private Path coverPath;
    /**
     * 阴影画笔
     */
    private Paint fillPaint;
    /**
     * 阴影路径
     */
    private Path fillPath;
    private float basePadding = 30;
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    /**
     * 当前是否是填充状态
     */
    private boolean isFilled = true;
    /**
     * 是否要显示边框线
     */
    private boolean isShowCoverLine = true;
    /**
     * 是否要显示xy轴
     */
    private boolean xyShowState = false;
    /**
     * 辅助网格线是否显示
     */
    private boolean hintLineShowState = true;
    /**
     * 网格线画笔
     */
    private Paint hintPaint;
    /**
     * Y轴最大值  默认取100
     */
    private int maxValueOfY = 100;
    /**
     * 是否需要执行动画
     */
    private boolean isPlayAnimator = true;
    /**
     * 顶部的Label 文字
     */
    private String[] labelStrs;
    /**
     * 顶部的Label 颜色
     */
    private int[] labelColors;
    /**
     * 曲线的数据源
     */
    private Map<Integer, List<ChartBean>> curveDataLists;
    /**
     * 曲线的画笔颜色的集合
     */
    private Map<Integer, Integer> curvePaintColors;
    private Map<Integer, Integer> curveShaderColors;
    /**
     * 曲线路径集合
     */
    private List<Path> pathList;
    /**
     * 动画执行的时长
     */
    private long duration = 1500;
    /**
     * 左上角的单位
     */
    private String unit = "";
    /**
     * hintLine 的默认数量
     */
    private int hintLinesNum = 6;
    private float curveXo;
    /**
     * x轴显示的 坐标数量   4 个点  分 5 个阶段
     */
    private int xNum = 4;
    /**
     * y轴的最大刻度
     */
    private boolean isShowFloat = false;
    private ScrollView scrollView;

    public BeizerCurveLine(Context context) {
        super(context);
        init();
    }

    public BeizerCurveLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BeizerCurveLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(Color.GRAY);
        basePaint.setStrokeWidth(dip2px(0.5f));
        basePaint.setTextSize(dip2px(10));
        basePaint.setTextAlign(Paint.Align.LEFT);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setDither(true);

        baseLabelPaint = new Paint();
        baseLabelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
        baseLabelPaint.setTextSize(dip2px(14));
        baseLabelPaint.setTextAlign(Paint.Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.SANS_SERIF, Typeface.DEFAULT_BOLD.getStyle());
        baseLabelPaint.setTypeface(font0);

        xyPaint = new Paint(basePaint);
        xyPaint.setColor(Color.GRAY);
        xyPaint.setStrokeWidth(dip2px(1));

        hintPaint = new Paint(basePaint);
        hintPaint.setStrokeWidth(0.5f);

        curvePaint = new Paint(basePaint);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(dip2px(4));

        coverPaint = new Paint(basePaint);
        coverPaint.setStyle(Paint.Style.STROKE);
        coverPaint.setStrokeWidth(dip2px(4));

        fillPaint = new Paint(basePaint);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeWidth(dip2px(4));

        coverPath = new Path();
        fillPath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            endX = getMeasuredWidth() - getPaddingRight();
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding * 3;
            endY = getPaddingTop() + basePadding * 4;
        }
    }

    private void start() {
        if (curveDataLists == null || curveDataLists.size() == 0 || curvePaintColors == null || curvePaintColors.size() == 0)
            return;
        if (startting) return;
//        this.post(new Runnable() {
//            @Override
//            public void run() {
        initPath();
        if (isPlayAnimator) {
            initListener();
            initAnimator();
            valueAnimator.start();
            startting = true;
        }
        invalidate();
//            }
//        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //无论有没有数据 都要显示坐标轴线
        drawLabels(canvas);//画顶部的Label
        drawLines(canvas);//画横线

        if (this.curveDataLists == null || this.curveDataLists.size() == 0) {
            return;
        }
        drawXY(canvas, curveDataLists.get(0));//画XY轴

        for (int i = 0; i < curveDataLists.size(); i++) {
            List<ChartBean> chartBeanList = curveDataLists.get(i);
            if (chartBeanList.size() <= 1) {//如果只有一条数据 就 drawPoint
                drawPoint(canvas);
            } else {
                drawCurveLines(canvas);
            }
        }
    }

    private void drawPoint(Canvas canvas) {
        //计算最大值
        float maxValue = calculateMaxValueOfY();
        float dy0 = (startY - endY) / hintLinesNum;
        float dy = (dy0 * (hintLinesNum - 1)) / maxValue;
        for (int i = 0; i < curveDataLists.size(); i++) {
            for (int j = 0; j < curveDataLists.get(i).size(); j++) {

                List<ChartBean> chartBeanList = curveDataLists.get(i);
                if (chartBeanList.size() <= 1) {//如果只有一条数据 就 drawPoint

                    fillPaint.setColor(ContextCompat.getColor(getContext(), curvePaintColors.get(i)));
                    float yValue = startY - curveDataLists.get(i).get(j).y * dy;
                    canvas.drawPoint(curveXo, yValue, fillPaint);
                }
            }
        }
    }

    private void initPath() {
        //计算最大值
        float maxValue = calculateMaxValueOfY();
        curveXo = startX + basePadding * 2.5f;//最左边的横坐标

        //float dx = (endX - startX) / curveDataLists.get(0).size();
        float dx = getDx();
        float dy0 = (startY - endY) / hintLinesNum;
        float dy = (dy0 * (hintLinesNum - 1)) / (maxValue <= 0 ? hintLinesNum - 1 : maxValue);
        float preX;
        float preY;
        float currentX;
        float currentY;
        float targetEndX;
        pathList = new ArrayList<>();
        for (int j = 0; j < curveDataLists.size(); j++) {
            Path curvePath = new Path();
            targetEndX = curveXo + getDx() * (curveDataLists.get(j).size() - 1);
            List<ChartBean> curveBeanList = curveDataLists.get(j);
            for (int i = curveBeanList.size() - 1; i >= 0; i--) {
                if (i == curveBeanList.size() - 1) {
                    float yValue = startY - curveBeanList.get(i).y * dy;
                    curvePath.moveTo(targetEndX, yValue);
                    continue;
                }
                //到这里 肯定不是起点
                preX = curveXo + dx * (i + 1);
                preY = startY - curveBeanList.get(i + 1).y * dy;
                currentX = curveXo + dx * i;
                currentY = startY - curveBeanList.get(i).y * dy;

                curvePath.cubicTo(
                        (preX + currentX) / 2, preY,
                        (preX + currentX) / 2, currentY,
                        currentX, currentY);
            }
            pathList.add(curvePath);
        }
    }

    private float calculateMaxValueOfY() {
        if (curveDataLists != null && curveDataLists.size() > 0) {
            float max = curveDataLists.get(0).get(0).y;
            for (int j = 0; j < curveDataLists.size(); j++) {
                for (int i = 0; i < curveDataLists.get(j).size(); i++) {
                    float f = curveDataLists.get(j).get(i).y;
                    if (max < f) {
                        max = f;
                    }
                }
            }
            return max;
        }
        return 0;
    }

    private ValueAnimator valueAnimator;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private PathMeasure pathMeasureCover;
    float curveLength;

    private void drawCurveLines(Canvas canvas) {
        if (pathList == null || pathList.size() == 0) return;
        Paint currentCoverPaint = null, currentFillPaint = null;
        for (int i = 0; i < pathList.size(); i++) {
            //初始化画笔
            if (isShowCoverLine) {
                currentCoverPaint = new Paint(coverPaint);
                currentCoverPaint.setColor(ContextCompat.getColor(getContext(), curvePaintColors.get(i)));
            }
            if (isFilled) {
                currentFillPaint = new Paint(fillPaint);
                currentFillPaint.setColor(ContextCompat.getColor(getContext(), curvePaintColors.get(i)));
                currentFillPaint.setShader(getShader(
                        new int[]{
                                ContextCompat.getColor(getContext(), curveShaderColors.get(i)),
                                ContextCompat.getColor(getContext(), R.color.alpha_sharder)}));
            }
            //开启动画
            if (isPlayAnimator) {
                pathMeasureCover = new PathMeasure(pathList.get(i), false);
                curveLength = pathMeasureCover.getLength();
                Path dst = new Path();//接收截取的path
                Path dst0 = new Path();
                //根据动画值从线段总长度不断截取绘制造成动画效果
                pathMeasureCover.getSegment(curveLength * (1 - mAnimatorValue), curveLength, dst, true);
                //画阴影
                if (isFilled) {
                    dst.lineTo(curveXo, startY);
                    //dst.lineTo((curveXo + getDx() * (curveDataLists.get(i).size() - 1) - curveXo) * (1 - mAnimatorValue) + curveXo, startY);
                    dst.lineTo((getDx() * (curveDataLists.get(i).size() - 1)) * mAnimatorValue + curveXo, startY);
                    dst.close();
                    assert currentFillPaint != null;
                    canvas.drawPath(dst, currentFillPaint);
                }
                //画曲线 在这里执行 是为了防止阴影將曲线覆盖 造成曲线 线宽 显示不全
                if (isShowCoverLine) {
                    pathMeasureCover.getSegment(curveLength * (1 - mAnimatorValue), curveLength, dst0, true);
                    assert currentCoverPaint != null;
                    canvas.drawPath(dst0, currentCoverPaint);
                }
                if (!isFilled && !isShowCoverLine) {
                    canvas.drawPath(dst0, curvePaint);
                }

            } else {
                if (isFilled) {
                    fillPath.set(pathList.get(i));
                    fillPath.lineTo(curveXo, startY);
                    //fillPath.lineTo(endX, startY);
                    fillPath.lineTo(curveXo + getDx() * curveDataLists.get(i).size(), startY);
                    fillPath.close();
                    assert currentFillPaint != null;
                    canvas.drawPath(fillPath, currentFillPaint);
                }
                if (isShowCoverLine) {
                    coverPath.set(pathList.get(i));
                    assert currentCoverPaint != null;
                    canvas.drawPath(coverPath, currentCoverPaint);
                }
                if (!isFilled && !isShowCoverLine) {
                    canvas.drawPath(pathList.get(i), curvePaint);
                }
            }
        }
    }

    float mAnimatorValue;
    private boolean startting = false;
    private boolean over = false;

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // if (mAnimatorValue != 0.0)
                mAnimatorValue = (float) animation.getAnimatedValue();
                if (!over) {
                    invalidate();
                }
            }
        };
    }

    private void initAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        valueAnimator.addUpdateListener(mUpdateListener);
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                over = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 画顶部的Label
     */
    private void drawLabels(Canvas canvas) {
        if (labelStrs == null || labelStrs.length == 0) return;
        if (labelColors == null || labelColors.length == 0) return;

        //在坐标系左上角 画单位
        float labelCenterY = endY - basePadding * 1.5f;

        Paint leftLabelPaint = new Paint(baseLabelPaint);
        leftLabelPaint.setTextSize(size2sp(15, getContext()));
        leftLabelPaint.setTextAlign(Paint.Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.MONOSPACE, Typeface.DEFAULT_BOLD.getStyle());
        leftLabelPaint.setTypeface(font0);
        canvas.drawText(labelStrs[0], startX + basePadding * 0.6f, labelCenterY, leftLabelPaint);

        float top0 = leftLabelPaint.getFontMetrics().top;
        float descent0 = leftLabelPaint.getFontMetrics().descent;

        //左上角的标题label
        Paint rectPaint = new Paint(basePaint);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[0]));
        canvas.drawRect(startX, labelCenterY + top0 * 0.8f, startX + basePadding / 3, labelCenterY + descent0 / 2, rectPaint);

        if (labelStrs.length != 3 || labelColors.length != 3) return;
        //右上角的label
        float left = endX - basePadding * 8;
        float baseY = endY - basePadding;
        float right = left + 4.5F * basePadding;
        float DX = basePadding / 2;

        Paint paint = new Paint(basePaint);
        paint.setTextSize(dip2px(10));
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(labelStrs[1], left, baseY, paint);//低压
        float top1 = paint.getFontMetrics().top;
        float descent1 = paint.getFontMetrics().descent;

        canvas.drawText(labelStrs[2], right, baseY, paint);//高压
        float top2 = paint.getFontMetrics().top;
        float descent2 = paint.getFontMetrics().descent;

        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[1]));
        float top11 = top1 * 0.8f;
        float descent11 = descent1 * 0.6f;
        canvas.drawRect(
                left - DX + top11 - descent11,
                baseY + top11,
                left - DX,
                baseY + descent11,
                rectPaint);

        float top22 = top2 * 0.8f;
        //float descent22 = descent2 * 0.6f;
        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[2]));
        canvas.drawRect(
                right - DX + top11 - descent11,
                baseY + top22,
                right - DX,
                baseY + descent11,
                rectPaint);
    }

    /**
     * 画网格线
     */
    private void drawLines(Canvas canvas) {
        if (hintLineShowState) {
            float dy = (startY - endY) / hintLinesNum;
            float x0 = startX + basePadding * 2;
            for (int i = 0; i < hintLinesNum + 1; i++) {
                if (i == hintLinesNum) {
                    //顶部的横线
                    canvas.drawLine(startX, startY - dy * i, endX, startY - dy * i, hintPaint);
                } else {
                    canvas.drawLine(x0, startY - dy * i, endX, startY - dy * i, hintPaint);
                }
                if (i == hintLinesNum - 1) {
                    //在坐标系左上角 画单位
                    Paint unitPaint = new Paint(xyPaint);
                    unitPaint.setTextAlign(Paint.Align.LEFT);
                    float baseY = startY - dy * i - basePadding / 2;
                    canvas.drawText(unit, startX, baseY, unitPaint);
                    //画Y轴最大刻度
                    float maxValue = calculateMaxValueOfY();
                    String max = maxValue <= 0 ? "" : (isShowFloat ? String.valueOf(maxValue) : String.valueOf((int) maxValue));
                    canvas.drawText(max,
                            startX, baseY + basePadding * 1.3f,

                            unitPaint);
                }
                if (i == 0) {
                    Paint unitPaint = new Paint(xyPaint);
                    unitPaint.setTextAlign(Paint.Align.LEFT);
                    //画Y轴最低刻度
                    canvas.drawText(String.valueOf(0), startX, startY, unitPaint);
                }
            }
        }
    }

    private void drawXY(Canvas canvas, List<ChartBean> xDatas) {
        if (xyShowState) {
            canvas.drawLine(startX, startY, endX, startY, xyPaint);//X轴
            canvas.drawLine(startX, startY, startX, endY, xyPaint);//Y轴
        }
        float x0 = startX + basePadding;
        float dx = (endX - curveXo - basePadding) / xNum;
        float y = startY + basePadding * 2;
        xyPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < xDatas.size(); i++) {//画X轴刻度
            canvas.drawText(xDatas.get(i).x, x0 + dx * i, y, xyPaint);
        }
    }

    public BeizerCurveLine setMaxXNum(int xNum) {
        this.xNum = xNum;
        return this;
    }

    /**
     * 计算 x 轴 平均值
     *
     * @return
     */
    private float getDx() {
        if (curveDataLists == null || curveDataLists.get(0) == null) return 0;
        return (endX - curveXo) / this.xNum;//按照最多显示7天数据的长度计算
    }

    public BeizerCurveLine setLabels(String[] labelStrs, int[] labelColors) {
        this.labelStrs = labelStrs;
        this.labelColors = labelColors;
        return this;
    }

    public BeizerCurveLine setPlayAnimator(boolean isPlayAnimator) {
        this.isPlayAnimator = isPlayAnimator;
        return this;
    }

    public BeizerCurveLine setMaxValueOfY(int maxValueOfY) {
        this.maxValueOfY = maxValueOfY;
        return this;
    }

    public BeizerCurveLine setCoverLine(boolean isShowCoverLine) {
        this.isShowCoverLine = isShowCoverLine;
        return this;
    }

    public BeizerCurveLine setCoverLine(int coverLineColor) {
        if (coverPaint != null) coverPaint.setColor(coverLineColor);
        return this;
    }

    public BeizerCurveLine setCoverLineWidth(float widthDpValue) {
        if (coverPaint != null) coverPaint.setStrokeWidth(dip2px(widthDpValue));
        return this;
    }

    public BeizerCurveLine setFillState(boolean isFilled) {
        this.isFilled = isFilled;
        return this;
    }

    public BeizerCurveLine setXYShowState(boolean xyShowState) {
        this.xyShowState = xyShowState;
        return this;
    }

    public BeizerCurveLine setXYColor(int colorResId) {
        if (xyPaint != null) xyPaint.setColor(ContextCompat.getColor(getContext(), colorResId));
        return this;
    }

    public BeizerCurveLine setHintLineColor(int colorResId) {
        if (hintPaint != null) hintPaint.setColor(ContextCompat.getColor(getContext(), colorResId));
        return this;
    }

    public BeizerCurveLine setShowFloat(boolean isShowFloat) {
        this.isShowFloat = isShowFloat;
        return this;
    }

    public BeizerCurveLine setHintLineShowState(boolean hintLineShowState) {
        this.hintLineShowState = hintLineShowState;
        return this;
    }

    public BeizerCurveLine setAnimDurationTime(long duration) {
        this.duration = duration;
        return this;
    }

    private BeizerCurveLine setHintLinesNum(int hintLinesNum) {
        if (hintLinesNum > -1) {
            this.hintLinesNum = hintLinesNum;
        }
        return this;
    }

    public BeizerCurveLine setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    private LinearGradient getShader(int colors[]) {
        if (colors != null) {
            if (colors.length == 0) {//说明用户没有传入颜色值 此时显示默认的颜色来填充
                colors = new int[]{Color.GREEN, ContextCompat.getColor(getContext(), R.color.alpha_sharder)};
            }
        } else {
            colors = new int[]{Color.GREEN, Color.GREEN, ContextCompat.getColor(getContext(), R.color.alpha_sharder)};
        }
        return new LinearGradient(//填充方向是以Y轴为基准 若是自上而下 就是 0,0,0,getMeasuredHeight()
                startX, endY, startX, startY,
                colors,
                null, Shader.TileMode.CLAMP);
    }

    float size2sp(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }

    int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @NonNull
    public String parseDate(String currentDate) {
        if (currentDate != null && currentDate.length() > 0) {
            try {
                String currentStr = String.valueOf(DateFormatUtil.getSecondsFromDate(currentDate));//转换成毫秒值
                String mm = DateFormatUtil.getDateFromSeconds(currentStr, "MM");
                String dd = DateFormatUtil.getDateFromSeconds(currentStr, "dd");
                if (mm.startsWith("0")) {
                    mm = mm.substring(1, mm.length());
                }
                if (dd.startsWith("0")) {
                    dd = dd.substring(1, dd.length());
                }

                return dd + "/" + mm;

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    /**
     * 数据可能是 1/5  4/5
     *
     * @param floatStr
     * @return
     */
    public float parseFloat(String floatStr) {
        if (floatStr != null && floatStr.length() > 0) {
            try {
                if (floatStr.length() > 2 && floatStr.contains("/")) {
                    String[] split = floatStr.split("/");
                    return new BigDecimal(split[0]).divide(new BigDecimal(split[1])).floatValue();
                } else {
                    return Float.parseFloat(floatStr) <= 0 ? 0 : Float.parseFloat(floatStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static class CurveLineBuilder {

        public Map<Integer, List<ChartBean>> curveDataLists;
        public Map<Integer, Integer> curvePaintColors;
        public Map<Integer, Integer> curveShaderColors;
        private int index;

        public CurveLineBuilder() {
            curveDataLists = new HashMap<>();
            curvePaintColors = new HashMap<>();
            curveShaderColors = new HashMap<>();
        }

        public CurveLineBuilder builder(List<ChartBean> curveBeans, int coverLineColor, int shaderColor) {
            if (curveBeans != null && curveBeans.size() > 0) {
                int index = this.index;
                this.curveDataLists.put(index, curveBeans);
                this.curvePaintColors.put(index, coverLineColor);
                this.curveShaderColors.put(index, shaderColor);
                this.index++;
            }
//            else {
//                throw new IllegalArgumentException("无效参数data或color");
//            }
            return this;
        }

        public void build(BeizerCurveLine beizerCurveLine) {
            beizerCurveLine.startDraw(curveDataLists, curvePaintColors, curveShaderColors);
        }
    }

    private void startDraw(Map<Integer, List<ChartBean>> curveDataLists, Map<Integer, Integer> curvePaintColors, Map<Integer, Integer> curveShaderColors) {
        if (curveDataLists == null || curveDataLists.size() == 0 || curvePaintColors == null || curvePaintColors.size() == 0)
            return;
        this.curveDataLists = curveDataLists;
        this.curvePaintColors = curvePaintColors;
        this.curveShaderColors = curveShaderColors;
    }

    public BeizerCurveLine setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
        scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (isCover(BeizerCurveLine.this)) {
                    start();
                }
            }
        });

        return this;
    }

    /**
     * 检测制定View是否被遮住显示不全
     *
     * @return
     */
    protected boolean isCover(View view) {
        Rect rect = new Rect();
        if (view.getGlobalVisibleRect(rect)) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

}
