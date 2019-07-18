package Dao;

import java.util.HashMap;
import java.util.List;

import model.Cdefpresemaintain;
import model.EfpInvoice;
import model.EfpInvoiceCopy;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpInvoiceMapper {
    int deleteByPrimaryKey(String id);

    List<EfpInvoice> selectAll();

    List<EfpInvoice> selectSelective(EfpInvoice record);

    int insertSelective(EfpInvoice record);

    int updateByPrimaryKeySelective(EfpInvoice record);

    int selectCount(String phone);



    List<String> selectCounts(String gfmc);

    EfpInvoice selectFpInfo(EfpInvoice record);

    List<HashMap<String, String>> selectLikeInvoices(@Param("gfmc") String gfmc, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    List<HashMap<String, String>> selectInvoicesList(@Param("phone") String phone, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    /**
     * 自定义sql
     */

    List<EfpInvoice> selectInvoicesByXfsh(@Param("xfsh") String xfsh, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);

    List<EfpInvoice> selectInvoicesByPhone(@Param("phone") String phone, @Param("pageOff") Integer pageOff, @Param("pageSize") Integer pageSize);


    List<HashMap<String, String>> selectSeleTest();

    List<HashMap<String, String>> selectbyhm(@Param("fphm") String fphm, @Param("fpdm") String fpdm);

    int insertRise(Cdefpresemaintain record);

    List<Cdefpresemaintain> selectRiseInfo(String gfsh);

    List<HashMap<String, Object>> selectMxall(@Param("fphm") String fphm, @Param("fpdm") String fpdm);
    List<HashMap<String, Object>>  queryNodpfAll(@Param("fphm") String fphm, @Param("fpdm") String fpdm);


}