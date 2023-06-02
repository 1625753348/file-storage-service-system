package com.chromatic.common.utils;

import java.util.*;

/************************************************************************
 * @author: wg
 * @description:
 * @params:
 * @return:
 * @createTime: 10:49  2022/5/6
 * @updateTime: 10:49  2022/5/6
 ************************************************************************/
public class CollectionUtil {
    private CollectionUtil() {
    }

    /**
     * 获取两个集合的不同元素
     *
     * @param collmax
     * @param collmin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Collection<T> getDifferent(Collection<T> collmax, Collection<T> collmin) {
        //使用LinkeList防止差异过大时,元素拷贝
        Collection csReturn = new LinkedList();
        Collection max = collmax;
        Collection min = collmin;
        //先比较大小,这样会减少后续map的if判断次数
        if (collmax.size() < collmin.size()) {
            max = collmin;
            min = collmax;
        }
        //直接指定大小,防止再散列
        Map<Object, Integer> map = new HashMap<Object, Integer>(max.size());
        for (Object object : max) {
            map.put(object, 1);
        }
        for (Object object : min) {
            if (map.get(object) == null) {
                csReturn.add(object);
            } else {
                map.put(object, 2);
            }
        }
        for (Map.Entry<Object, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                csReturn.add(entry.getKey());
            }
        }
        return csReturn;
    }

    /**
     * 获取两个集合的不同元素,去除重复
     *
     * @param collmax
     * @param collmin
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Collection<T> getDifferentNoDuplicate(Collection<T> collmax, Collection<T> collmin) {
        return new HashSet(getDifferent(collmax, collmin));
    }

    /************************************************************************
     * @author: wg
     * @description: 找相同
     * @params:
     * @return:
     * @createTime: 17:22  2022/5/31
     * @updateTime: 17:22  2022/5/31
     ************************************************************************/
    public static <T> Collection<T> getSame(Collection<T> list1, Collection<T> list2) {
        ArrayList<T> exist = new ArrayList<>(list2);
        ArrayList<T> same = new ArrayList<>(list2);

        exist.removeAll(list1);

        same.removeAll(exist);

        return same;
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}