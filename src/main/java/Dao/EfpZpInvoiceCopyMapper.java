package Dao;

import java.util.List;
import model.EfpZpInvoiceCopy;
import org.springframework.stereotype.Repository;

@Repository
public interface EfpZpInvoiceCopyMapper {
    int deleteByPrimaryKey(String id);

    List<EfpZpInvoiceCopy> selectAll();

    List<EfpZpInvoiceCopy> selectSelective(EfpZpInvoiceCopy record);

    int insertSelective(EfpZpInvoiceCopy record);

    int updateByPrimaryKeySelective(EfpZpInvoiceCopy record);
}