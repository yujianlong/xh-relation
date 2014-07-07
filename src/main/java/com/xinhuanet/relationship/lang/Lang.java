package com.xinhuanet.relationship.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Lang {

	/**
	 * 将数组转换成一个列表。
	 * 
	 * @param array
	 *            原始数组
	 * @return 新列表
	 * 
	 * @see org.nutz.castor.Castors
	 */
	public static <T> List<T> array2list(T[] array) {
		if (null == array) {
			return null;
		}
		List<T> re = new ArrayList<T>(array.length);
		for (T obj : array) {
			re.add(obj);
		}
		return re;
	}

	/**
	 * 较方便的创建一个列表，比如：
	 * 
	 * <pre>
	 * List&lt;Pet&gt; pets = Lang.list(pet1, pet2, pet3);
	 * </pre>
	 * 
	 * 注，这里的 List，是 ArrayList 的实例
	 * 
	 * @param eles
	 *            可变参数
	 * @return 列表对象
	 */
	public static <T> List<T> list(T... eles) {
		ArrayList<T> list = new ArrayList<T>(eles.length);
		for (T ele : eles) {
			list.add(ele);
		}
		return list;
	}

	/**
	 * 判断一个对象是否为空。它支持如下对象类型：
	 * <ul>
	 * <li>null : 一定为空
	 * <li>数组
	 * <li>集合
	 * <li>Map
	 * <li>其他对象 : 一定不为空
	 * </ul>
	 * 
	 * @param obj
	 *            任意对象
	 * @return 是否为空
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).isEmpty();
		}
		if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).isEmpty();
		}
		return false;
	}
}
