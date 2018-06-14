package com.hefvcjm.expresshelper.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * 用于管理用户信息的类
 */
public class UserInfos {

    public enum State {
        STATE_ONLINE("在线"), STATE_OFFLINE("下线"), STATE_LOGINED("已登录"), STATE_NOT_LOGIN("未登录"), STATE_UNKOWN("未知");
        private String name;

        private State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String phone = "";//手机号码
    private String name = "";//用户名
    private String email = "";//邮箱
    private String password = "";//密码
    private String lastlogin = "";//最近一次登录时间
    private String state = "";//账号状态

    private UserInfos(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Class<?> userInfos = this.getClass();
                String methodName = "set" + (key.toLowerCase().charAt(0) + "").toUpperCase(Locale.ROOT) + key.toLowerCase().substring(1);
//                System.out.println("methodName:" + methodName);
                Method method = userInfos.getMethod(methodName, String.class);
                method.invoke(this, jsonObject.getString(key));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private UserInfos() {

    }

    private static volatile UserInfos instance;

    public static UserInfos getInstance() {
        if (instance == null) {
            synchronized (UserInfos.class) {
                if (instance == null) {
                    instance = new UserInfos();
                }
            }
        }
        return instance;
    }

    public static UserInfos getInstance(String json) {
        if (instance == null) {
            synchronized (UserInfos.class) {
                if (instance == null) {
                    instance = new UserInfos(json);
                }
            }
        }
        return instance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String toString() {
        String str = "{\"phone\":\"" + getPhone() + "\"," + "\"name\":\"" + getName() + "\","
                + "\"email\":\"" + getEmail() + "\"," + "\"password\":\"" + getPassword() + "\","
                + "\"state\":\"" + getState() + "\"," + "\"lastlogin\":\"" + getLastlogin() + "\"}";
        return str;
    }
}

