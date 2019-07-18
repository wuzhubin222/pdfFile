package Dao;

import model.EfpJpInvoiceCopy;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EfpJpInvoiceCopyMapper {
    int deleteByPrimaryKey(String id);

    List<EfpJpInvoiceCopy> selectAll();

    List<EfpJpInvoiceCopy> selectSelective(EfpJpInvoiceCopy record);

    int insertSelective(EfpJpInvoiceCopy record);

    int updateByPrimaryKeySelective(EfpJpInvoiceCopy record);
}