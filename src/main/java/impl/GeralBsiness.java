package impl;

import java.math.BigDecimal;

public class GeralBsiness {

    public static double getTwoDecimal(BigDecimal bigDecimal) {
        double db = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return db;
    }

    public static String getTwoDecimal2(BigDecimal bigDecimal) {
        String db = bigDecimal.setScale(8, BigDecimal.ROUND_HALF_UP).toString();
        return db;
    }


}
