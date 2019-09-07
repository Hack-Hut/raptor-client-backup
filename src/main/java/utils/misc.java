package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class misc {
    public static <T> List<String> convertSetToList(Set<T> set)
    {
        List<String> aList = new ArrayList<>(set.size());
        for (T x : set)
            aList.add((String) x);
        return aList;
    }

}
