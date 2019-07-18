package Dao;

import model.EfpZpInvoiceCopy;
import model.EfpZyfpInvoiceCopy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EfpZyfpInvoiceCopyMapper {

    int deleteByPrimaryKey(String id);

    List<EfpZyfpInvoiceCopy> selectAll();


    List<EfpZyfpInvoiceCopy> selectSelective(EfpZyfpInvoiceCopy record);


    int insertSelective(EfpZyfpInvoiceCopy record);


    int updateByPrimaryKeySelective(EfpZyfpInvoiceCopy record);

}