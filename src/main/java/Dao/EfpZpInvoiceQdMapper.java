package Dao;

import java.util.List;
import model.EfpZpInvoiceQd;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpZpInvoiceQdMapper {
    int deleteByPrimaryKey(String id);

    List<EfpZpInvoiceQd> selectAll();

    List<EfpZpInvoiceQd> selectSelective(EfpZpInvoiceQd record);

    int insertSelective(EfpZpInvoiceQd record);

    int updateByPrimaryKeySelective(EfpZpInvoiceQd record);
}