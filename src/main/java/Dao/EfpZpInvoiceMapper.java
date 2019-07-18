package Dao;

import java.util.List;
import model.EfpZpInvoice;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpZpInvoiceMapper {
    int deleteByPrimaryKey(String id);

    List<EfpZpInvoice> selectAll();

    List<EfpZpInvoice> selectSelective(EfpZpInvoice record);

    int insertSelective(EfpZpInvoice record);

    int updateByPrimaryKeySelective(EfpZpInvoice record);
}