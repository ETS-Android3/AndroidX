package com.android.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Decimal {

    public static final int ROUND_UP = BigDecimal.ROUND_UP;
    public static final int ROUND_DOWN = BigDecimal.ROUND_DOWN;
    public static final int ROUND_HALF_DOWN = BigDecimal.ROUND_HALF_DOWN;
    public static final int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;
    public static final int ROUND_HALF_EVEN = BigDecimal.ROUND_HALF_EVEN;
    public static final int ROUND_CEILING = BigDecimal.ROUND_CEILING;
    public static final int ROUND_UNNECESSARY = BigDecimal.ROUND_UNNECESSARY;

    /**
     * 小数点之前的长度
     */
    private static int beforeLength = 0;

    /**
     * 格式化数据
     *
     * @param value       数据
     * @param decimalSize 小数位数
     * @return
     */
    public static String format(double value, int decimalSize) {
        return format(value + "", decimalSize, ROUND_DOWN);
    }

    /**
     * 格式化数据
     *
     * @param value       数据
     * @param decimalSize 小数位数
     * @return
     */
    public static String format(float value, int decimalSize) {
        return format(value + "", decimalSize, ROUND_DOWN);
    }

    /**
     * 格式化数据
     *
     * @param value       数据
     * @param decimalSize 小数位数
     * @return
     */
    public static String format(String value, int decimalSize) {
        return format(value + "", decimalSize, ROUND_DOWN);
    }


    /**
     * 构建零的个数
     *
     * @param size 个数
     * @return
     */
    public static String buildZero(int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * 格式化
     *
     * @param value 数值
     * @return
     */
    public static String format(String value) {
        return format(value, 2, ROUND_DOWN);
    }

    /**
     * 格式价格字符串
     *
     * @param value        值
     * @param decimalSize  小数个数
     * @param roundingMode 模式
     * @return
     */
    public static String format(String value, int decimalSize, int roundingMode) {
        if (value == null || value.length() == 0 || value.equals("null") || value.equals("0")) {
            return "0." + buildZero(decimalSize);
        }
        if (!value.contains(".")) {
            value += "." + buildZero(decimalSize);
        }
        return new BigDecimal(value).setScale(decimalSize, roundingMode).toString();
    }

    /**
     * 价格格式输入
     *
     * @param editText
     */
    public static void format(EditText editText) {
        format(editText, 2);
    }

    /**
     * 设置过滤器
     *
     * @param editText     输入控件
     * @param charSequence 字符
     * @param decimalSize  小数点
     */
    public static void setFilter(EditText editText, CharSequence charSequence, int decimalSize) {
        setFilter(editText, charSequence, decimalSize, 255);
    }

    /**
     * 设置过滤器
     *
     * @param editText     输入控件
     * @param charSequence 字符
     * @param decimalSize  小数点
     * @param maxLength    最大长度
     */
    public static void setFilter(EditText editText, CharSequence charSequence, int decimalSize, int maxLength) {
        if (charSequence.toString().startsWith(".")) {
            editText.setText("");
            return;
        }
        if (matches("^\\d+.$", charSequence) && charSequence.toString().contains(".")) {
            beforeLength = charSequence.length();
            int length = beforeLength + decimalSize;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        } else {
            if (matches("^\\d+.[0-9]{0,}", charSequence) && charSequence.toString().contains(".")) {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(beforeLength + decimalSize)});
            } else {
                if (charSequence.length() <= maxLength && !charSequence.toString().contains(".")) {
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength + decimalSize)});
                } else {
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    if (charSequence.length() > 0 && maxLength <= charSequence.length()) {
                        editText.setText(charSequence.subSequence(0, maxLength));
                    }
                    if (editText.getText().length() > 0) {
                        editText.setSelection(editText.length());
                    }
                }
            }
        }
    }

    /**
     * 匹配
     *
     * @param regex        规则
     * @param charSequence 内容
     * @return
     */
    public static boolean matches(String regex, CharSequence charSequence) {
        Pattern r = Pattern.compile(regex);
        Matcher matcher = r.matcher(charSequence);
        return matcher.matches();
    }


    /**
     * 显示输入小数点位数
     *
     * @param editText    控件
     * @param decimalSize 小数位数
     */
    public static void format(EditText editText, int decimalSize, int maxLength) {
        format(editText, decimalSize, maxLength, null);
    }

    /**
     * 显示输入小数点位数
     *
     * @param editText    控件
     * @param decimalSize 小数位数
     */
    public static void format(EditText editText, int decimalSize) {
        format(editText, decimalSize, 255, null);
    }

    /**
     * 显示输入小数点位数
     *
     * @param editText    控件
     * @param decimalSize 小数位数
     */
    public static void format(final EditText editText, final int decimalSize, final int maxLength, final addTextChangedListener listener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                setFilter(editText, charSequence, decimalSize, maxLength);
                if (listener != null) {
                    listener.onTextChanged(charSequence, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface addTextChangedListener {

        void onTextChanged(CharSequence charSequence, int start, int before, int count);

    }


}
