package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import excption.BusinessException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fpdmfphm {

    public static String removeTailZero(BigDecimal b) {
        String s = b.toString();
        int i, len = s.length();
        for (i = 0; i < len; i++)
            if (s.charAt(len - 1 - i) != '0')
                break;
        if (s.charAt(len - i - 1) == '.')
            return s.substring(0, len - i - 1);
        return s.substring(0, len - i);
    }

    public static String Calculation(String JsonObject, Integer taxType) throws BusinessException {

        Map<String, String> map = new HashMap<String, String>();
        String sps = "";
        try {
            JSONObject jsonObject = JSON.parseObject(JsonObject);
            //获取json里的spobj键下的列表
            JSONArray jsonArray = jsonObject.getJSONArray("spobj");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (taxType == 0) {
                    BigDecimal sl = null;
                    if (StringUtils.isEmpty(obj.get("sl")) && StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        if (StringUtils.isEmpty(obj.get("sl"))) {
                            obj.put("sl", "");
                        }
                        if (StringUtils.isEmpty(obj.get("dj"))) {
                            obj.put("dj", "");
                        }
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(bhsje));
                    }else if (StringUtils.isEmpty(obj.get("sl")) && !StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double dj = Double.parseDouble(obj.get("dj") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        sl = BigDecimal.valueOf(bhsje / dj);
                        sl = sl.setScale(15, BigDecimal.ROUND_HALF_UP);
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("sl", removeTailZero(sl));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(bhsje));
                    }else if (!StringUtils.isEmpty(obj.get("sl")) && !StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double dj = Double.parseDouble(obj.get("dj") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        sl = BigDecimal.valueOf(bhsje / dj);
                        sl = sl.setScale(15, BigDecimal.ROUND_HALF_UP);
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("sl", removeTailZero(sl));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(bhsje));
                    }else if (StringUtils.isEmpty(obj.get("dj")) && !StringUtils.isEmpty(obj.get("sl"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        double jsonSl = Double.parseDouble(obj.get("sl") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        BigDecimal dj = BigDecimal.valueOf(bhsje / jsonSl);
                        dj = dj.setScale(15, BigDecimal.ROUND_HALF_UP);
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("dj", removeTailZero(dj));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(bhsje));
                    }
                }

                if (taxType == 1) {
                    Double sl = null;
                    if (StringUtils.isEmpty(obj.get("sl")) && StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        if (StringUtils.isEmpty(obj.get("sl"))) {
                            obj.put("sl", "");
                        }
                        if (StringUtils.isEmpty(obj.get("dj"))) {
                            obj.put("dj", "");
                        }
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(je));
                    }else if (StringUtils.isEmpty(obj.get("sl")) && !StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double dj = Double.parseDouble(obj.get("dj") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        sl = bhsje / dj;
                        BigDecimal b = new BigDecimal(sl);
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("sl", (double) Math.round(b.setScale(15, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(je));
                    }else if (!StringUtils.isEmpty(obj.get("sl")) && !StringUtils.isEmpty(obj.get("dj"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        Double dj = Double.parseDouble(obj.get("dj") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        sl = bhsje / dj;
                        BigDecimal b = new BigDecimal(sl);
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("sl", (double) Math.round(b.setScale(15, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(je));
                    }else if (StringUtils.isEmpty(obj.get("dj")) && !StringUtils.isEmpty(obj.get("sl"))) {
                        Double je = Double.parseDouble(obj.get("je") + "");
                        Double slv = Double.parseDouble(obj.get("slv") + "");
                        sl = Double.parseDouble(obj.get("sl") + "");
                        Double bhsje = je / (1 + slv);
                        Double se = je - bhsje;
                        Double dj = je / sl;
                        obj.put("se", new java.text.DecimalFormat("0.00").format(se));
                        obj.put("dj", new java.text.DecimalFormat("0.000000000000000").format(dj));
                        obj.put("je", new java.text.DecimalFormat("0.00").format(je));
                    }
                }

                String spbm = obj.getString("spbm");
                for (int j = spbm.length(); j < 19; j++) {
                    spbm += "0";                }
                obj.remove("spbm");
                obj.put("spbm", spbm);
            }
            sps = jsonArray.toJSONString();
        } catch (Exception e) {
            throw new BusinessException(3046,"商品明细异常");
        }
        return sps;
    }


    private static String sigFpdmFphm() {

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        int max = uuid.length();
        String result = "";
        for (int i = 0; i < max; i++) {
            char c = uuid.charAt(i);
            if (!Character.isDigit(c)) {
                int b = (int) c;
                result = result + ((b + "").length() > 2 ? b % 100 : b % 10);
            } else {
                result = result + c;
            }
        }

        String dmhm = result.substring(0, 12) + result.substring(result.length() - 8, result.length());
        return dmhm;
    }

    public static String getFpdm() {
        return sigFpdmFphm().substring(0, 12);
    }

    public static String getFphm() {
        return sigFpdmFphm().substring(12, 20);
    }

}





