package com.yxm.controller;

import com.yxm.dao.SystemDao;
import com.yxm.po.*;
import com.yxm.service.*;
import com.yxm.util.JsonMsg;
import com.yxm.util.ProjectParameter;
import com.yxm.util.Tools;
import com.yxm.vo.MenuTableTreeVo;
import com.yxm.vo.NursetTypeCost;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private static final String UPLOAD_PATH="G:/yxm/javaProjectUp/BaseAdmin/user/";
    @Autowired
    private MenuService menuService;
    @Autowired
    private CostService costService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping("/index")
    public ModelAndView index(HttpSession session){
        SysUser user = (SysUser) session.getAttribute(ProjectParameter.SESSION_USER);
        ModelAndView mv;
        if(user!=null){
            List<MenuTableTreeVo> list =menuService.selectMenuByPositionId(user.getPositionId());
            mv = new ModelAndView("/home");
            mv.addObject("loginUser",user);
            mv.addObject("menuList",list);
            return mv;
        }else {
            mv = new ModelAndView("redirect:/");
            return mv;
        }
    }

    @RequestMapping("/noAuth")
    public String noAuth(){
        return "/noAuth";
    }

    /**
     * ??????????????????????????? ???
     */
    @RequestMapping(value = "/getPortraitImage")
    public void getPortraitImage(String imgName, HttpServletResponse response) throws IOException {
        if (Tools.isNotNull(imgName)){
            //??????????????????
            String imgPath=UPLOAD_PATH+imgName;
            File fileImg=new File(imgPath);
            if (fileImg.exists()){
                //??????????????????
                response.setContentType(Tools.getImageContentType(imgName));

                InputStream in=null;
                OutputStream out=null;
                try {
                    in= new FileInputStream(fileImg);
                    out=response.getOutputStream();
                    //??????
                    // byte[] buff=new byte[1024*1024*10];//10M
                    // int count=0;
                    // while ((count=in.read(buff,0,buff.length))!=-1){
                    //     out.write(buff,0,count);
                    // }
                    //commons-io
                    IOUtils.copy(in,out);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in!=null)in.close();
                    if (out!=null)out.close();
                }

            }
        }
    }

    /**
     * ????????????????????????
     * @return
     */
    @RequestMapping(value = "/createIndent",produces = "application/json;charset=utf-8")
    @ResponseBody
    @Transactional
    public JsonMsg createIndent() throws ParseException {
        JsonMsg jsonMsg = new JsonMsg();
        //1.???????????????????????????
        List<SysElder> selectElderAll = this.costService.selectElder();
        //????????????????????????????????????10???
        Calendar c = Calendar.getInstance();//????????????????????????????????????
        int date = c.get(Calendar.DATE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        Date clearDate = new Date();
        String years =simpleDateFormat.format(clearDate);
        Integer month = clearDate.getMonth()+1;
        if(date == 11){
            //??????????????????????????????
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.roll(Calendar.DAY_OF_MONTH, -1);
            String dateString = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            Date expireDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            for (SysElder obj:selectElderAll) {
                //?????????????????????????????????
                SysAppointment sysAppointment = this.appointmentService.selectAppointmentByCheckInCode(obj.getAppointmentId());
                if(sysAppointment!=null){
                    SysCollection sysCollection = this.collectionService.selectElderOrder(obj.getId(),years);
                    if(sysCollection==null){
                        //??????????????????????????????
                        NursetTypeCost nursetTypeCost = this.costService.selectNursetTypeCost(obj.getId());
                        BigDecimal money = ((nursetTypeCost.getAlimony().add(nursetTypeCost.getBerth())).add(nursetTypeCost.getBoardWages())).add(nursetTypeCost.getServiceCharge());
                        SysCollection collection = new SysCollection();
                        collection.setClearing(null);
                        collection.setElderId(obj.getId());
                        collection.setExpire(expireDate);
                        collection.setTotalMoney(money);
                        collection.setPractical(money);
                        collection.setState((byte) 1);
                        boolean isOk = this.costService.insertElderOrder(collection);
                        if(isOk){
                            jsonMsg.setState(true);
                        }else {
                            jsonMsg.setMsg("????????????????????????");
                        }
                    }
                }
            }
        }
        return jsonMsg;
    }

    /**
     * ????????????
     * @return
     */
    @RequestMapping("/welcome")
    public String welcome(){
        return "/welcome";
    }

    @RequestMapping(value = "/selectIssueAll",produces = "application/json;charset=utf-8")
    @ResponseBody
    public List<SysIssue> selectIssueAll(){
        return this.systemService.selectIssue();
    }


}
