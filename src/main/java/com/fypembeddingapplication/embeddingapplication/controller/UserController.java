package com.fypembeddingapplication.embeddingapplication.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fypembeddingapplication.embeddingapplication.Service.ConfirmationTokenService;
import com.fypembeddingapplication.embeddingapplication.Service.UserService;
import com.fypembeddingapplication.embeddingapplication.model.ConfirmationToken;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForForgetPassword;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForSignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForSignUp;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForConfirmUser;
import com.fypembeddingapplication.embeddingapplication.responseModel.requestForChangePassword;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/sign_up")
    @ResponseBody
    public Map<String, Object> userSignUp(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForSignUp requestForSignUp =  mapper.readValue(jsonString, requestForSignUp.class);
            String email=requestForSignUp.getEmail();
            String username= requestForSignUp.getUserName();
            String password =requestForSignUp.getPassword();
            int indicator =userService.signUpUser(email,username,password);
            if (indicator==3){
                jsonBody.put("sign_up","success");
            }else if (indicator ==1){
                jsonBody.put("sign_up","fail");
                errorMessage.add("Account registered.");
            }else if(indicator ==2){
                jsonBody.put("sign_up","fail");
                errorMessage.add("You have registered the account before. Please go to your email to active the account.");
            }else if (indicator ==4){
                jsonBody.put("sign_up","fail");
                errorMessage.add("Something wrong with the Email server. Please contact us.");
            }

        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("sign_up","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);
        return jsonBody.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/confirm_user")
    @ResponseBody
    public Map<String, Object> confirmUser(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForConfirmUser requestForConfirmUser = mapper.readValue(jsonString, requestForConfirmUser.class);
            String token = requestForConfirmUser.getToken();
            ConfirmationToken confirmationToken =confirmationTokenService.findConfirmationToken(token);
            if (confirmationToken!=null){
                int indicator=userService.confirmUser(confirmationToken);
                if (indicator==1){
                    jsonBody.put("confirm_user","success");
                }else if (indicator==2){
                    jsonBody.put("confirm_user","fail");
                    errorMessage.add("Error 101. Error occur at database");
                }else{
                    exceptionMessage.add("Exception occur");
                }
            }else {
                jsonBody.put("confirm_user","fail");
                errorMessage.add("Wrong confirmation token");
            }

        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("confirm_user","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);

        return jsonBody.returmMap();
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/sign_in")
    @ResponseBody
    public Map<String, Object> signInUser(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForSignIn requestForSignIn = mapper.readValue(jsonString, requestForSignIn.class);
            String email = requestForSignIn.getEmail();
            String password = requestForSignIn.getPassword();
            int indicator = userService.signInUser(email,password);
            if (indicator ==1){
                jsonBody.put("sign_in","success");
                jsonBody.put("token",userService.getToken(email));
                jsonBody.put("id",userService.getUserId(email));
            }
            else if (indicator==2){
                jsonBody.put("sign_in","fail");
                errorMessage.add("Error . Account has not been active yet.");
            }else if (indicator==3){
                jsonBody.put("sign_in","fail");
                errorMessage.add("Error . Wrong email or password");
            }
        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("sign_in","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);
        return jsonBody.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/forget_password")
    @ResponseBody
    public Map<String, Object> forgetPassword(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForForgetPassword requestForgetPassword =  mapper.readValue(jsonString, requestForForgetPassword.class);
            String email = requestForgetPassword.getEmail();
            int indicator = userService.forgetPassword(email);
            if (indicator==1){
                jsonBody.put("forget_password","success");
            }else if (indicator==2){
                jsonBody.put("forget_password","fail");
                errorMessage.add("Error .Something wrong with the Email server. Please contact us.");
            }else if (indicator==3){
                jsonBody.put("forget_password","fail");
                errorMessage.add("Error .This email does not exist");
            }
        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("forget_password","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);
        return jsonBody.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/confirm_change_password")
    @ResponseBody
    public Map<String, Object> confirmChangePassword(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForConfirmUser requestForConfirmUser = mapper.readValue(jsonString, requestForConfirmUser.class);
            String token = requestForConfirmUser.getToken();
            ConfirmationToken confirmationToken =confirmationTokenService.findConfirmationToken(token);
            System.out.println(confirmationToken==null);
            if (confirmationToken!=null){
                int indicator=userService.confirmChangingPassword(confirmationToken);
                if (indicator==1){
                    jsonBody.put("confirm_change_password","success");
                }else if (indicator==2){
                    jsonBody.put("confirm_change_password","fail");
                    errorMessage.add("Error 101. Error occur at database");
                }else{
                    exceptionMessage.add("Exception occur");
                }

            }else {
                jsonBody.put("confirm_change_password","fail");
                errorMessage.add("Wrong confirmation token");
            }

        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("confirm_change_password","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);
        return jsonBody.returmMap();
    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/change_password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestBody String allParams) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonString=allParams;
        ArrayList<String> errorMessage = new ArrayList<>();
        ArrayList<String> exceptionMessage = new ArrayList<>();
        JsonCustomized<String,Object> jsonBody =new JsonCustomized<>();
        try {
            requestForChangePassword requestForChangePassword = mapper.readValue(jsonString, requestForChangePassword.class);
            String email = requestForChangePassword.getEmail();
            String newPassword=requestForChangePassword.getPassword();
            int indicator = userService.changePassword(email,newPassword);
            if (indicator == 1) {
                jsonBody.put("change_password","success");
            }else if (indicator==2){
                jsonBody.put("change_password","fail");
                errorMessage.add("Error . Your new password should be same as old password.");
            }else if (indicator==3){
                jsonBody.put("change_password","fail");
                errorMessage.add("Error . Something wrong with database.");
            }
        }catch (Exception e){
            exceptionMessage.add(e.getMessage());
        }
        if(errorMessage.size()!=0||exceptionMessage.size()!=0){
            jsonBody.put("change_password","fail");
        }
        jsonBody.put("exception_message",exceptionMessage);
        jsonBody.put("errorMessage",errorMessage);
        return jsonBody.returmMap();
    }

}
