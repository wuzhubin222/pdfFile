package test;

import java.io.*;

public class sqlMapperTest {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\zccx\\Desktop\\mapperXml.txt");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        StringBuilder selectWhere = new StringBuilder("<where>");
        StringBuilder updateSet = new StringBuilder("<set>");

        StringBuilder insertPrefix = new StringBuilder("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
        StringBuilder insertPrefixValue = new StringBuilder("<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >");

        Boolean whereAddAnd = false;
        String line = null;
        while ((line = bufferedReader.readLine())!=null){
            String dl[] = line.split(" ");
            String column = dl[5].split("=")[1].replace("\"","");
            String property = dl[6].split("=")[1].replace("\"","");
            String jdbctype = dl[7].split("=")[1].replace("\"","");

            if(whereAddAnd){
                selectWhere.append("<if test=\""+property+" != null\"> and "
                        +column+" = #{"+property+",jdbcType="+jdbctype+"}</if>");
            }

            if(!whereAddAnd){
                selectWhere.append("<if test=\""+property+" != null\">"
                        +column+" = #{"+property+",jdbcType="+jdbctype+"}</if>");
                whereAddAnd = true;
            }

            updateSet.append("<if test=\""+property+" != null and "+property+" != ''\">"
                    +column+" = #{"+property+",jdbcType="+jdbctype+"},</if>");

            insertPrefix.append("<if test=\""+property+" != null\">"
                    +column+",</if>");

            insertPrefixValue.append("<if test=\""+property+" != null\">#{"
                    +property+",jdbcType="+jdbctype+"},</if>");
        }
        selectWhere.append("</where>");
        insertPrefix.append("</trim>");
        insertPrefixValue.append("</trim>");
        updateSet.append("</set>");

        System.out.println("selectWhere");
        System.out.println(selectWhere.toString());
        System.out.println("\ninsertTrim");
        System.out.println(insertPrefix.append(insertPrefixValue).toString());
        System.out.println("\nupdateSet");
        System.out.println(updateSet.toString());

    }

}
