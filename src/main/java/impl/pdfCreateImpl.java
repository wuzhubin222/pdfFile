package impl;

import Dao.EfpInvoiceMapper;
import com.alibaba.fastjson.JSONObject;
import entity.ConResult;
import excption.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.ICore;
import service.pdfCreate;
import utils.BaseUtils;
import utils.PdfUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Service
public class pdfCreateImpl extends BaseUtils implements pdfCreate {

    @Autowired
    EfpInvoiceMapper efpInvoiceMapper;

    public static  String get(String o){
        return PdfUtils.moneyNumberTocCh(new BigDecimal(o));
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(pdfCreateImpl.class);

    @Autowired
    private ICore iCore;
 /*   @Autowired
    private BaseUtils baseUtils;*/


    @Override
    public ConResult Createpdf(String fphm,String fpdm) {
       // String fphm,  String fpdm

        //
        List<HashMap<String, Object>> list =efpInvoiceMapper.queryNodpfAll(fphm,fpdm);
        List<Map<String, Object>> list1 = new ArrayList<>();
        Map<String,Object> qq=new HashMap<String,Object>();
        for(Map<String,Object> item:list){
            List<HashMap<String, Object>> mxlist=efpInvoiceMapper.selectMxall( item.get("fphm").toString(),item.get("fpdm").toString());
            item.put("mxxx",mxlist);
            item.put("jshjdx",   get(item.get("jshjxx").toString()));
            qq.put("dzfpresult",item);
            String param=item.get("parm2").toString();

       /*     System.out.println(JSONObject.toJSONString(qq));
            System.out.println(param);
            System.out.println( get(item.get("jshjxx").toString()));*/

            try{
               // LogUtils.writeLogFileName("推送数据log.txt", JSONObject.toJSONString(qq));
               // LogUtils.writeLogFileName("推送qqsh参数log.txt",param);

                LOGGER.info("Json数据"+JSONObject.toJSONString(qq));
                LOGGER.info("qqsh参数"+param);
               ConResult c=  iCore.Createpdf1("createDZp.txt", JSONObject.toJSONString(qq),param,iCore);

              // LogUtils.writeLogFileName("返回结果log.txt", JSONObject.toJSONString(c));
                Thread.sleep(1000);


//            System.out.println(
//                HttpRequestUtils.postRequest("http://localhost:8083/invoice/createDZp",
//                        "dzfpResult="+  URLEncoder.encode(JSONObject.toJSONString(qq))+"&qqlsh="+param));
            }catch (Exception e){

                //e.printStackTrace();
              //  LOGGER.error(e.getMessage());
              //      this.getState(e);

                this.logOutputException(new BusinessException(3073, "获取版式文件失败测试测试测试v" + e.toString()));
                    System.out.println("=====重新生成pdf错误======"+e.getMessage());

            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("fpdm", fpdm);
            map.put("fphm", fphm);
            list1.add(map);
        }
        ConResult conResult = new ConResult();
        Map<String, Object> mapaaa = new HashMap<String, Object>();
        mapaaa.put("list", list1);
        conResult.setMap(mapaaa);
        return  conResult;
    }




}
