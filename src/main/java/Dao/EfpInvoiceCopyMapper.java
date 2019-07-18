package Dao;

import java.util.HashMap;
import java.util.List;
import model.EfpInvoiceCopy;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpInvoiceCopyMapper {
    int deleteByPrimaryKey(String id);

    List<EfpInvoiceCopy> selectAll();

    List<EfpInvoiceCopy> selectSelective(EfpInvoiceCopy record);

    int insertSelective(EfpInvoiceCopy record);

    int updateByPrimaryKeySelective(EfpInvoiceCopy record);

    /*查询短信数量*/
    HashMap<String, Object> selectSmsOrderbyNsrsbh(@Param("nsrsbh") String nsrsbh);

    /*根据号码查询开票点 不用*/
    HashMap<String, Object> selectbyPhone(@Param("phone") String phone);


    /**
     * 自定义sql
     */

    List<EfpInvoiceCopy> selectInvoiceCopysByOpenid(@Param("openId") String openId, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    List<EfpInvoiceCopy> selectInvoiceCopysByNsrsbh(@Param("nsrsbh") String nsrsbh, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    List<EfpInvoiceCopy> selectInvoiceCopysByPhoneOrOpenid(@Param("phone") String phone,@Param("openId") String openId, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    List<String> selectCounts(String nsrsbh);

    List<HashMap<String, String>> selectInvoiceCopyList(@Param("parm1") String parm1, @Param("pageOff") Integer pageOff, @Param("pageSize")Integer pageSize);
}