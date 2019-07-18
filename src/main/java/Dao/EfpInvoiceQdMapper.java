package Dao;

import java.util.List;
import model.EfpInvoiceQd;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpInvoiceQdMapper {
    int deleteByPrimaryKey(String id);

    List<EfpInvoiceQd> selectAll();

    List<EfpInvoiceQd> selectSelective(EfpInvoiceQd record);

    EfpInvoiceQd selectFpInfo(EfpInvoiceQd efpInvoiceQd);

    int insertSelective(EfpInvoiceQd record);

    int updateByPrimaryKeySelective(EfpInvoiceQd record);

}