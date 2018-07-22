package io.jboot.admin.base.common;

/**
 * 正则校验常量类
 * @author Rlax
 *
 */
public class RegexKey {

    public final static String MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\\\d{8}$";

    public final static String EMAIL = "\\w+@(\\w+.)+[a-z]{2,3}";

    public final static String OgCode="^(([0-9]{8})-[0-9]{1})$";
}
