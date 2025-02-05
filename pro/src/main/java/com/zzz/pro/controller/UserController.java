package com.zzz.pro.controller;


import com.zzz.pro.pojo.InterfaceDto.LoginDTO;
import com.zzz.pro.pojo.bo.UserBO;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.dto.UserTag;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.RegisterVO;
import com.zzz.pro.service.UserService;
import com.zzz.pro.utils.Img2Base64;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ztx
 * @date 2021-12-03 11:27
 * @description : 用户控制器
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;



//    @ApiLimit(seconds = 10,maxCount = 3)
    @PostMapping("/login")
    public SysJSONResult login(@RequestBody LoginDTO loginDTO,@RequestHeader("token") String token){

        System.out.println("执行了");
        userService.userLogin(loginDTO.getLoginParams());
        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
         String type =   loginDTO.getLoginMethod();
         switch (type){
             case "NORMAL":return userService.userLogin(loginDTO.getLoginParams());
             case "VERIFY":return userService.userLoginByToken(token);
            // case "TOKEN":loginDTO.getLoginParams().get;;break;
             default:return ResultVOUtil.error(401,"登录类型不存在");
         }

    }


    //TODO  tag 
    @PostMapping("/profile")
    public SysJSONResult profile(@RequestBody LoginDTO loginDTO,@RequestHeader("token") String token){


        //1.username可为邮箱，手机号，用户名，后端需验证username的类型
        String type =   loginDTO.getLoginMethod();
        switch (type){
            case "NORMAL":return userService.userLogin(loginDTO.getLoginParams());
            case "VERIFY":return userService.userLoginByToken(token);
            // case "TOKEN":loginDTO.getLoginParams().get;;break;
            default:return ResultVOUtil.error(401,"登录类型不存在");
        }

    }
    //验证注册码
    @PostMapping("/register/verifyPhone")
    public SysJSONResult verifyCode(@RequestBody RegisterVO registerVO){
        //1.验证用户名及密码
        UserBaseInfo u = new UserBaseInfo();
        u.setUserPhone(registerVO.getUserPhone());
        return userService.userIsExist(u);

        //2. 保存用户ID， 将ID与channelID进行绑定，提交消息引擎


    }
    @PostMapping("/editUserProfile")
    public SysJSONResult editUserProfile(@RequestBody UserPersonalInfo userPersonalInfo) {
        return userService.updateUserProfile(userPersonalInfo);
    }

    // TODO： 手机号登录 or 注册
    @PostMapping("/register")
    public SysJSONResult register(@RequestBody RegisterVO registerVO){
        if(!registerVO.getVerifyCode().equals("6666")){
            return ResultVOUtil.error(401,"验证码错误");
        }
        System.out.printf(registerVO.getUserPhone());
        System.out.println(registerVO.getPassword());
        //1.验证用户名及密码
        UserBaseInfo u = new UserBaseInfo();
        u.setUserPhone(registerVO.getUserPhone());
        u.setUserPassword(registerVO.getPassword());
        return userService.userRegister(u);

        //2. 保存用户ID， 将ID与channelID进行绑定，提交消息引擎


    }

    @PostMapping("/uploadFaceImageBig")
    @CrossOrigin(maxAge = 3699,origins = "*")
    public SysJSONResult uploadFaceImageBig(@RequestBody UserBO userBO){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userBO.getUserId());
        userPersonalInfo.setUserFaceImage(base64Data);
        userPersonalInfo.setUserFaceImageBig(base64Data);
        //TODO： 将文件上传到文件服务器
        return userService.uploadFaceImg(userPersonalInfo);

    }

    @PostMapping("/uploadFaceImageFile")
    @CrossOrigin(maxAge = 3699,origins = "*")
    public SysJSONResult uploadFaceImageFile(@RequestParam("files") MultipartFile files,HttpServletRequest request) throws IOException {
        String userId =  request.getParameter("userId");
        if(files.isEmpty()||files.getSize()==0||files.getInputStream()==null){
            return ResultVOUtil.error(401,"头像文件为空！");
        }
        String base64Data =  Img2Base64.getImageInput(files.getInputStream());
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userId);
        userPersonalInfo.setUserFaceImage(base64Data);
        userPersonalInfo.setUserFaceImageBig(base64Data);
        System.out.println("文件长度： "+files.getSize());
        return userService.uploadFaceImg(userPersonalInfo);

    }
    @PostMapping("/modifyUser")
    public SysJSONResult modifyUser(@RequestBody UserBaseInfo userBaseInfo){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        return  userService.delUser(userBaseInfo);
    }


    //测试 - 删除用户
    @PostMapping("/delUser")
    public SysJSONResult delUser(@RequestBody UserBaseInfo userBaseInfo){

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        return  userService.delUser(userBaseInfo);
    }

    @PostMapping("/updateUserTag")
    public SysJSONResult updateUserTag(@RequestBody UserTag userTag){
        userService.updateUserTag(userTag);
        return  ResultVOUtil.success();
    }

    @PostMapping("/addUserTag")
    public SysJSONResult addUserTag(@RequestBody UserTag userTag){
        userService.addUserTag(userTag);
        return  ResultVOUtil.success();
    }

    @GetMapping("/queryUserTag")
    public SysJSONResult queryUserTag(@Param("userId") String userId){
        return  ResultVOUtil.success(userService.queryUserTag(userId));
    }

    @PostMapping("/clearUserTag")
    public SysJSONResult clearUserTag(@RequestBody UserTag userTag){
        userService.clearUserTag(userTag);
        return  ResultVOUtil.success();
    }


}
