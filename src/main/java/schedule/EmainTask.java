package schedule;


import Dao.CdEfpMailmanageMapper;
import com.alibaba.fastjson.JSONObject;
import entity.ConResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import utils.HttpRequestUtils;
import utils.LogUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class EmainTask {
    //@led(cron="0 0 /5 * * ? *")
    @Resource
    CdEfpMailmanageMapper cdEfpMailmanageMapper;
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*定时推送邮箱*/
     @Scheduled(cron="0 */5 * * * ?")
    public void run(){
        try{
            repushMail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    * 重新推送邮箱
    * */
    public synchronized void repushMail() {
        List<String> data=new ArrayList<String>();
        List<HashMap<String,String>> list=cdEfpMailmanageMapper.selectUrl();
      //  System.out.println(list.size());
        //String path ="mailPush.txt";
        for (HashMap<String,String> ss :list){
            try{
                if( ss.get("url")!=null){
                    //System.out.println(ss.get("url"));
                    String result = HttpRequestUtils.get(ss.get("url"));

                    if(JSONObject.parseObject(result).getInteger("code")==0){
                        data.add(ss.get("id"));
                    }
                   // LogUtils.writeLogFileName(path,result,"重新推送pdf地址"+ss.get("url")+"id"+ss.get("id"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }catch ( Error e){
                e.printStackTrace();
            }
        }
        if(data.size()>0){
            cdEfpMailmanageMapper.updateUrlsState(data);
        }
     }



}
