package Dao;

import java.util.List;
import model.EfpJpInvoiceQd;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpJpInvoiceQdMapper {
    int deleteByPrimaryKey(String id);

    List<EfpJpInvoiceQd> selectAll();

    List<EfpJpInvoiceQd> selectSelective(EfpJpInvoiceQd record);

    int insertSelective(EfpJpInvoiceQd record);

    int updateByPrimaryKeySelective(EfpJpInvoiceQd record);
}