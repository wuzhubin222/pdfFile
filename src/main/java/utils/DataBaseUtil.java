package utils;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataBaseUtil { 
	/*

CREATE TABLE `cd_efp_invoice_to_json` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(200) DEFAULT NULL COMMENT '关联的编号',
  `json` text COMMENT '转换后json',
  `state` int(11) DEFAULT '0' COMMENT '状态(0未生成1已生成,2上传)',
  `data` varchar(500) DEFAULT NULL COMMENT '文件位置',
  `ftp_url` varchar(500) DEFAULT NULL COMMENT 'ftp的返回值',
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6324 DEFAULT CHARSET=utf8




	 * */
	
	
	/*private String jdbcUrl="jdbc:mysql://114.67.47.58:3306/taxcloud_efp?zeroDateTimeBehavior=convertToNull";
	private String name="test";
	private String password="test888";*/
	private String jdbcUrl="jdbc:mysql://114.67.47.41:3706/taxcloud_efp?zeroDateTimeBehavior=convertToNull";
	private String name="admin";
	private String password="mysql@tsp";
	private String className="com.mysql.jdbc.Driver";
	Connection conn=getConnection();
	private String collName="temp.id,bz as bz,"
			+ "fhr as fhr,fpdm as fpdm,fphm as fphm,gfmc as gfmc,ifnull(gfsh,'000000000000000') as gfsh,"
			+ "concat('￥',hjje) as je,jqbh as jqbh,fp_jym as jym,kpr as kpr,DATE_FORMAT(kprq,'%Y年%m月%d日') as kprq,"
			+ "mw as mw,concat('￥',hjse)   as se,skr as skr,xfdz_dh as xfdzdh,xfmc as xfmc,xfsh as  xfsh,xfyhmc_yhzh as xfyhzh";
	public String getQuerySql(){
		return " SELECT \n" +
				"\tCONCAT('01,10,',fpdm,',',fphm,',',hjje,',',kprq,',',fp_jym,',',\n" +
				"\tSUBSTR('POIUYTREWQASDFGHJKLMNBVCXZ',ROUND(RAND()*25+1,0),1),\n" +
				"SUBSTR('0123456789',ROUND(RAND()*9+1,0),1),\n" +
				"SUBSTR('POIUYTREWQASDFGHJKLMNBVCXZ',ROUND(RAND()*25+1,0),1),\n" +
				"SUBSTR('0123456789',ROUND(RAND()*9+1,0),1)\n" +

				"\t\n" +
				"\t) AS qrm,\n" +
				"hjje+hjse AS jshjxx,"+
				"\ttemp.id AS id,\n" +
				"\tbz AS bz,\n" +
				"\tfhr AS fhr,\n" +
				"\tfpdm AS fpdm,\n" +
				"\tfphm AS fphm,\n" +
				"\tgfmc AS gfmc,\n" +
				"\tgfdz_dh AS gfdzdh,\n" +
				"\tgfyhmc_yhzh AS gfyhzh,\n" +
				"\tIFNULL(gfsh,'000000000000000') AS gfsh,\n" +
				"\tCONCAT('￥',hjje) AS je,\n" +
				"\tjqbh AS jqbh,\n" +
				"\tfp_jym AS jym,\n" +
				"\tkpr AS kpr,\n" +
				"\tDATE_FORMAT(kprq,'%Y年%m月%d日') AS kprq,\n" +
				"\tmw AS mw,\n" +
				"\tCONCAT('￥',hjse) AS se,\n" +
				"\tskr AS skr,\n" +
				"\txfdz_dh AS xfdzdh,\n" +
				"\txfmc AS xfmc,\n" +
				"\txfsh AS xfsh,\n" +
				"\txfyhmc_yhzh AS xfyhzh\n" +
				"\t \n" +
				"\tFROM (\n" +
				"\tSELECT temp.* FROM cd_efp_file_repository  AS c LEFT JOIN   ( \tSELECT * FROM cd_efp_invoice ) AS  temp ON temp.fphm =c.fphm AND c.fpdm=temp.fpdm  WHERE c.create_time BETWEEN '2018-10-30 13:02:42' AND '2018-11-20 13:36:04' ) AS temp \n" +
				" \n" +
				"\t\t\t LEFT JOIN `cd_efp_invoice_to_json` AS json ON json.uid=temp.id \n" +
				"\t\t\tWHERE json.id IS NULL "
				;
	}
	public List<Map<String,Object>> goodsList(String fphm){
		return query("SELECT CONCAT(ROUND(c.slv*100,0),'%') AS slv,c.se,c.dj,c.wp_dw AS dw,c.wp_mc AS hwmc,c.wp_xh AS ggxh ,c.je,ROUND(c.sl,0) AS sl FROM cd_efp_invoice_qd AS c WHERE c.fphm='"+fphm+"'");
	}
	public  void toJson(int size){
		List<Map<String,Object>> list= query(getQuerySql()+" limit "+size) ;
		int length=0;
		for(Map<String,Object> item:list){
			String fphm=item.get("fphm").toString();
			List<Map<String,Object>>data=goodsList(fphm);
			String value=item.get("jshjxx").toString();
			item.put("jshjdx", PdfUtils.moneyNumberTocCh(new BigDecimal(value)));
			System.out.println(++length);
			item.put("mxxx", data);
			insertToJson(item);
		} 
	}
	public static void main(String ...args){
		DataBaseUtil d=new DataBaseUtil();
		d.toJson(1);
		d.close();
	}
	public void close(){
		try {
			conn.close();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
	public  Map<String,Object>  queryOne(String sql,Object... param){ 
		List<Map<String,Object>> result=query(sql,param);
		if(result.size()>0){
			return result.get(0);
		}
		return null;
	}
	public int insertToJson(Map<String,Object> item){
		String uid=item.get("id").toString();
		item.remove("id");
		String json= JSONObject.toJSON(item).toString();
		 return update("insert into cd_efp_invoice_to_json(uid,json ) value (?,?)", uid,json);
	}
	public int update(String sql,Object ...param){
		int result=-1;
		 PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<param.length;i++){
				 ps.setObject(i+1, param[i]);
			}
			result=ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
		return result;
	}
	public List<Map<String,Object>> query(String sql,Object... param){ 
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		 try {
			 PreparedStatement ps =conn.prepareStatement(sql);
			 for(int i=0;i<param.length;i++){
				 ps.setObject(i+1, param[i]);
			 }
			 ResultSet rs=ps.executeQuery();
			 while(rs.next()){
				 Map<String,Object> item=new HashMap<String,Object>();
				 for(int i=0;i<rs.getMetaData().getColumnCount();i++){
				 
					 String name=rs.getMetaData().getColumnLabel(i+1);
					 Object value=rs.getObject( rs.getMetaData().getColumnLabel(i+1));	
					 item.put(name, value);
				 }
				 result.add(item);
			 }
			 rs.close();
			 ps.close(); 
		} catch (Exception e) { 
			e.printStackTrace();
		} 
		return result;
	}
	public Connection getConnection(){  
		try{  
				 Class.forName(className) ; 
		}catch(ClassNotFoundException e){ 
			 e.printStackTrace();
		}
		try{
			return DriverManager.getConnection(jdbcUrl ,
					name, password);			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
