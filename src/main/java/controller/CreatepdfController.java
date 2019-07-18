package controller;

import entity.ConResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.pdfCreate;

import javax.servlet.http.HttpServletResponse;


@Scope("prototype")
@Controller
public class CreatepdfController extends BaseController {
    @Autowired
    private pdfCreate pdfCreates;
    //extends BaseUtils

    @ResponseBody
    @RequestMapping(value = "/invoice/pdfcreateUtil",produces = "application/json;charset=utf-8",method = RequestMethod.GET)
    public ConResult createHzs(
            HttpServletResponse response,@RequestParam(value = "fphm") String fphm,@RequestParam(value = "fpdm") String fpdm)
            throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");

       // this.logFilePath= this.getClass().getResource("/").getPath()+"log/createDZp2.txt";
        this.logOutput("发票号码 "+fphm,"发票代码 "+fpdm);
        ConResult conResult = pdfCreates.Createpdf(fphm,fpdm);
        return conResult;
    }




}
