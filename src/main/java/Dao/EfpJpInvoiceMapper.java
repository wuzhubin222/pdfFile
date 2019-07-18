package Dao;

import java.util.List;
import model.EfpJpInvoice;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpJpInvoiceMapper {
    int deleteByPrimaryKey(String id);

    List<EfpJpInvoice> selectAll();

    List<EfpJpInvoice> selectSelective(EfpJpInvoice record);

    int insertSelective(EfpJpInvoice record);

    int updateByPrimaryKeySelective(EfpJpInvoice record);
}